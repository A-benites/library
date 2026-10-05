/*
 * Copyright (c) 2026. Arquitectura de Sistemas, DISC, UCN, Antofagasta.
 */
package cl.ucn.disc.arqsist.library.service;

/**
 * Thrown when a requested entity does not exist in the persistence layer.
 * <p>Maps to HTTP 404 Not Found at the application boundary.</p>
 */
public final class NotFoundException extends RuntimeException {

    /**
     * Creates a new {@code NotFoundException} with the given detail message.
     *
     * @param message human-readable description of the missing entity
     */
    public NotFoundException(String message) {
        super(message);
    }
}
