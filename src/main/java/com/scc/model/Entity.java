package com.scc.model;

/**
 * One dental treatment record (5 attributes).
 *
 * A record is a short way to write a class that only holds data.
 * Java automatically gives it a constructor, getters (id(), patientName() ...),
 * toString(), equals() and hashCode().
 *
 * id = 0 means "not saved yet". The database assigns the real id (1, 2, 3 ...).
 */
public record Entity(
        int id,
        String patientName,
        String treatment,    // CLEANING, EXTRACTION or FILLING
        double amountDue,    // service fee, shown with 2 decimals
        String payment       // PAID or PENDING
) {
}
