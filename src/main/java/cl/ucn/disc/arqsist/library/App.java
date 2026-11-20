/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.controller.BookController;
import cl.ucn.disc.arqsist.library.controller.LoanController;
import cl.ucn.disc.arqsist.library.controller.MemberController;
import cl.ucn.disc.arqsist.library.controller.ReservationController;
import cl.ucn.disc.arqsist.library.dao.BookDao;
import cl.ucn.disc.arqsist.library.dao.LoanDao;
import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.dao.ReservationDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.grpc.MemberGrpcService;
import cl.ucn.disc.arqsist.library.service.BookService;
import cl.ucn.disc.arqsist.library.service.LoanService;
import cl.ucn.disc.arqsist.library.service.MemberService;
import cl.ucn.disc.arqsist.library.service.ReservationService;
import cl.ucn.disc.arqsist.library.service.NotFoundException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.javalin.json.JavalinJackson;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** Application entry point and manual dependency-wiring root. */
public final class App {

    /** Logger for startup and configuration events. */
    private static final Logger log = LoggerFactory.getLogger(App.class);

    /** Prevents construction of the application entry-point class. */
    private App() {
    }

    /**
     * Initializes persistence, wires the application, and starts the HTTP server.
     *
     * @param args command-line arguments (currently unused)
     * @throws Exception if database initialization or server startup fails
     */
    public static void main(String[] args) throws Exception {
        Database db = new Database("jdbc:sqlite:database.db");
        db.seedIfEmpty();
        log.info("Database initialized");

        BookDao bookDao = new BookDao(db.connectionSource());
        MemberDao memberDao = new MemberDao(db.connectionSource());
        LoanDao loanDao = new LoanDao(db.connectionSource());
        ReservationDao reservationDao = new ReservationDao(db.connectionSource());

        Clock clock = Clock.system(ZoneId.of("America/Santiago"));
        MemberService memberService = new MemberService(memberDao);
        BookService bookService = new BookService(bookDao);
        LoanService loanService = new LoanService(loanDao, memberDao, bookService, clock);
        ReservationService reservationService = new ReservationService(
                reservationDao, memberDao, loanDao, bookService, clock);

        log.info("starting the member gRPC server on {}:{}", MemberGrpcService.HOST, MemberGrpcService.PORT);
        Server memberServer = NettyServerBuilder
                .forAddress(new InetSocketAddress(MemberGrpcService.HOST, MemberGrpcService.PORT))
                .addService(new MemberGrpcService(memberService))
                .build()
                .start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("stopping the gRPC server");
            stop(memberServer);
        }));

        BookController bookController = new BookController(bookService);
        MemberController memberController = new MemberController(memberService);
        LoanController loanController = new LoanController(loanService);
        ReservationController reservationController = new ReservationController(reservationService);

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper ->
                    mapper.registerModule(new JavaTimeModule())
                            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)));
            config.routes.exception(NotFoundException.class, (e, ctx) ->
                    ctx.status(404).json(Map.of("error", e.getMessage())));
            config.routes.exception(IllegalArgumentException.class, (e, ctx) ->
                    ctx.status(400).json(Map.of("error", e.getMessage())));
            config.routes.exception(IllegalStateException.class, (e, ctx) ->
                    ctx.status(409).json(Map.of("error", e.getMessage())));
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });
            bookController.register(config);
            memberController.register(config);
            loanController.register(config);
            reservationController.register(config);
        });
        log.info("starting the web server on port 7070");
        app.start(7070);
        log.info("Library application started on port 7070");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("stopping the web server");
            app.stop();
        }));
    }

    /** Stops a gRPC server, allowing in-flight calls to finish. */
    private static void stop(Server server) {
        server.shutdown();
        try {
            if (!server.awaitTermination(5, TimeUnit.SECONDS)) {
                server.shutdownNow();
            }
        } catch (InterruptedException e) {
            server.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
