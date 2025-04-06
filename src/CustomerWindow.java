package src;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.List;
import java.awt.event.*;

public class CustomerWindow extends JFrame {
    private User currentUser;
    private JTable menuTable;
    private DefaultTableModel menuTableModel;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JLabel totalLabel;
    private JTabbedPane tabbedPane;
    private JPanel userInfoPanel;

    public CustomerWindow(User user) {
        this.currentUser = user;
        setTitle("Food Ordering System - Customer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create main container with border layout
        JPanel mainContainer = new JPanel(new BorderLayout());
        
        // Header panel with user info and admin switch if applicable
        createHeaderPanel(mainContainer);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.addTab("Menu", createMenuPanel());
        tabbedPane.addTab("Cart", createCartPanel());
        tabbedPane.addTab("Orders", createOrdersPanel());
        
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
        startOrderRefreshTimer();

        // Add tab change listener to refresh data
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                loadMenuItems();
            } else if (tabbedPane.getSelectedIndex() == 1) {
                updateCart();
            } else if (tabbedPane.getSelectedIndex() == 2) {
                loadOrders();
            }
        });
    }
    
    private void createHeaderPanel(JPanel mainContainer) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 250));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(44, 62, 80));
        
        // Admin switch button - only show if user has admin rights
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(new Color(245, 245, 250));
        
        if (currentUser.getRole().equals("Admin")) {
            JButton switchToAdminButton = new JButton("Switch to Admin Mode");
            switchToAdminButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            switchToAdminButton.setBackground(new Color(52, 58, 64));
            switchToAdminButton.setForeground(Color.black);
            switchToAdminButton.setFocusPainted(false);
            switchToAdminButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            switchToAdminButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    switchToAdminButton.setBackground(new Color(52, 58, 64).brighter());
                }
                public void mouseExited(MouseEvent e) {
                    switchToAdminButton.setBackground(new Color(52, 58, 64));
                }
            });
            
            switchToAdminButton.addActionListener(e -> switchToAdminMode());
            rightPanel.add(switchToAdminButton);
        }
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        mainContainer.add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(255, 255, 255));

        // Create menu table with improved styling
        String[] columns = {"ID", "Image", "Item Name", "Description", "Price", "Category"};
        menuTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 1) return ImageIcon.class;
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
        
        // Hide ID column as it's not relevant to users
        menuTable.getColumnModel().getColumn(0).setMinWidth(0);
        menuTable.getColumnModel().getColumn(0).setMaxWidth(0);
        menuTable.getColumnModel().getColumn(0).setWidth(0);
        
        // Set column widths
        menuTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        menuTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        menuTable.getColumnModel().getColumn(3).setPreferredWidth(250);
        menuTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        menuTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Set custom renderer for image column
        TableCellRenderer imageRenderer = new DefaultTableCellRenderer() {
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
        };
        menuTable.getColumnModel().getColumn(1).setCellRenderer(imageRenderer);

        JScrollPane scrollPane = new JScrollPane(menuTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Create add to cart button and quantity spinner with better styling
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        quantitySpinner.setPreferredSize(new Dimension(60, 30));
        quantitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addToCartButton.setBackground(new Color(0, 123, 255));
        addToCartButton.setForeground(Color.BLACK);
        addToCartButton.setFocusPainted(false);
        addToCartButton.setPreferredSize(new Dimension(150, 35));
        addToCartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        addToCartButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                addToCartButton.setBackground(new Color(0, 105, 217));
            }
            public void mouseExited(MouseEvent e) {
                addToCartButton.setBackground(new Color(0, 123, 255));
            }
        });
        
        // Add action listener with quantity
        addToCartButton.addActionListener(e -> addToCart((Integer) quantitySpinner.getValue()));

        controlPanel.add(quantityLabel);
        controlPanel.add(quantitySpinner);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(addToCartButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Create cart table with improved styling
        String[] columns = {"Item Name", "Price", "Quantity", "Total"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(40);
        cartTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        cartTable.getTableHeader().setBackground(new Color(240, 240, 240));
        cartTable.setSelectionBackground(new Color(232, 244, 248));
        cartTable.setSelectionForeground(new Color(44, 62, 80));
        cartTable.setGridColor(new Color(230, 230, 230));
        cartTable.setShowVerticalLines(true);
        cartTable.setShowHorizontalLines(true);
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Create total label and checkout button with better styling
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JButton removeButton = new JButton("Remove Item");
        removeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        removeButton.setBackground(new Color(220, 53, 69));
        removeButton.setForeground(Color.BLACK);
        removeButton.setFocusPainted(false);
        removeButton.setPreferredSize(new Dimension(140, 35));
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButton.addActionListener(e -> removeFromCart());
        
        removeButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                removeButton.setBackground(new Color(220, 53, 69).brighter());
            }
            public void mouseExited(MouseEvent e) {
                removeButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        JButton checkoutButton = new JButton("Checkout");
        checkoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutButton.setBackground(new Color(40, 167, 69));
        checkoutButton.setForeground(Color.BLACK);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setPreferredSize(new Dimension(140, 35));
        checkoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkoutButton.addActionListener(e -> checkout());
        
        checkoutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                checkoutButton.setBackground(new Color(40, 167, 69).brighter());
            }
            public void mouseExited(MouseEvent e) {
                checkoutButton.setBackground(new Color(40, 167, 69));
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(removeButton);
        buttonPanel.add(checkoutButton);
        
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        updateCart();
        return panel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Create orders table with improved styling
        String[] columns = {"Order ID", "Status", "Date"};
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

        // Add refresh button with better styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JButton refreshButton = new JButton("Refresh Orders");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.setBackground(new Color(23, 162, 184));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
        refreshButton.setPreferredSize(new Dimension(150, 35));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadOrders());
        
        refreshButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                refreshButton.setBackground(new Color(23, 162, 184).brighter());
            }
            public void mouseExited(MouseEvent e) {
                refreshButton.setBackground(new Color(23, 162, 184));
            }
        });
        
        buttonPanel.add(refreshButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loadOrders();
        return panel;
    }

    private void loadMenuItems() {
        menuTableModel.setRowCount(0);
        List<MenuItem> items = MenuItem.getAllItems();
        for (MenuItem item : items) {
            ImageIcon icon = item.getImageIcon(70, 70);
            menuTableModel.addRow(new Object[]{
                item.getId(),
                icon,
                item.getItemName(),
                item.getDescription(),
                String.format("$%.2f", item.getPrice()),
                item.getCategory()
            });
        }
    }

    private void addToCart(int quantity) {
        int selectedRow = menuTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to add to cart.",
                "Selection Required",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int itemId = (int) menuTableModel.getValueAt(selectedRow, 0);
        if (Order.addToCart(currentUser.getId(), itemId, quantity)) {
            updateCart();
            JOptionPane.showMessageDialog(this, 
                "Item added to cart successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an item to remove from cart.",
                "Selection Required",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String itemName = (String) cartTableModel.getValueAt(selectedRow, 0);
        int result = JOptionPane.showConfirmDialog(this,
            "Remove '" + itemName + "' from cart?",
            "Confirm Removal",
            JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            // Get the item from cart by index and remove it
            List<Order.OrderItem> cartItems = Order.getCartItems(currentUser.getId());
            if (selectedRow < cartItems.size()) {
                Order.OrderItem item = cartItems.get(selectedRow);
                Order.removeFromCart(currentUser.getId(), item.getItemId());
                updateCart();
            }
        }
    }

    private void updateCart() {
        cartTableModel.setRowCount(0);
        double total = 0;
        List<Order.OrderItem> cartItems = Order.getCartItems(currentUser.getId());
        
        for (Order.OrderItem item : cartItems) {
            total += item.getTotal();
            cartTableModel.addRow(new Object[]{
                item.getItemName(),
                String.format("$%.2f", item.getPrice()),
                item.getQuantity(),
                String.format("$%.2f", item.getTotal())
            });
        }
        
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void checkout() {
        List<Order.OrderItem> cartItems = Order.getCartItems(currentUser.getId());
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Your cart is empty!",
                "Empty Cart",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to place this order?",
            "Confirm Order",
            JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            if (Order.createOrder(currentUser.getId())) {
                updateCart();
                loadOrders();
                JOptionPane.showMessageDialog(this, 
                    "Order placed successfully!",
                    "Order Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Switch to Orders tab to show the new order
                tabbedPane.setSelectedIndex(2);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "There was an error processing your order. Please try again.",
                    "Order Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadOrders() {
        ordersTableModel.setRowCount(0);
        List<Order> orders = Order.getUserOrders(currentUser.getId());
        for (Order order : orders) {
            ordersTableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getOrderStatus(),
                order.getOrderDate()
            });
        }
    }

    // Start auto-refresh timer for orders
    private void startOrderRefreshTimer() {
        Timer timer = new Timer(30000, e -> { // Refresh every 30 seconds
            if (tabbedPane.getSelectedIndex() == 2) { // Orders tab is selected
                loadOrders();
            }
        });
        timer.start();
    }
    
    private void switchToAdminMode() {
        if (currentUser.getRole().equals("Admin")) {
            new AdminWindow(currentUser).setVisible(true);
            this.dispose();
        }
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