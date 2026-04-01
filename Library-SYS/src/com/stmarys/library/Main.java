package com.stmarys.library;

import javax.swing.SwingUtilities;

/**
 * The main entry point for the Library Management System application..
 */
public class Main {

    public static void main(String[] args) {
        // This is the standard, safe way to start a Swing GUI application.
        // It ensures that all UI components are created and updated on the
        // correct thread, preventing potential bugs and freezes.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Creates an instance of your main window and makes it visible.
                new MainFrame().setVisible(true);
            }
        });
    }
}