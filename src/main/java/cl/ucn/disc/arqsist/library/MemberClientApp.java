/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.grpc.MemberClient;
import cl.ucn.disc.arqsist.library.grpc.MemberGrpcService;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.NotFoundException;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Command-line entry point for the member gRPC client. */
public final class MemberClientApp {

    /** Logger for command-line results. */
    private static final Logger log = LoggerFactory.getLogger(MemberClientApp.class);

    /** Prevents construction of the command-line entry-point class. */
    private MemberClientApp() {
    }

    /**
     * Lists all members and then finds members with the supplied IDs.
     *
     * @param args member IDs to retrieve
     */
    public static void main(String[] args) {
        try (MemberClient client = new MemberClient(MemberGrpcService.HOST, MemberGrpcService.PORT)) {
            for (Member member : client.findAll()) {
                log.info("member {}: {} <{}>", member.getId(), member.getName(), member.getEmail());
            }
            for (String arg : args) {
                Member member = client.findById(Integer.parseInt(arg));
                log.info("member {}: {} <{}>", member.getId(), member.getName(), member.getEmail());
            }
        } catch (NotFoundException e) {
            log.error("member not found: {}", e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            log.error("invalid argument: {}", e.getMessage());
            System.exit(1);
        } catch (StatusRuntimeException e) {
            log.error("the gRPC call failed: {}", e.getStatus());
            System.exit(1);
        }
    }
}
