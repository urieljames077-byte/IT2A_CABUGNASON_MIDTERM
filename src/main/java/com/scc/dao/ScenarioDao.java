package com.scc.dao;

import com.scc.model.Entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object: the ONLY place that runs SQL for patient treatments.
 * Every query uses a PreparedStatement with ? placeholders (no string joining).
 *
 * Database problems are reported as IllegalStateException so the menu
 * can show a friendly message without importing anything from java.sql.
 */
public class ScenarioDao {

    public ScenarioDao() {
        try {
            DatabaseConnection.initialize();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not prepare the database: " + e.getMessage(), e);
        }
    }

    /** Inserts a new record. The id inside the entity is ignored; the database assigns it. */
    public void add(Entity entity) {
        String sql = """
                INSERT INTO patient_treatments (patient_name, treatment, amount_due, payment)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.patientName());
            statement.setString(2, entity.treatment());
            statement.setDouble(3, entity.amountDue());
            statement.setString(4, entity.payment());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not save the record: " + e.getMessage(), e);
        }
    }

    /** Returns every record, oldest first. */
    public List<Entity> findAll() {
        String sql = """
                SELECT id, patient_name, treatment, amount_due, payment
                FROM patient_treatments
                ORDER BY id
                """;
        List<Entity> records = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                records.add(new Entity(
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("treatment"),
                        rs.getDouble("amount_due"),
                        rs.getString("payment")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not read the records: " + e.getMessage(), e);
        }
        return records;
    }
}