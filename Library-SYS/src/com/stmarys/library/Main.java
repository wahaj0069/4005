package com.stmarys.library;

import javax.swing.SwingUtilities;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Developed by Wahaj");
        System.out.println("   ST MARY'S LIBRARY MANAGEMENT SYSTEM     ");
        System.out.println("Welcome");
        System.out.println("Select Interface Mode:");
        System.out.println("1) Text Based Console ");
        System.out.println("2) Graphical User Interface");
        System.out.print("\nEnter choice (1-2): ");

        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            ConsoleInterface ui = new ConsoleInterface();
            ui.start();
        } else if (choice.equals("2")) {
            SwingUtilities.invokeLater(() -> {
                new MainFrame().setVisible(true);
            });
        } else {
            System.out.println("Invalid selection, Exiting system.");
        }
    }
}