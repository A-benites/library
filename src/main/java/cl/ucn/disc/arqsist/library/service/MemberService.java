/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

import cl.ucn.disc.arqsist.library.dao.MemberDao;
import cl.ucn.disc.arqsist.library.model.Member;

import java.util.List;

/**
 * Application service for {@link Member} management.
 *
 * <p>Handles member registration and retrieval.</p>
 */
public final class MemberService {

    /** DAO for member persistence. */
    private final MemberDao memberDao;

    /**
     * Creates a new {@code MemberService}.
     *
     * @param memberDao the member DAO; must not be {@code null}
     */
    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    /**
     * Registers a new member in the system.
     *
     * @param member the member to register; must not be {@code null}
     * @return the persisted member with its generated ID
     */
    public Member register(Member member) {
        memberDao.create(member);
        return member;
    }

    /**
     * Returns all registered members.
     *
     * @return a list of all members; never {@code null}
     */
    public List<Member> findAll() {
        return memberDao.findAll();
    }

    /**
     * Finds a member by ID.
     *
     * @param id the member ID
     * @return the member
     * @throws NotFoundException if no member has the given ID
     */
    public Member findById(int id) {
        Member member = memberDao.findById(id);
        if (member == null) {
            throw new NotFoundException("Member not found: " + id);
        }
        return member;
    }
}
