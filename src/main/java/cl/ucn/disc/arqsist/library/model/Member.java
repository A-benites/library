/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** A library member who can borrow and reserve books. */
@DatabaseTable(tableName = "members")
public final class Member {

    /** Auto-generated primary key. */
    @DatabaseField(generatedId = true)
    private int id;

    /** Member's full name. */
    @DatabaseField(canBeNull = false)
    private String name;

    /** Member's email address. */
    @DatabaseField(canBeNull = false)
    private String email;

    /** No-argument constructor required by ORMLite. */
    public Member() {
    }

    /**
     * Creates a member with a name and email address.
     *
     * @param name the member's full name
     * @param email the member's email address
     */
    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /** @return the generated member ID */
    public int getId() {
        return id;
    }

    /** @param id the generated member ID */
    public void setId(int id) {
        this.id = id;
    }

    /** @return the member's full name */
    public String getName() {
        return name;
    }

    /** @param name the member's full name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the member's email address */
    public String getEmail() {
        return email;
    }

    /** @param email the member's email address */
    public void setEmail(String email) {
        this.email = email;
    }
}
