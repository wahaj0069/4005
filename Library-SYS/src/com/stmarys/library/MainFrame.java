package com.stmarys.library;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("St Mary's Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Set the window icon
        ImageIcon icon = new ImageIcon("logo.png"); // Make sure logo.png is in your project's root folder
        setIconImage(icon.getImage());

        // Create the JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Create instances of our panels
        JPanel bookPanel = new BookPanel(); // Changed to JPanel for now
        JPanel memberPanel = new MemberPanel();
        JPanel borrowPanel = new BorrowPanel();

        // Add panels as tabs
        tabbedPane.addTab("Books", bookPanel);
        tabbedPane.addTab("Members", memberPanel);
        tabbedPane.addTab("Borrowing", borrowPanel);

        // Add the tabbed pane to the frame's content area
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // --- Menu bar ---
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);
        // We can remove the other menus for now as tabs are the main navigation
        setJMenuBar(menuBar);
    }
}