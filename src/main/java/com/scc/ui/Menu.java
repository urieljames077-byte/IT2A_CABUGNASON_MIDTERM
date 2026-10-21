package com.scc.ui;

import com.scc.dao.ScenarioDao;
import com.scc.model.Entity;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Console menu. It only talks to ScenarioDao.
 * This class contains no database code at all.
 */
public class Menu {

    private static final int MAX_NAME_LENGTH = 24;
    private static final double MAX_AMOUNT = 9_999_999.99;

    private final Scanner scanner = new Scanner(System.in);
    private final ScenarioDao dao;

    public Menu() {
        // Use "." as the decimal point whatever the computer's language setting is.
        scanner.useLocale(Locale.US);
        this.dao = new ScenarioDao();
    }

    /** The menu loop: keeps showing options until the user chooses Exit. */
    public void start() {
        boolean running = true;
        while (running) {
            showMenu();
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> recordTreatment();
                case 2 -> viewRecords();
                case 3 -> {
                    System.out.println("\nExiting system. Goodbye!");
                    running = false;
                }
                default -> System.out.println("\n[!] Invalid choice. Please enter 1, 2 or 3.");
            }
        }
        scanner.close();
    }

    private void showMenu() {
        System.out.println();
        System.out.println("==============================================");
        System.out.println("         DENTAL CLINIC TREATMENT LOG");
        System.out.println("==============================================");
        System.out.println("[1] Record Patient Treatment");
        System.out.println("[2] View Patient Records");
        System.out.println("[3] Exit System");
        System.out.println("----------------------------------------------");
    }

    // ---------------------------------------------------------------- [1] Add

    private void recordTreatment() {
        System.out.println("\n--- RECORD PATIENT TREATMENT ---");

        String name = readName("Enter Patient Name: ");
        String treatment = readChoice("Enter Treatment (CLEANING / EXTRACTION / FILLING): ",
                "CLEANING", "EXTRACTION", "FILLING");
        double amount = readAmount("Enter Amount Due: ");
        String payment = readChoice("Enter Payment Status (PAID / PENDING): ", "PAID", "PENDING");

        try {
            // id 0 = "not saved yet"; the database gives the real id
            dao.add(new Entity(0, name, treatment, amount, payment));
            System.out.println("\nSUCCESS: Patient treatment recorded!");
        } catch (IllegalStateException e) {
            System.out.println("\n[!] " + e.getMessage());
        }
    }

    // --------------------------------------------------------------- [2] View

    private void viewRecords() {
        List<Entity> records;
        try {
            records = dao.findAll();
        } catch (IllegalStateException e) {
            System.out.println("\n[!] " + e.getMessage());
            return;
        }

        String border = "+------+--------------------------+--------------+--------------+-----------+";
        System.out.println();
        System.out.println("===============================================================================");
        System.out.println("                            DENTAL PATIENT RECORDS");
        System.out.println("===============================================================================");

        if (records.isEmpty()) {
            System.out.println("No patient records found yet.");
            return;
        }

        System.out.println(border);
        System.out.printf("| %-4s | %-24s | %-12s | %-12s | %-9s |%n",
                "ID", "PATIENT NAME", "TREATMENT", "AMOUNT DUE", "PAYMENT");
        System.out.println(border);
        for (Entity r : records) {
            System.out.printf(Locale.US, "| %-4d | %-24s | %-12s | %-12.2f | %-9s |%n",
                    r.id(), r.patientName(), r.treatment(), r.amountDue(), r.payment());
        }
        System.out.println(border);
        System.out.println("Total Records Displayed: " + records.size());
    }

    // ------------------------------------------------------- input handling
    // Mixing nextInt()/nextDouble() with nextLine():
    // nextInt() and nextDouble() read the number but leave the Enter key
    // (the newline) in the input. If the next call is nextLine(), it would read
    // that leftover newline as an empty line. So right after every number read we
    // call scanner.nextLine() once to throw the leftover newline away.

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            int value = scanner.nextInt();
            scanner.nextLine();                 // consume the leftover newline
            return value;
        } catch (InputMismatchException e) {
            scanner.nextLine();                 // throw away the bad text
            return -1;                          // -1 shows "Invalid choice" in the menu
        } catch (NoSuchElementException e) {
            return 3;                           // input closed: leave the program safely
        }
    }

    private double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = scanner.nextDouble();
                scanner.nextLine();             // consume the leftover newline
                if (value > 0 && value <= MAX_AMOUNT) {
                    return value;
                }
                System.out.println("[!] Amount must be more than 0 and not above 9,999,999.99.");
            } catch (InputMismatchException e) {
                scanner.nextLine();             // throw away the bad text
                System.out.println("[!] Please enter a number such as 1500.00.");
            }
        }
    }

    private String readName(String prompt) {
        while (true) {
            System.out.print(prompt);
            String name = scanner.nextLine().trim();
            if (!name.isEmpty() && name.length() <= MAX_NAME_LENGTH) {
                return name;
            }
            System.out.println("[!] Name must be 1 to " + MAX_NAME_LENGTH + " characters.");
        }
    }

    /** Keeps asking until the user types one of the allowed words (any letter case). */
    private String readChoice(String prompt, String... allowed) {
        while (true) {
            System.out.print(prompt);
            String text = scanner.nextLine().trim().toUpperCase();
            for (String option : allowed) {
                if (option.equals(text)) {
                    return option;
                }
            }
            System.out.println("[!] Please type one of: " + String.join(" / ", allowed));
        }
    }
}
