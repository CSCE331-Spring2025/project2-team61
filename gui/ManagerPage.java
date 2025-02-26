import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.util.ArrayList;

public class ManagerPage extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JTable inventoryTable, priceTable;
    private DefaultTableModel tableModel, priceTableModel;
    private Connection connection;
    private ReportPanel reportPanel;
    private EmployeePanel employeePanel;
    private int employeeId;
    private JButton addProductButton;


    // TODO: pass in db
    public ManagerPage(int employeeId) {
        super("Manager Inventory Interface");
        FrameStyle.StyleFrame(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.employeeId = employeeId;

        // Navbar Panel
        JPanel navbarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
        navbarPanel.setBackground(Color.decode("#f0f0f0")); // light background

        JButton inventoryNavButton = new JButton("Inventory");
        inventoryNavButton.setFont(new Font("Arial", Font.BOLD, 20));
        inventoryNavButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "inventory");
            }
        });

        JButton priceNavButton = new JButton("Price");
        priceNavButton.setFont(new Font("Arial", Font.BOLD, 20));
        priceNavButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "price");
            }
        });

        JButton employeeNavButton = new JButton("Employee");
        employeeNavButton.setFont(new Font("Arial", Font.BOLD, 20));
        employeeNavButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "employee");
            }
        });

        JButton reportNavButton = new JButton("Reports");
        reportNavButton.setFont(new Font("Arial", Font.BOLD, 20));
        reportNavButton.addActionListener(e -> {
            loadReportData();
            cardLayout.show(cardPanel, "report");
        });

        JButton logoutNavButton = new JButton("Logout");
        logoutNavButton.setFont(new Font("Arial", Font.BOLD, 20));
        logoutNavButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });

        navbarPanel.add(inventoryNavButton);
        navbarPanel.add(priceNavButton);
        navbarPanel.add(employeeNavButton);
        navbarPanel.add(reportNavButton);
        navbarPanel.add(logoutNavButton);
        add(navbarPanel, BorderLayout.NORTH);

        // Card Panel to hold different views
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Inventory Panel
        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inventoryPanel.setBackground(Color.decode("#E0F6F1"));

        JLabel inventoryLabel = new JLabel("Inventory");
        inventoryLabel.setFont(new Font("Arial", Font.BOLD, 50));
        inventoryLabel.setForeground(Color.BLACK);
        inventoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inventoryPanel.add(inventoryLabel, BorderLayout.NORTH);

        String[] inventoryColumns = {"Item", "Inventory Count", "Order More"};
        tableModel = new DefaultTableModel(inventoryColumns, 0);
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 20));
        inventoryTable.setRowHeight(40);

        // Add Button Column for ordering more
        TableColumn orderMoreColumn = inventoryTable.getColumnModel().getColumn(2);
        orderMoreColumn.setCellRenderer(new ButtonRenderer());
        orderMoreColumn.setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane inventoryScrollPane = new JScrollPane(inventoryTable);
        inventoryPanel.add(inventoryScrollPane, BorderLayout.CENTER);

        // Price Panel
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pricePanel.setBackground(Color.decode("#E0F6F1"));

        JLabel priceLabel = new JLabel("Price Table");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 50));
        priceLabel.setForeground(Color.BLACK);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pricePanel.add(priceLabel, BorderLayout.NORTH);

        String[] priceColumns = {"Item", "Price ($)", "Edit Price"};
        priceTableModel = new DefaultTableModel(priceColumns, 0);
        priceTable = new JTable(priceTableModel);
        priceTable.setFont(new Font("Arial", Font.PLAIN, 20));
        priceTable.setRowHeight(40);

        // Set up the Edit Price Button in the table
        TableColumn editPriceColumn = priceTable.getColumnModel().getColumn(2);
        editPriceColumn.setCellRenderer(new PriceButtonRenderer());
        editPriceColumn.setCellEditor(new PriceButtonEditor(new JCheckBox()));

        JScrollPane priceScrollPane = new JScrollPane(priceTable);
        pricePanel.add(priceScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addProductButton = new JButton("Add Product +");
        addProductButton.setFont(new Font("Arial", Font.BOLD, 18));
        addProductButton.setBackground(new Color(76, 175, 80)); // Green color
        addProductButton.setForeground(Color.WHITE);
        addProductButton.setFocusPainted(false);
        addProductButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        addProductButton.addActionListener(e -> showAddProductDialog());
        buttonPanel.add(addProductButton);

        pricePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Connect to database before creating panels that need the connection
        connectToDatabase();

        // Create report panel
        reportPanel = new ReportPanel();

        // Create employee panel
        employeePanel = new EmployeePanel(connection);

        // Add panels to card layout
        cardPanel.add(inventoryPanel, "inventory");
        cardPanel.add(pricePanel, "price");
        cardPanel.add(employeePanel, "employee");
        cardPanel.add(reportPanel, "report");

        add(cardPanel, BorderLayout.CENTER);

        // Load data
        loadInventory();
        loadPriceTable();
        loadReportData();

        // Show inventory view by default
        cardLayout.show(cardPanel, "inventory");

        setVisible(true);
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Product", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField(20);

        JLabel priceLabel = new JLabel("Price (in cents):");
        JTextField priceField = new JTextField(20);

        JLabel productTypeLabel = new JLabel("Product Type:");
        JTextField productTypeField = new JTextField(20);

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(productTypeLabel);
        formPanel.add(productTypeField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String price = priceField.getText().trim();
            String productType = productTypeField.getText().trim();

            if (name.isEmpty() || price.isEmpty() || productType.isEmpty()) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Name and password cannot be empty",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (addProduct(name, price, productType)) {
                dialog.dispose();
                loadPriceTable();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private boolean addProduct(String productName, String productPrice, String productType) {
        Db db = new Db();

        if (db.query("INSERT INTO product (product_type, name, price) VALUES ('%s', '%s', %s) RETURNING id;", 
            productType, productName, productPrice) == null) {
            return false;
        }
        return true;
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_61_db",
                    "team_61",
                    "6161"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInventory() {
        tableModel.setRowCount(0); // Clear the table before reloading
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT product.name, product.inventory AS inventory_count " +
                            "FROM product ORDER BY inventory_count DESC;"
            );
            while (rs.next()) {
                String name = rs.getString("name");
                int inventory = rs.getInt("inventory_count");
                tableModel.addRow(new Object[]{name, inventory, "Order More"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPriceTable() {
        priceTableModel.setRowCount(0); // Clear table before reloading
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT product.name, product.price FROM product ORDER BY price DESC;"
            );
            while (rs.next()) {
                String name = rs.getString("name");
                // Convert cents to dollars for display
                double price = rs.getDouble("price") / 100.0;
                priceTableModel.addRow(new Object[]{
                        name,
                        String.format("$%.2f", price),
                        "Edit Price"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReportData() {
        ArrayList<String> itemNames = new ArrayList<>();
        ArrayList<Integer> inventoryCounts = new ArrayList<>();
        ArrayList<String> recentItems = new ArrayList<>();
        ArrayList<Double> recentPrices = new ArrayList<>();
        ArrayList<String> recentPayments = new ArrayList<>();
    
        try {
            Statement stmt = connection.createStatement();
    
            // Query for Low Inventory Items
            ResultSet rs = stmt.executeQuery("SELECT name, inventory FROM product WHERE inventory < 50 ORDER BY inventory ASC;");
            while (rs.next()) {
                itemNames.add(rs.getString("name"));
                inventoryCounts.add(rs.getInt("inventory"));
            }
            reportPanel.updateLowSupplyTable(itemNames, inventoryCounts);
    
            // Query for 5 Most Recent Orders
            rs = stmt.executeQuery(
                "SELECT p.name AS item_name, ti.subtotal AS price, t.payment_type " +
                "FROM transaction t " +
                "JOIN transaction_item ti ON t.id = ti.transaction_id " +
                "JOIN product p ON ti.product_id = p.id " +
                "ORDER BY t.time DESC " +
                "LIMIT 5;"
            );
    
            while (rs.next()) {
                recentItems.add(rs.getString("item_name"));
                recentPrices.add(rs.getDouble("price"));
                recentPayments.add(rs.getString("payment_type"));
            }
            reportPanel.updateRecentOrders(recentItems, recentPrices, recentPayments);
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Button Renderer for Inventory "Order More" column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Order More");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            return this;
        }
    }

    // Button Editor for Inventory "Order More" column
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Order More");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column
        ) {
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            String itemName = (String) tableModel.getValueAt(selectedRow, 0);
            orderMoreItem(itemName, selectedRow);
            return "Order More";
        }
    }

    private void orderMoreItem(String itemName, int row) {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter amount to add for " + itemName + ":",
                "Order More",
                JOptionPane.PLAIN_MESSAGE
        );
        if (input != null) {
            try {
                int amountToAdd = Integer.parseInt(input);
                if (amountToAdd > 0) {
                    updateInventoryInDatabase(itemName, amountToAdd);
                    int updatedInventory = (int) tableModel.getValueAt(row, 1) + amountToAdd;
                    tableModel.setValueAt(updatedInventory, row, 1);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid number.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void updateInventoryInDatabase(String itemName, int amountToAdd) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE product SET inventory = inventory + ? WHERE name = ?"
            );
            pstmt.setInt(1, amountToAdd);
            pstmt.setString(2, itemName);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Renderer for Price "Edit Price" button column
    class PriceButtonRenderer extends JButton implements TableCellRenderer {
        public PriceButtonRenderer() {
            setText("Edit Price");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column
        ) {
            return this;
        }
    }

    // Editor for Price "Edit Price" button column
    class PriceButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;

        public PriceButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Edit Price");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column
        ) {
            selectedRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            String itemName = (String) priceTableModel.getValueAt(selectedRow, 0);
            editPriceItem(itemName, selectedRow);
            return "Edit Price";
        }
    }

    private void editPriceItem(String itemName, int row) {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter new price for " + itemName + " in dollars:",
                "Edit Price",
                JOptionPane.PLAIN_MESSAGE
        );
        if (input != null) {
            try {
                double newPrice = Double.parseDouble(input);
                if (newPrice >= 0) {
                    // Convert dollars to cents (assuming price is stored in cents)
                    int newPriceCents = (int) Math.round(newPrice * 100);
                    updatePriceInDatabase(itemName, newPriceCents);
                    // Update the table model with formatted price
                    priceTableModel.setValueAt(String.format("$%.2f", newPrice), row, 1);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid price.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void updatePriceInDatabase(String itemName, int newPriceCents) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE product SET price = ? WHERE name = ?"
            );
            pstmt.setInt(1, newPriceCents);
            pstmt.setString(2, itemName);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        dispose();
        JFrame jobSelectionFrame = new JobSelectionPage(new Db(), employeeId);
        jobSelectionFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManagerPage mp = new ManagerPage(1);
            mp.setVisible(true);
        });
    }
}
