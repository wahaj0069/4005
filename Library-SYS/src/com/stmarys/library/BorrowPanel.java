package com.stmarys.library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class BorrowPanel extends JPanel {

    private final BorrowDAO borrowDAO;
    private final BookDAO bookDAO;
    private final MemberDAO memberDAO;
    private final JTable borrowingTable;
    private final DefaultTableModel tableModel;
    private final JComboBox<Book> availableBooksComboBox;
    private final JComboBox<Member> membersComboBox;
    private final JTextField searchField;
    private final JComboBox<String> sortComboBox;
    private final JTextField borrowDateTextField;
    private final JTextField dueDateTextField;
    
    private final JLabel statusLabel;
    private final JButton overdueButton;
    
    private List<Borrowing> currentBorrowings;
    private List<Borrowing> originalOrderList;

    public BorrowPanel() {
        this.borrowDAO = new BorrowDAO();
        this.bookDAO = new BookDAO();
        this.memberDAO = new MemberDAO();
        this.currentBorrowings = new ArrayList<>();
        this.originalOrderList = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("New Borrowing"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        availableBooksComboBox = new JComboBox<>();
        membersComboBox = new JComboBox<>();
        borrowDateTextField = new JTextField();
        dueDateTextField = new JTextField();
        setupComboBoxRenderers();
        JButton borrowButton = new JButton("Borrow Selected Book");
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; topPanel.add(new JLabel("Select Member:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; topPanel.add(membersComboBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; topPanel.add(new JLabel("Select Book (Available):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; topPanel.add(availableBooksComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 2; topPanel.add(new JLabel("Borrow Date (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; topPanel.add(borrowDateTextField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; topPanel.add(new JLabel("Due Date (dd-MM-yyyy):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; topPanel.add(dueDateTextField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; topPanel.add(borrowButton, gbc);

        ImageIcon originalIcon = new ImageIcon("newlogo.png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(-1, 120, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel logoLabel = new JLabel(scaledIcon);

        gbc.gridx = 2;      
        gbc.gridy = 0;      
        gbc.gridheight = 5; 
        gbc.insets = new Insets(5, 20, 5, 5); 
        topPanel.add(logoLabel, gbc);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Currently Borrowed Books"));
        
        JPanel searchSortPanel = new JPanel(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");
        JButton clearSearchButton = new JButton("Clear");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearchButton);
        
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        String[] sortOptions = { "Default Order", "Sort by Book Title (A-Z)", "Sort by Book Title (Z-A)", "Sort by Member Name (A-Z)", "Sort by Member Name (Z-A)" };
        sortComboBox = new JComboBox<>(sortOptions);
        sortPanel.add(new JLabel("Sort:"));
        sortPanel.add(sortComboBox);
        
        searchSortPanel.add(searchPanel, BorderLayout.WEST);
        searchSortPanel.add(sortPanel, BorderLayout.EAST);
        
        String[] columnNames = {"Borrow ID", "Book Title", "Member Name", "Borrow Date", "Due Date"};
        tableModel = new DefaultTableModel(columnNames, 0) { @Override public boolean isCellEditable(int row, int column) { return false; } };
        borrowingTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(borrowingTable);
        centerPanel.add(searchSortPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout()); 
        
        JButton returnButton = new JButton("Return Selected Book");
        overdueButton = new JButton(" Show Overdue Books");
        statusLabel = new JLabel(" Status: Ready");
        statusLabel.setForeground(Color.BLUE);

        JLabel creditLabel = new JLabel("Developed by: Wahaj Ibne Zahid |");
        creditLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        creditLabel.setForeground(Color.DARK_GRAY);
        creditLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));

        JPanel westBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        westBottomPanel.add(creditLabel);
        westBottomPanel.add(statusLabel);

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonGroup.add(overdueButton);
        buttonGroup.add(returnButton);
        
        bottomPanel.add(westBottomPanel, BorderLayout.WEST); 
        bottomPanel.add(buttonGroup, BorderLayout.EAST); 
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        borrowButton.addActionListener(e -> borrowBook());
        returnButton.addActionListener(e -> returnBook());
        overdueButton.addActionListener(e -> fetchOverdueBooksAsync());
        searchButton.addActionListener(e -> performSearch());
        clearSearchButton.addActionListener(e -> refreshAllData());
        sortComboBox.addActionListener(e -> performSort());
        
        this.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) { refreshAllData(); }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {}
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {}
        });
        
        refreshAllData();
    }
    
    private void setupComboBoxRenderers() {
        availableBooksComboBox.setRenderer(new DefaultListCellRenderer() { @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) { super.getListCellRendererComponent(l,v,i,s,f); if (v instanceof Book) setText("ID: " + ((Book)v).getBookId() + " - " + ((Book)v).getTitle()); return this; }});
        membersComboBox.setRenderer(new DefaultListCellRenderer() { @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) { super.getListCellRendererComponent(l,v,i,s,f); if (v instanceof Member) setText("ID: " + ((Member)v).getMemberId() + " - " + ((Member)v).getName()); return this; }});
    }
    
    private void refreshAllData() {
        searchField.setText(""); borrowDateTextField.setText(""); dueDateTextField.setText("");
        try {
            refreshBorrowingTable();
            List<Book> allBooks = bookDAO.listBooks();
            Vector<Book> availableBooks = new Vector<>();
            for(Book book : allBooks) { if("Available".equalsIgnoreCase(book.getAvailabilityStatus())) { availableBooks.add(book); } }
            Collections.sort(availableBooks, Comparator.comparing(Book::getBookId));
            availableBooksComboBox.setModel(new DefaultComboBoxModel<>(availableBooks));
            
            List<Member> allMembers = memberDAO.listMembers();
            Collections.sort(allMembers, Comparator.comparing(Member::getMemberId));
            membersComboBox.setModel(new DefaultComboBoxModel<>(new Vector<>(allMembers)));
        } catch (SQLException e) { 
            JOptionPane.showMessageDialog(this, "Failed to refresh combobox data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); 
        }
    }
    
    private void borrowBook() {
        Book selectedBook = (Book) availableBooksComboBox.getSelectedItem(); Member selectedMember = (Member) membersComboBox.getSelectedItem();
        String borrowDateStr = borrowDateTextField.getText().trim(); String dueDateStr = dueDateTextField.getText().trim();
        
        if (selectedBook == null || selectedMember == null) { JOptionPane.showMessageDialog(this, "Please select a member and a book.", "Error", JOptionPane.ERROR_MESSAGE); return; }
        if (borrowDateStr.isEmpty() || dueDateStr.isEmpty()) { JOptionPane.showMessageDialog(this, "Borrow Date and Due Date cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE); return; }
        
        LocalDate borrowDate; LocalDate dueDate; DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try { 
            borrowDate = LocalDate.parse(borrowDateStr, formatter); dueDate = LocalDate.parse(dueDateStr, formatter);
        } catch (DateTimeParseException ex) { 
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use dd-MM-yyyy.", "Validation Error", JOptionPane.ERROR_MESSAGE); return; 
        }
        
        if (!dueDate.isAfter(borrowDate)) { JOptionPane.showMessageDialog(this, "Due Date must be after the Borrow Date.", "Validation Error", JOptionPane.ERROR_MESSAGE); return; }
        
        try {
            borrowDAO.borrowBook(selectedBook.getBookId(), selectedMember.getMemberId(), borrowDateStr, dueDateStr);
            bookDAO.updateBookStatus(selectedBook.getBookId(), "Borrowed");
            JOptionPane.showMessageDialog(this, "Book borrowed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAllData();
        } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); }
    }
    
    private void returnBook() {
        int selectedRow = borrowingTable.getSelectedRow(); if (selectedRow < 0) { JOptionPane.showMessageDialog(this, "Please select a book to return.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int modelRow = borrowingTable.convertRowIndexToModel(selectedRow); Borrowing selectedBorrowing = currentBorrowings.get(modelRow);
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to return the book:\n" + selectedBorrowing.getBookTitle() + "?", "Confirm Return", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            try {
                borrowDAO.returnBook(selectedBorrowing.getBorrowId()); bookDAO.updateBookStatus(selectedBorrowing.getBookId(), "Available");
                JOptionPane.showMessageDialog(this, "Book returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE); 
                refreshAllData();
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE); }
        }
    }
    
    private void performSort() {
        String selectedSort = (String) sortComboBox.getSelectedItem(); if (selectedSort == null) return;
        switch (selectedSort) {
            case "Sort by Book Title (A-Z)": currentBorrowings.sort(Comparator.comparing(Borrowing::getBookTitle, String.CASE_INSENSITIVE_ORDER)); break;
            case "Sort by Book Title (Z-A)": currentBorrowings.sort(Comparator.comparing(Borrowing::getBookTitle, String.CASE_INSENSITIVE_ORDER).reversed()); break;
            case "Sort by Member Name (A-Z)": currentBorrowings.sort(Comparator.comparing(Borrowing::getMemberName, String.CASE_INSENSITIVE_ORDER)); break;
            case "Sort by Member Name (Z-A)": currentBorrowings.sort(Comparator.comparing(Borrowing::getMemberName, String.CASE_INSENSITIVE_ORDER).reversed()); break;
            default: currentBorrowings = new ArrayList<>(originalOrderList); break;
        }
        updateBorrowingTableFromCache();
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText(); if (searchTerm == null || searchTerm.trim().isEmpty()) { refreshAllData(); return; }
        try {
            this.currentBorrowings = borrowDAO.searchCurrentBorrowings(searchTerm); this.originalOrderList = new ArrayList<>(this.currentBorrowings);
            sortComboBox.setSelectedIndex(0); updateBorrowingTableFromCache();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error during search: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE); }
    }
    
    private void updateBorrowingTableFromCache() {
        tableModel.setRowCount(0); DateTimeFormatter dbFormatter = DateTimeFormatter.ISO_LOCAL_DATE; DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (Borrowing borrowing : currentBorrowings) {
            LocalDate bDate = LocalDate.parse(borrowing.getBorrowDate(), dbFormatter); LocalDate dDate = LocalDate.parse(borrowing.getDueDate(), dbFormatter);
            String displayBorrowDate = bDate.format(displayFormatter); String displayDueDate = dDate.format(displayFormatter);
            tableModel.addRow(new Object[]{ borrowing.getBorrowId(), borrowing.getBookTitle(), borrowing.getMemberName(), displayBorrowDate, displayDueDate });
        }
    }

    private void refreshBorrowingTable() {
        statusLabel.setText("Loading data from database...");
        overdueButton.setEnabled(false);

        SwingWorker<List<Borrowing>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Borrowing> doInBackground() throws Exception {
                Thread.sleep(300);
                return borrowDAO.listCurrentBorrowings();
            }

            @Override
            protected void done() {
                try {
                    currentBorrowings = get();
                    originalOrderList = new ArrayList<>(currentBorrowings);
                    sortComboBox.setSelectedIndex(0); 
                    updateBorrowingTableFromCache();
                    statusLabel.setText("All records loaded successfully.");
                    statusLabel.setForeground(new Color(0, 153, 0));
                } catch (Exception e) {
                    statusLabel.setText(" Status: Error loading data.");
                    statusLabel.setForeground(Color.RED);
                    JOptionPane.showMessageDialog(BorrowPanel.this, "DB Error: " + e.getMessage());
                } finally {
                    overdueButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void fetchOverdueBooksAsync() {
        statusLabel.setText("Searching for overdue books...");
        statusLabel.setForeground(Color.BLUE);
        overdueButton.setEnabled(false);

        SwingWorker<List<Borrowing>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Borrowing> doInBackground() throws Exception {
                return borrowDAO.getOverdueBorrowings();
            }

            @Override
            protected void done() {
                try {
                    currentBorrowings = get();
                    updateBorrowingTableFromCache();
                    if (currentBorrowings.isEmpty()) {
                        statusLabel.setText("No overdue books found! Great.");
                        statusLabel.setForeground(new Color(0, 153, 0));
                    } else {
                        statusLabel.setText(" Status: " + currentBorrowings.size() + " overdue books found!");
                        statusLabel.setForeground(Color.RED);
                    }
                } catch (Exception e) {
                    statusLabel.setText(" Status: Error fetching overdue books.");
                    statusLabel.setForeground(Color.RED);
                } finally {
                    overdueButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}