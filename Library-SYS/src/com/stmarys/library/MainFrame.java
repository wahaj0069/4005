package com.stmarys.library;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("St Mary's Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon icon = new ImageIcon("logo.png");
        setIconImage(icon.getImage());

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel bookPanel = new BookPanel();
        JPanel memberPanel = new MemberPanel();
        JPanel borrowPanel = new BorrowPanel();

        tabbedPane.addTab("Books", bookPanel);
        tabbedPane.addTab("Members", memberPanel);
        tabbedPane.addTab("Borrowing", borrowPanel);

        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        
        setJMenuBar(menuBar);
    }
}