/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.grpc;

import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.NotFoundException;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.List;
import java.util.concurrent.TimeUnit;

/** Blocking client for the member gRPC service. */
public final class MemberClient implements AutoCloseable {

    /** Maximum time for one RPC, in seconds. */
    private static final long DEADLINE_SECONDS = 5;

    /** Connection to the gRPC server. */
    private final ManagedChannel channel;

    /** Blocking RPC stub for the member service. */
    private final MemberServiceGrpc.MemberServiceBlockingStub stub;

    /**
     * Connects to a server using the lab's plaintext transport.
     *
     * @param host the server host
     * @param port the server port
     */
    public MemberClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext().build());
    }

    /**
     * Creates a client on an existing channel.
     *
     * @param channel the channel to the server
     */
    public MemberClient(ManagedChannel channel) {
        this.channel = channel;
        this.stub = MemberServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Registers a member.
     *
     * @param member the member to register
     * @return the persisted member
     */
    public Member register(Member member) {
        try {
            MemberMessage reply = stub.withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                    .registerMember(RegisterMemberRequest.newBuilder()
                            .setName(member.getName())
                            .setEmail(member.getEmail())
                            .build());
            return toMember(reply);
        } catch (StatusRuntimeException e) {
            throw toException(e);
        }
    }

    /**
     * Returns all members.
     *
     * @return all members returned by the server
     */
    public List<Member> findAll() {
        try {
            ListMembersReply reply = stub.withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                    .listMembers(ListMembersRequest.getDefaultInstance());
            return reply.getMembersList().stream().map(MemberClient::toMember).toList();
        } catch (StatusRuntimeException e) {
            throw toException(e);
        }
    }

    /**
     * Finds a member by ID.
     *
     * @param id the member ID
     * @return the requested member
     * @throws NotFoundException if no member has the given ID
     */
    public Member findById(int id) {
        try {
            MemberMessage reply = stub.withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                    .getMember(GetMemberRequest.newBuilder().setId(id).build());
            return toMember(reply);
        } catch (StatusRuntimeException e) {
            throw toException(e);
        }
    }

    /**
     * Searches members by a name fragment.
     *
     * @param query the name fragment to find
     * @return matching members ordered by ID
     * @throws IllegalArgumentException if the query is blank
     */
    public List<Member> search(String query) {
        try {
            ListMembersReply reply = stub.withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                    .searchMembers(SearchMembersRequest.newBuilder().setQuery(query).build());
            return reply.getMembersList().stream().map(MemberClient::toMember).toList();
        } catch (StatusRuntimeException e) {
            throw toException(e);
        }
    }

    /** Closes the channel after allowing active calls to finish. */
    @Override
    public void close() {
        channel.shutdown();
        try {
            if (!channel.awaitTermination(DEADLINE_SECONDS, TimeUnit.SECONDS)) {
                channel.shutdownNow();
            }
        } catch (InterruptedException e) {
            channel.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /** Converts a gRPC status into the matching domain exception. */
    private static RuntimeException toException(StatusRuntimeException e) {
        String description = e.getStatus().getDescription();
        if (description == null) {
            description = e.getStatus().getCode().name();
        }
        return switch (e.getStatus().getCode()) {
            case NOT_FOUND -> new NotFoundException(description);
            case INVALID_ARGUMENT -> new IllegalArgumentException(description);
            case FAILED_PRECONDITION -> new IllegalStateException(description);
            default -> e;
        };
    }

    /** Converts a protobuf member into the domain model. */
    private static Member toMember(MemberMessage message) {
        Member member = new Member(message.getName(), message.getEmail());
        member.setId(message.getId());
        return member;
    }
}
