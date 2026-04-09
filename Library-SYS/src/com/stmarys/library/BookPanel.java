package com.stmarys.library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class BookPanel extends JPanel {

    private JTable bookTable;
    private DefaultTableModel tableModel;
    private BookDAO bookDAO;
    private List<Book> currentBookList;
    private List<Book> originalOrderList;

    private JTextField searchField;
    private JComboBox<String> sortComboBox;

    public BookPanel() {
        bookDAO = new BookDAO();
        currentBookList = new ArrayList<>();
        originalOrderList = new ArrayList<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- TOP PANEL FOR SEARCH, SORT, AND LOGO ---
        JPanel topPanel = new JPanel(new BorderLayout());

        // This sub-panel groups the search and sort controls together
        JPanel controlsPanel = new JPanel(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear Search");
        searchPanel.add(new JLabel("Search by Title or Author:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);
        
        // Sort panel
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        String[] sortOptions = {
            "Default Order", 
            "Sort by Title (A-Z)", 
            "Sort by Title (Z-A)",
            "Sort by Author (A-Z)", 
            "Sort by Author (Z-A)"
        };
        sortComboBox = new JComboBox<>(sortOptions);
        sortPanel.add(new JLabel("Sort:"));
        sortPanel.add(sortComboBox);

        // Add search and sort to the grouped controls panel
        // **FIX 1: Changed searchPanel from WEST to CENTER**
        controlsPanel.add(searchPanel, BorderLayout.CENTER);
        controlsPanel.add(sortPanel, BorderLayout.EAST);

        // Logo
        ImageIcon originalIcon = new ImageIcon("newlogo.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(-1, 60, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Add the grouped controls and the logo to the main top panel
        topPanel.add(controlsPanel, BorderLayout.CENTER);
        topPanel.add(logoLabel, BorderLayout.EAST);

        // --- CENTER TABLE ---
        String[] columnNames = {"ID", "Title", "Author", "Category", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // --- BOTTOM PANEL FOR ACTIONS & DEVELOPER CREDIT ---
        JPanel bottomPanel = new JPanel(new BorderLayout()); 

        JLabel creditLabel = new JLabel("Developed by: Wahaj Ibne Zahid");
        creditLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        creditLabel.setForeground(Color.DARK_GRAY);
        creditLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Book");
        JButton deleteButton = new JButton("Delete Selected");
        JButton updateButton = new JButton("Update Selected");
        bottomButtonPanel.add(addButton);
        bottomButtonPanel.add(deleteButton);
        bottomButtonPanel.add(updateButton);
        
        // **FIX 2: Changed creditLabel from WEST to CENTER**
        bottomPanel.add(creditLabel, BorderLayout.CENTER);
        bottomPanel.add(bottomButtonPanel, BorderLayout.EAST);

        // --- ADD PANELS TO LAYOUT ---
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- ACTION LISTENERS ---
        loadAllBooks();
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadAllBooks();
        });
        sortComboBox.addActionListener(e -> performSort());
        
        addButton.addActionListener(e -> {
            AddBookDialog dialog = new AddBookDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                try {
                    bookDAO.addBook(dialog.getBookId(), dialog.getBookTitle(), dialog.getAuthor(), dialog.getCategory(), dialog.getStatus());
                    loadAllBooks(); 
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error adding book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = bookTable.convertRowIndexToModel(selectedRow);
                int bookId = (int) tableModel.getValueAt(modelRow, 0);
                 if (JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try {
                        bookDAO.deleteBook(bookId);
                        loadAllBooks(); 
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting book: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a book to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = bookTable.convertRowIndexToModel(selectedRow);
            int bookIdToUpdate = (int) tableModel.getValueAt(modelRow, 0);
            
            Optional<Book> bookToUpdateOpt = currentBookList.stream().filter(book -> book.getBookId() == bookIdToUpdate).findFirst();

            if (bookToUpdateOpt.isPresent()) {
                Book bookToUpdate = bookToUpdateOpt.get();
                AddBookDialog dialog = new AddBookDialog((Frame) SwingUtilities.getWindowAncestor(this), bookToUpdate);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    Book updatedBook = new Book(dialog.getBookId(), dialog.getBookTitle(), dialog.getAuthor(), dialog.getCategory(), dialog.getStatus());
                    try {
                        bookDAO.updateBook(updatedBook);
                        loadAllBooks(); 
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error updating book: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
    
    // (The rest of the methods are unchanged)
    private void performSort() {
        String selectedSort = (String) sortComboBox.getSelectedItem();
        if (selectedSort == null) return;
        switch (selectedSort) {
            case "Sort by Title (A-Z)": currentBookList.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER)); break;
            case "Sort by Title (Z-A)": currentBookList.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER).reversed()); break;
            case "Sort by Author (A-Z)": currentBookList.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER)); break;
            case "Sort by Author (Z-A)": currentBookList.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER).reversed()); break;
            default: currentBookList = new ArrayList<>(originalOrderList); break;
        }
        updateTable();
    }
    private void performSearch() {
        try {
            String searchTerm = searchField.getText();
            if(searchTerm == null || searchTerm.trim().isEmpty()) { loadAllBooks(); return; }
            this.currentBookList = bookDAO.searchBooks(searchTerm);
            this.originalOrderList = new ArrayList<>(this.currentBookList);
            sortComboBox.setSelectedIndex(0);
            updateTable();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error during search: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); }
    }
    private void loadAllBooks() {
        try {
            this.currentBookList = bookDAO.listBooks();
            this.originalOrderList = new ArrayList<>(this.currentBookList);
            sortComboBox.setSelectedIndex(0);
            updateTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            this.currentBookList = new ArrayList<>(); 
            this.originalOrderList = new ArrayList<>();
        }
    }
    private void updateTable() {
        tableModel.setRowCount(0); 
        if (currentBookList != null) {
            for (Book book : currentBookList) {
                tableModel.addRow(new Object[]{book.getBookId(), book.getTitle(), book.getAuthor(), book.getCategory(), book.getAvailabilityStatus()});
            }
        }
    }
}