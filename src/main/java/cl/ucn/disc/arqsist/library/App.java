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
import cl.ucn.disc.arqsist.library.service.BookService;
import cl.ucn.disc.arqsist.library.service.LoanService;
import cl.ucn.disc.arqsist.library.service.MemberService;
import cl.ucn.disc.arqsist.library.service.ReservationService;
import cl.ucn.disc.arqsist.library.service.NotFoundException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.json.JavalinJackson;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Map;

public final class App {

    public static void main(String[] args) throws Exception {
        Database db = new Database("jdbc:sqlite:database.db");
        db.seedIfEmpty();

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

        BookController bookController = new BookController(bookService);
        MemberController memberController = new MemberController(memberService);
        LoanController loanController = new LoanController(loanService);
        ReservationController reservationController = new ReservationController(reservationService);

        Javalin.create(config -> {
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
        }).start(7070);
    }
}
