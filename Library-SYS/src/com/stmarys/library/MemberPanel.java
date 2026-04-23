package com.stmarys.library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MemberPanel extends JPanel {

    private JTable memberTable;
    private DefaultTableModel tableModel;
    private MemberDAO memberDAO;
    private List<Member> currentMemberList;
    private List<Member> originalOrderList;

    private JTextField searchField;
    private JComboBox<String> sortComboBox;

    public MemberPanel() {
        memberDAO = new MemberDAO();
        currentMemberList = new ArrayList<>();
        originalOrderList = new ArrayList<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(25);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear Search");
        searchPanel.add(new JLabel("Search by Name or Email:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        String[] sortOptions = {
            "Default Order", 
            "Sort by Name (A-Z)", 
            "Sort by Name (Z-A)"
        };
        sortComboBox = new JComboBox<>(sortOptions);
        sortPanel.add(new JLabel("Sort:"));
        sortPanel.add(sortComboBox);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(sortPanel, BorderLayout.EAST);

        String[] columnNames = {"ID", "Name", "Email", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel bottomPanel = new JPanel(new BorderLayout()); 

        JLabel creditLabel = new JLabel("Developed by: Wahaj Ibne Zahid");
        creditLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        creditLabel.setForeground(Color.DARK_GRAY);
        creditLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Member");
        JButton deleteButton = new JButton("Delete Selected");
        JButton updateButton = new JButton("Update Selected");
        bottomButtonPanel.add(addButton);
        bottomButtonPanel.add(deleteButton);
        bottomButtonPanel.add(updateButton);

        bottomPanel.add(creditLabel, BorderLayout.CENTER);
        bottomPanel.add(bottomButtonPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(memberTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadAllMembers();
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadAllMembers();
        });
        sortComboBox.addActionListener(e -> performSort());

        addButton.addActionListener(e -> {
            AddMemberDialog dialog = new AddMemberDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                String name = dialog.getMemberName().trim();
                String email = dialog.getMemberEmail().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Member Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
                if (!email.isEmpty() && !email.matches(emailRegex)) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    memberDAO.addMember(dialog.getMemberId(), name, email, dialog.getStatus());
                    loadAllMembers();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error adding member: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow >= 0) {
                int modelRow = memberTable.convertRowIndexToModel(selectedRow);
                int memberId = (int) tableModel.getValueAt(modelRow, 0);
                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this member?", "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    try {
                        memberDAO.deleteMember(memberId);
                        loadAllMembers();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error deleting member: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a member to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = memberTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(this, "Please select a member to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = memberTable.convertRowIndexToModel(selectedRow);
            int memberIdToUpdate = (int) tableModel.getValueAt(modelRow, 0);
            Optional<Member> memberToUpdateOpt = currentMemberList.stream().filter(member -> member.getMemberId() == memberIdToUpdate).findFirst();

            if (memberToUpdateOpt.isPresent()) {
                AddMemberDialog dialog = new AddMemberDialog((Frame) SwingUtilities.getWindowAncestor(this), memberToUpdateOpt.get());
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    String name = dialog.getMemberName().trim();
                    String email = dialog.getMemberEmail().trim();
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Member Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
                    if (!email.isEmpty() && !email.matches(emailRegex)) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Member updatedMember = new Member(dialog.getMemberId(), name, email, dialog.getStatus());
                    try {
                        memberDAO.updateMember(updatedMember);
                        loadAllMembers();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error updating member: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
    
    private void performSort() {
        String selectedSort = (String) sortComboBox.getSelectedItem();
        if (selectedSort == null) return;

        switch (selectedSort) {
            case "Sort by Name (A-Z)":
                currentMemberList.sort(Comparator.comparing(Member::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Sort by Name (Z-A)":
                currentMemberList.sort(Comparator.comparing(Member::getName, String.CASE_INSENSITIVE_ORDER).reversed());
                break;
            default: 
                currentMemberList = new ArrayList<>(originalOrderList);
                break;
        }
        updateTable();
    }

    private void performSearch() {
        try {
            String searchTerm = searchField.getText();
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                loadAllMembers();
                return;
            }
            this.currentMemberList = memberDAO.searchMembers(searchTerm);
            this.originalOrderList = new ArrayList<>(this.currentMemberList);
            sortComboBox.setSelectedIndex(0);
            updateTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error during search: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllMembers() {
        try {
            this.currentMemberList = memberDAO.listMembers();
            this.originalOrderList = new ArrayList<>(this.currentMemberList);
            sortComboBox.setSelectedIndex(0);
            updateTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading members: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            this.currentMemberList = new ArrayList<>();
            this.originalOrderList = new ArrayList<>();
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        if (currentMemberList != null) {
            for (Member member : currentMemberList) {
                tableModel.addRow(new Object[]{member.getMemberId(), member.getName(), member.getEmail(), member.getMembershipStatus()});
            }
        }
    }
}