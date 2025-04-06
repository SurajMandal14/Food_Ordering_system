package src;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Use system default Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Run the application on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    // Create and show the main window
                    MainWindow mainWindow = new MainWindow();
                    mainWindow.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Error creating main window: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 