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
import cl.ucn.disc.arqsist.library.service.NotFoundException;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Tests the member gRPC round trip. */
final class MemberGrpcTest {

    /**
     * Registers a member, lists members, and retrieves one by ID.
     *
     * @throws Exception if the in-memory database or test server cannot start
     */
    @Test
    void registerListAndGetMember() throws Exception {
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
            Member registered = client.register(new Member("Ada Lovelace", "ada@example.com"));
            assertEquals("Ada Lovelace", registered.getName());

            var members = client.findAll();
            assertEquals(1, members.size());
            assertEquals(registered.getId(), members.get(0).getId());

            Member found = client.findById(registered.getId());
            assertEquals("ada@example.com", found.getEmail());
            assertThrows(NotFoundException.class, () -> client.findById(9999));
        } finally {
            server.shutdownNow();
        }
    }
}
