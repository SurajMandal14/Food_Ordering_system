package src;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.List;
import java.awt.event.*;

public class AdminWindow extends JFrame {
    private User currentUser;
    private JTable menuTable;
    private DefaultTableModel menuTableModel;
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JTabbedPane tabbedPane;

    public AdminWindow(User user) {
        this.currentUser = user;
        setTitle("Food Ordering System - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create main container with border layout
        JPanel mainContainer = new JPanel(new BorderLayout());
        
        // Header panel with user info and mode switch
        createHeaderPanel(mainContainer);

        // Create tabbed pane with improved styling
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.addTab("Menu Management", createMenuPanel());
        tabbedPane.addTab("Order Management", createOrdersPanel());
        
        // Set custom styling for tabs
        tabbedPane.setBackground(new Color(245, 245, 250));
        tabbedPane.setForeground(new Color(44, 62, 80));

        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        
        // Add footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(new Color(245, 245, 250));
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.black);
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        
        logoutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(220, 53, 69).brighter());
            }
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        footerPanel.add(logoutButton);
        mainContainer.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainContainer);
        loadMenuItems();
        loadOrders();
        startOrderRefreshTimer();

        // Add tab change listener to refresh data
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                loadMenuItems();
            } else if (tabbedPane.getSelectedIndex() == 1) {
                loadOrders();
            }
        });
    }
    
    private void createHeaderPanel(JPanel mainContainer) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Admin panel label
        JLabel adminLabel = new JLabel("Admin Panel - " + currentUser.getFullName());
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        adminLabel.setForeground(new Color(44, 62, 80));
        
        // Customer mode switch button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(245, 245, 250));
        
        JButton switchToCustomerButton = new JButton("Switch to Customer Mode");
        switchToCustomerButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        switchToCustomerButton.setBackground(new Color(52, 152, 219));
        switchToCustomerButton.setForeground(Color.black);
        switchToCustomerButton.setFocusPainted(false);
        switchToCustomerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        switchToCustomerButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                switchToCustomerButton.setBackground(new Color(52, 152, 219).brighter());
            }
            public void mouseExited(MouseEvent e) {
                switchToCustomerButton.setBackground(new Color(52, 152, 219));
            }
        });
        
        switchToCustomerButton.addActionListener(e -> switchToCustomerMode());
        rightPanel.add(switchToCustomerButton);
        
        headerPanel.add(adminLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        mainContainer.add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(255, 255, 255));

        // Create menu table with improved styling
        String[] columns = {"ID", "Item Name", "Description", "Price", "Category", "Vegetarian", "Spicy", "Calories", "Image"};
        menuTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 8) return ImageIcon.class;
                return super.getColumnClass(column);
            }
        };
        menuTable = new JTable(menuTableModel);
        menuTable.setRowHeight(80);
        menuTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        menuTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        menuTable.getTableHeader().setBackground(new Color(240, 240, 240));
        menuTable.setSelectionBackground(new Color(232, 244, 248));
        menuTable.setSelectionForeground(new Color(44, 62, 80));
        menuTable.setGridColor(new Color(230, 230, 230));
        menuTable.setShowVerticalLines(true);
        menuTable.setShowHorizontalLines(true);
        
        menuTable.getColumnModel().getColumn(8).setPreferredWidth(80);
        menuTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        menuTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        menuTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        menuTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        // Set custom renderer for image column
        menuTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                         boolean isSelected, boolean hasFocus,
                                                         int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                if (value instanceof ImageIcon) {
                    label.setText("");
                    label.setIcon((ImageIcon) value);
                } else {
                    label.setIcon(null);
                    label.setText(value != null ? value.toString() : "");
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Create buttons panel with improved styling
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton addButton = new JButton("Add Item");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.black);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(120, 40));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton editButton = new JButton("Edit Item");
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        editButton.setBackground(new Color(0, 123, 255));
        editButton.setForeground(Color.black);
        editButton.setFocusPainted(false);
        editButton.setPreferredSize(new Dimension(120, 40));
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton deleteButton = new JButton("Delete Item");
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteButton.setBackground(new Color(220, 53, 69));
        deleteButton.setForeground(Color.black);
        deleteButton.setFocusPainted(false);
        deleteButton.setPreferredSize(new Dimension(120, 40));
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effects
        addButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                addButton.setBackground(new Color(40, 167, 69).brighter());
            }
            public void mouseExited(MouseEvent e) {
                addButton.setBackground(new Color(40, 167, 69));
            }
        });
        
        editButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                editButton.setBackground(new Color(0, 123, 255).brighter());
            }
            public void mouseExited(MouseEvent e) {
                editButton.setBackground(new Color(0, 123, 255));
            }
        });
        
        deleteButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                deleteButton.setBackground(new Color(220, 53, 69).brighter());
            }
            public void mouseExited(MouseEvent e) {
                deleteButton.setBackground(new Color(220, 53, 69));
            }
        });

        addButton.addActionListener(e -> showAddItemDialog());
        editButton.addActionListener(e -> showEditItemDialog());
        deleteButton.addActionListener(e -> deleteSelectedItem());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Create orders table with improved styling
        String[] columns = {"Order ID", "Customer", "Status", "Date", "Total", "Payment Status"};
        ordersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setRowHeight(40);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ordersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        ordersTable.getTableHeader().setBackground(new Color(240, 240, 240));
        ordersTable.setSelectionBackground(new Color(232, 244, 248));
        ordersTable.setSelectionForeground(new Color(44, 62, 80));
        ordersTable.setGridColor(new Color(230, 230, 230));
        ordersTable.setShowVerticalLines(true);
        ordersTable.setShowHorizontalLines(true);
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Create buttons panel with improved styling
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JButton updateStatusButton = new JButton("Update Status");
        updateStatusButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateStatusButton.setBackground(new Color(0, 123, 255));
        updateStatusButton.setForeground(Color.black);
        updateStatusButton.setFocusPainted(false);
        updateStatusButton.setPreferredSize(new Dimension(150, 40));
        updateStatusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setBackground(new Color(23, 162, 184));
        refreshButton.setForeground(Color.black);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(150, 40));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effects
        updateStatusButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                updateStatusButton.setBackground(new Color(0, 123, 255).brighter());
            }
            public void mouseExited(MouseEvent e) {
                updateStatusButton.setBackground(new Color(0, 123, 255));
            }
        });
        
        refreshButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                refreshButton.setBackground(new Color(23, 162, 184).brighter());
            }
            public void mouseExited(MouseEvent e) {
                refreshButton.setBackground(new Color(23, 162, 184));
            }
        });

        updateStatusButton.addActionListener(e -> updateOrderStatus());
        refreshButton.addActionListener(e -> loadOrders());

        buttonPanel.add(updateStatusButton);
        buttonPanel.add(refreshButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadMenuItems() {
        menuTableModel.setRowCount(0);
        List<MenuItem> items = MenuItem.getAllItems();
        for (MenuItem item : items) {
            ImageIcon icon = item.getImageIcon(50, 50);
            menuTableModel.addRow(new Object[]{
                item.getId(),
                item.getItemName(),
                item.getDescription(),
                String.format("$%.2f", item.getPrice()),
                item.getCategory(),
                item.isVegetarian() ? "Yes" : "No",
                item.isSpicy() ? "Yes" : "No",
                item.getCalories(),
                icon
            });
        }
        // Set row height to accommodate images
        menuTable.setRowHeight(50);
        // Set custom renderer for image column
        menuTable.getColumnModel().getColumn(8).setCellRenderer(new ImageRenderer());
    }

    // Custom renderer for image column
    private class ImageRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                     boolean isSelected, boolean hasFocus,
                                                     int row, int column) {
            if (value instanceof ImageIcon) {
                setIcon((ImageIcon) value);
                setText("");
            } else {
                setIcon(null);
                setText(value != null ? value.toString() : "");
            }
            return this;
        }
    }

    private void loadOrders() {
        ordersTableModel.setRowCount(0);
        List<Order> orders = Order.getAllOrders(); // Change to get all orders for admin
        for (Order order : orders) {
            ordersTableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getCustomerName(), // Display customer name instead of user ID
                order.getOrderStatus(),
                order.getOrderDate(),
                String.format("$%.2f", order.getTotalAmount()),
                order.getPaymentStatus()
            });
        }
    }

    // Add a timer to refresh orders automatically
    private void startOrderRefreshTimer() {
        Timer timer = new Timer(30000, e -> { // Refresh every 30 seconds
            if (tabbedPane.getSelectedIndex() == 1) { // Orders tab is selected
                loadOrders();
            }
        });
        timer.start();
    }

    private void showAddItemDialog() {
        JDialog dialog = new JDialog(this, "Add New Item", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create form fields
        JTextField nameField = new JTextField(20);
        JTextField descField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField categoryField = new JTextField(20);
        JCheckBox vegetarianBox = new JCheckBox();
        JCheckBox spicyBox = new JCheckBox();
        JSpinner caloriesSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 5000, 10));
        JSpinner prepTimeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 180, 5));
        JButton imageButton = new JButton("Select Image");
        JLabel imageLabel = new JLabel("No image selected");

        // Add form fields to panel
        int row = 0;
        addFormField(panel, "Item Name:", nameField, gbc, row++);
        addFormField(panel, "Description:", descField, gbc, row++);
        addFormField(panel, "Price:", priceField, gbc, row++);
        addFormField(panel, "Category:", categoryField, gbc, row++);
        addFormField(panel, "Vegetarian:", vegetarianBox, gbc, row++);
        addFormField(panel, "Spicy:", spicyBox, gbc, row++);
        addFormField(panel, "Calories:", caloriesSpinner, gbc, row++);
        addFormField(panel, "Prep Time (min):", prepTimeSpinner, gbc, row++);

        // Image selection
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Image:"), gbc);
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.add(imageButton);
        imagePanel.add(imageLabel);
        gbc.gridx = 1;
        panel.add(imagePanel, gbc);

        final String[] selectedImagePath = {null};
        imageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(java.io.File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg") 
                           || f.getName().toLowerCase().endsWith(".png");
                }
                public String getDescription() {
                    return "Image files (*.jpg, *.png)";
                }
            });

            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImagePath[0] = fileChooser.getSelectedFile().getAbsolutePath();
                imageLabel.setText(fileChooser.getSelectedFile().getName());
            }
        });

        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String desc = descField.getText();
                double price = Double.parseDouble(priceField.getText());
                String category = categoryField.getText();
                boolean isVegetarian = vegetarianBox.isSelected();
                boolean isSpicy = spicyBox.isSelected();
                int calories = (Integer) caloriesSpinner.getValue();
                int prepTime = (Integer) prepTimeSpinner.getValue();

                if (name.isEmpty() || desc.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields");
                    return;
                }

                String imagePath = selectedImagePath[0];
                if (imagePath != null) {
                    imagePath = ImageHandler.saveImage(imagePath);
                }

                if (MenuItem.addItem(name, desc, price, category, imagePath, 
                                  isVegetarian, isSpicy, calories, prepTime)) {
                    loadMenuItems();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Item added successfully");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add item");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.gridwidth = 2;
        panel.add(addButton, gbc);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, String label, JComponent field, 
                            GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void showEditItemDialog() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit");
            return;
        }

        int itemId = (int) menuTable.getValueAt(selectedRow, 0);
        String currentName = (String) menuTable.getValueAt(selectedRow, 1);
        String currentDesc = (String) menuTable.getValueAt(selectedRow, 2);
        double currentPrice = Double.parseDouble(menuTable.getValueAt(selectedRow, 3).toString().substring(1));
        String currentCategory = (String) menuTable.getValueAt(selectedRow, 4);
        boolean currentVegetarian = menuTable.getValueAt(selectedRow, 5).toString().equals("Yes");
        boolean currentSpicy = menuTable.getValueAt(selectedRow, 6).toString().equals("Yes");
        int currentCalories = (int) menuTable.getValueAt(selectedRow, 7);

        JDialog dialog = new JDialog(this, "Edit Item", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create form fields with current values
        JTextField nameField = new JTextField(currentName, 20);
        JTextField descField = new JTextField(currentDesc, 20);
        JTextField priceField = new JTextField(String.valueOf(currentPrice), 20);
        JTextField categoryField = new JTextField(currentCategory, 20);
        JCheckBox vegetarianBox = new JCheckBox("", currentVegetarian);
        JCheckBox spicyBox = new JCheckBox("", currentSpicy);
        JSpinner caloriesSpinner = new JSpinner(new SpinnerNumberModel(currentCalories, 0, 5000, 10));
        JSpinner prepTimeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 180, 5));
        JButton imageButton = new JButton("Select Image");
        JLabel imageLabel = new JLabel("No image selected");

        // Add form fields to panel
        int row = 0;
        addFormField(panel, "Item Name:", nameField, gbc, row++);
        addFormField(panel, "Description:", descField, gbc, row++);
        addFormField(panel, "Price:", priceField, gbc, row++);
        addFormField(panel, "Category:", categoryField, gbc, row++);
        addFormField(panel, "Vegetarian:", vegetarianBox, gbc, row++);
        addFormField(panel, "Spicy:", spicyBox, gbc, row++);
        addFormField(panel, "Calories:", caloriesSpinner, gbc, row++);
        addFormField(panel, "Prep Time (min):", prepTimeSpinner, gbc, row++);

        // Image selection
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Image:"), gbc);
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.add(imageButton);
        imagePanel.add(imageLabel);
        gbc.gridx = 1;
        panel.add(imagePanel, gbc);

        final String[] selectedImagePath = {null};
        imageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(java.io.File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".jpg") 
                           || f.getName().toLowerCase().endsWith(".png");
                }
                public String getDescription() {
                    return "Image files (*.jpg, *.png)";
                }
            });

            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImagePath[0] = fileChooser.getSelectedFile().getAbsolutePath();
                imageLabel.setText(fileChooser.getSelectedFile().getName());
            }
        });

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                String desc = descField.getText();
                double price = Double.parseDouble(priceField.getText());
                String category = categoryField.getText();
                boolean isVegetarian = vegetarianBox.isSelected();
                boolean isSpicy = spicyBox.isSelected();
                int calories = (Integer) caloriesSpinner.getValue();
                int prepTime = (Integer) prepTimeSpinner.getValue();

                if (name.isEmpty() || desc.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in all required fields");
                    return;
                }

                String imagePath = selectedImagePath[0];
                if (imagePath != null) {
                    imagePath = ImageHandler.saveImage(imagePath);
                }

                if (MenuItem.updateItem(itemId, name, desc, price, category, imagePath,
                                     isVegetarian, isSpicy, calories, prepTime, true)) {
                    loadMenuItems();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Item updated successfully");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update item");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid price");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.gridwidth = 2;
        panel.add(updateButton, gbc);

        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void deleteSelectedItem() {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete");
            return;
        }

        int itemId = (int) menuTable.getValueAt(selectedRow, 0);
        String itemName = (String) menuTable.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete " + itemName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (MenuItem.deleteItem(itemId)) {
                loadMenuItems();
                JOptionPane.showMessageDialog(this, "Item deleted successfully");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete item");
            }
        }
    }

    private void updateOrderStatus() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an order to update.", 
                "No Order Selected", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int orderId = (int) ordersTableModel.getValueAt(selectedRow, 0);
        String customerName = (String) ordersTableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) ordersTableModel.getValueAt(selectedRow, 2);

        String[] statusOptions = {"Pending", "Preparing", "Ready", "In Delivery", "Delivered", "Cancelled"};
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Update status for Order #" + orderId + " from " + customerName,
            "Update Order Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus
        );

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            if (Order.updateOrderStatus(orderId, newStatus)) {
                loadOrders();
                JOptionPane.showMessageDialog(this, 
                    "Order status updated successfully.",
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to update order status.",
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void switchToCustomerMode() {
        new CustomerWindow(currentUser).setVisible(true);
        this.dispose();
    }
    
    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            new MainWindow().setVisible(true);
            this.dispose();
        }
    }
} 