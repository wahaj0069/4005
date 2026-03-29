    package com.stmarys.library;

    import javax.swing.SwingUtilities;

    public class Main {
        public static void main(String[] args) {
            // Use SwingUtilities.invokeLater to ensure the GUI is created on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            });
        }
    }