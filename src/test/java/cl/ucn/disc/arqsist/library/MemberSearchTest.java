/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library;

import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.db.Database;
import cl.ucn.disc.arqsist.library.grpc.MemberClient;
import cl.ucn.disc.arqsist.library.grpc.MemberGrpcService;
import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.MemberService;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests the member search behavior across the gRPC boundary. */
final class MemberSearchTest {

    /**
     * Verifies case handling, trimming, ordering, literal wildcards, and blank input.
     *
     * @throws Exception if the in-memory database or test server cannot start
     */
    @Test
    void searchesMembersByLiteralCaseInsensitiveNameFragment() throws Exception {
        Database db = new Database("jdbc:sqlite::memory:");
        MemberService memberService = new MemberService(new MemberDao(db.connectionSource()));

        String serverName = InProcessServerBuilder.generateName();
        Server server = InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new MemberGrpcService(memberService))
                .build()
                .start();

        ManagedChannel channel = InProcessChannelBuilder.forName(serverName).directExecutor().build();
        try (MemberClient client = new MemberClient(channel)) {
            client.register(new Member("Ada Lovelace", "ada@example.com"));
            client.register(new Member("Grace Hopper", "grace@example.com"));
            client.register(new Member("Alan Turing", "alan@example.com"));

            assertEquals(List.of("Ada Lovelace"), names(client.search("ada")));
            assertEquals(List.of("Ada Lovelace"), names(client.search("ADA")));
            assertEquals(List.of("Ada Lovelace"), names(client.search(" ada ")));
            assertEquals(List.of("Ada Lovelace", "Grace Hopper", "Alan Turing"), names(client.search("a")));
            assertEquals(List.of(), client.search("zzz"));
            assertEquals(List.of(), client.search("%"));
            assertEquals(List.of(), client.search("_"));
            assertThrows(IllegalArgumentException.class, () -> client.search(""));
            assertThrows(IllegalArgumentException.class, () -> client.search("   "));
        } finally {
            server.shutdownNow();
        }
    }

    /** Returns member names in the order supplied by the service. */
    private static List<String> names(List<Member> members) {
        return members.stream().map(Member::getName).toList();
    }
}
