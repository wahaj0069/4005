package com.stmarys.library;

import javax.swing.*;
import java.awt.*;

public class AddMemberDialog extends JDialog {

    private JTextField idField, nameField, emailField;
    private JComboBox<String> statusComboBox;
    private boolean confirmed = false;

    public AddMemberDialog(Frame owner) {
        super(owner, "Add New Member", true);
        initComponents();
        setTitle("Add New Member");
    }

    public AddMemberDialog(Frame owner, Member memberToUpdate) {
        super(owner, "Update Member", true);
        initComponents();
        setTitle("Update Member");

        idField.setText(String.valueOf(memberToUpdate.getMemberId()));
        idField.setEditable(false); 
        nameField.setText(memberToUpdate.getName());
        emailField.setText(memberToUpdate.getEmail());
        statusComboBox.setSelectedItem(memberToUpdate.getMembershipStatus());
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        idField = new JTextField();
        nameField = new JTextField();
        emailField = new JTextField();
        String[] statuses = {"Active", "Inactive", "Suspended"};
        statusComboBox = new JComboBox<>(statuses);

        panel.add(new JLabel("Member ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Membership Status:"));
        panel.add(statusComboBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> onSave());
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> onCancel());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack(); 
        setLocationRelativeTo(getParent());
    }
    
    private void onSave() {
        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Member ID and Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Integer.parseInt(idField.getText());
        } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(this, "Member ID must be a number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        confirmed = true;
        dispose();
    }

    private void onCancel() {
        confirmed = false;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getMemberId() {
        return Integer.parseInt(idField.getText());
    }

    public String getMemberName() {
        return nameField.getText();
    }
    
    public String getMemberEmail() {
        return emailField.getText();
    }

    public String getStatus() {
        return (String) statusComboBox.getSelectedItem();
    }
}