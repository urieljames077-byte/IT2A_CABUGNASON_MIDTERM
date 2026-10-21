package com.scc;

import com.scc.ui.Menu;

/** Application entry point. It only starts the menu. */
public class Main {

    public static void main(String[] args) {
        try {
            new Menu().start();
        } catch (IllegalStateException e) {
            // e.g. the database file cannot be opened
            System.out.println("[!] " + e.getMessage());
        }
    }
}
