/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.grpc;

import cl.ucn.disc.arqsist.library.model.Member;
import cl.ucn.disc.arqsist.library.service.MemberService;
import cl.ucn.disc.arqsist.library.service.NotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import java.util.List;

/** gRPC adapter for the member service. */
public final class MemberGrpcService extends MemberServiceGrpc.MemberServiceImplBase {

    /** Address used by the gRPC server and client. */
    public static final String HOST = "127.0.0.1";

    /** Port used by the gRPC server and client. */
    public static final int PORT = 50051;

    /** Member domain service. */
    private final MemberService memberService;

    /**
     * Creates the adapter around the existing member service.
     *
     * @param memberService the service that owns member operations
     */
    public MemberGrpcService(MemberService memberService) {
        this.memberService = memberService;
    }

    /** Registers a member and returns the persisted member. */
    @Override
    public void registerMember(RegisterMemberRequest request, StreamObserver<MemberMessage> observer) {
        try {
            Member member = memberService.register(new Member(request.getName(), request.getEmail()));
            observer.onNext(toMessage(member));
            observer.onCompleted();
        } catch (RuntimeException e) {
            observer.onError(toError(e));
        }
    }

    /** Lists all registered members. */
    @Override
    public void listMembers(ListMembersRequest request, StreamObserver<ListMembersReply> observer) {
        try {
            List<MemberMessage> members = memberService.findAll().stream()
                    .map(MemberGrpcService::toMessage)
                    .toList();
            observer.onNext(ListMembersReply.newBuilder().addAllMembers(members).build());
            observer.onCompleted();
        } catch (RuntimeException e) {
            observer.onError(toError(e));
        }
    }

    /** Finds one member by ID. */
    @Override
    public void getMember(GetMemberRequest request, StreamObserver<MemberMessage> observer) {
        try {
            observer.onNext(toMessage(memberService.findById(request.getId())));
            observer.onCompleted();
        } catch (RuntimeException e) {
            observer.onError(toError(e));
        }
    }

    /** Searches members by a name fragment. */
    @Override
    public void searchMembers(SearchMembersRequest request, StreamObserver<ListMembersReply> observer) {
        try {
            List<MemberMessage> members = memberService.search(request.getQuery()).stream()
                    .map(MemberGrpcService::toMessage)
                    .toList();
            observer.onNext(ListMembersReply.newBuilder().addAllMembers(members).build());
            observer.onCompleted();
        } catch (RuntimeException e) {
            observer.onError(toError(e));
        }
    }

    /** Maps a domain exception to its gRPC status. */
    private static StatusRuntimeException toError(RuntimeException e) {
        Status status = Status.INTERNAL;
        if (e instanceof NotFoundException) {
            status = Status.NOT_FOUND;
        } else if (e instanceof IllegalArgumentException) {
            status = Status.INVALID_ARGUMENT;
        } else if (e instanceof IllegalStateException) {
            status = Status.FAILED_PRECONDITION;
        }
        return status.withDescription(e.getMessage()).withCause(e).asRuntimeException();
    }

    /** Converts a domain member to the wire message. */
    private static MemberMessage toMessage(Member member) {
        return MemberMessage.newBuilder()
                .setId(member.getId())
                .setName(member.getName())
                .setEmail(member.getEmail())
                .build();
    }
}
