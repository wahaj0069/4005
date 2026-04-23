package com.stmarys.library;

import javax.swing.*;
import java.awt.*;

public class AddBookDialog extends JDialog {
    private JTextField idField = new JTextField(10);
    private JTextField titleField = new JTextField(20);
    private JTextField authorField = new JTextField(20);
    private JTextField categoryField = new JTextField(20);
    private JComboBox<String> statusComboBox;
    private boolean confirmed = false;

    public AddBookDialog(Frame owner) {
        super(owner, "Add Book", true);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Book ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);
        formPanel.add(new JLabel("Status:"));

        String[] statuses = {"Available", "Borrowed"};
        statusComboBox = new JComboBox<>(statuses);
        formPanel.add(statusComboBox);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(e -> {
            if (idField.getText().trim().isEmpty() || titleField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Book ID and Title cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        pack();
        setLocationRelativeTo(owner);
    }

    public AddBookDialog(Frame owner, Book bookToUpdate) {
        this(owner); 
        setTitle("Update Book");

        idField.setText(String.valueOf(bookToUpdate.getBookId()));
        titleField.setText(bookToUpdate.getTitle());
        authorField.setText(bookToUpdate.getAuthor());
        categoryField.setText(bookToUpdate.getCategory());
        statusComboBox.setSelectedItem(bookToUpdate.getAvailabilityStatus());

        idField.setEditable(false);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getBookId() {
        return Integer.parseInt(idField.getText());
    }

    public String getBookTitle() {
        return titleField.getText();
    }

    public String getAuthor() {
        return authorField.getText();
    }

    public String getCategory() {
        return categoryField.getText();
    }

    public String getStatus() {
        return (String) statusComboBox.getSelectedItem();
    }
}