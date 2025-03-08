import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.util.ArrayList;

/**
 * This class handles the Manager page on the gui.
 * It is reponsible for displaying and manageing each tab.
 *
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
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
    private SalesReport salesReport;
    private XReportPanel xReportPanel;
    private ProductUsePanel productUsePanel;
    private ZReportPanel zReportPanel;

    private Db db;

    /**
     * Constructs the manager page given an employee id.
     *
     * @param employeeId The employee id of the current person logged in.
     */
    public ManagerPage(int employeeId) {
        super("Manager Inventory Interface");
        FrameStyle.StyleFrame(this);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.employeeId = employeeId;
        db = new Db();

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

        JButton ZReportNavButton = new JButton("Z-Report");
        ZReportNavButton.setFont(new Font("Arial", Font.BOLD, 20));
        ZReportNavButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zReportPanel.loadZReportData("2025-02-26"); // Ensure data loads before switching
                cardLayout.show(cardPanel, "zReport"); // Use correct key "zReport"
            }
        });

        JButton salesNavButton = new JButton("Sales Report");
        salesNavButton.setFont(new Font("Arial", Font.BOLD, 20));
        salesNavButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "SalesReport");
            }
        });

        JButton xReportButton = new JButton("X-Report");
        xReportButton.setFont(new Font("Arial", Font.BOLD, 20));
        xReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "xReport");
            }
        });

        JButton productUseNavButton = new JButton("Product Usage");
        productUseNavButton.setFont(new Font("Arial", Font.BOLD, 20));
        productUseNavButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "productUse");
            }
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
        navbarPanel.add(xReportButton);
        navbarPanel.add(ZReportNavButton);
        navbarPanel.add(salesNavButton);
        navbarPanel.add(productUseNavButton);
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

        String[] inventoryColumns = { "Item", "Inventory Count", "Order More" };
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

        String[] priceColumns = { "Item", "Price ($)", "Edit Price" };
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

        zReportPanel = new ZReportPanel(connection);
        salesReport = new SalesReport();

        xReportPanel = new XReportPanel(connection);

        productUsePanel = new ProductUsePanel(connection);

        // Add panels to card layout
        cardPanel.add(inventoryPanel, "inventory");
        cardPanel.add(pricePanel, "price");
        cardPanel.add(employeePanel, "employee");
        cardPanel.add(reportPanel, "report");
        cardPanel.add(xReportPanel, "xReport");
        cardPanel.add(productUsePanel, "productUse");
        cardPanel.add(zReportPanel, "zReport");
        cardPanel.add(salesReport, "SalesReport");

        add(cardPanel, BorderLayout.CENTER);

        loadInventory();
        loadPriceTable();
        loadReportData();
        xReportPanel.loadXReportData("2025-02-26");

        // Show inventory view by default
        cardLayout.show(cardPanel, "inventory");

        setVisible(true);
    }
    
    /**
     * Displays dialog box with options to add a product.
     */
    private void showAddProductDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Product", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 600);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField(20);

        JLabel priceLabel = new JLabel("Price (in cents):");
        JTextField priceField = new JTextField(20);

        JLabel initialInventoryLabel = new JLabel("Initial Inventory:");
        JTextField initialInventoryField = new JTextField(20);

        JLabel productTypeLabel = new JLabel("Product Type:");

        JPanel productTypePanel = new JPanel(new GridLayout(12, 1));
        ButtonGroup productTypeButtonGroup = new ButtonGroup();

        ArrayList<String> productTypes = new ArrayList<>();
        ArrayList<String> productTypesReadable = new ArrayList<>();
        ResultSet rs = db.query(
                "SELECT enumlabel FROM pg_enum JOIN pg_type ON pg_type.oid = pg_enum.enumtypid WHERE pg_type.typname = 'product_type';");

        try {
            while (rs.next()) {
                String productType = rs.getString("enumlabel");
                productTypes.add(productType);
                productTypesReadable.add(Utils.snakeToReadable(productType));
            }
        } catch (SQLException se) {
            System.err.println(se);
            System.exit(1);
        }

        for (int i = 0; i < productTypes.size(); i++) {
            JRadioButton option = new JRadioButton(productTypesReadable.get(i));
            option.setActionCommand(productTypes.get(i));
            productTypeButtonGroup.add(option);
            productTypePanel.add(option);
        }

        Component[] components = new Component[] { nameLabel, nameField, priceLabel, priceField, initialInventoryLabel,
                initialInventoryField };

        gbc.weighty = 1.0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 2; col++) {
                int idx = row * 2 + col;
                gbc.gridx = col;
                gbc.gridy = row;
                formPanel.add(components[idx], gbc);
            }
        }

        gbc.weighty = 2.0;
        gbc.gridy = 3;

        gbc.gridx = 0;
        formPanel.add(productTypeLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(productTypePanel, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();

            int price;
            try {
                price = Integer.parseInt(priceField.getText().trim());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Price should be an integer",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (price < 0) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Price must be non-negative",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int inventory;
            try {
                inventory = Integer.parseInt(initialInventoryField.getText().trim());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Initial inventory should be an integer",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (inventory < 0) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Inventory must be non-negative",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            ButtonModel productTypeSelection = productTypeButtonGroup.getSelection();

            if (name.isEmpty() || productTypeSelection == null) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Name, price, inventory and product type cannot be empty",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String productType = productTypeSelection.getActionCommand();

            if (addProduct(name, price, productType, inventory)) {
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

    /**
     * Adds product to database.
     *
     * @param name name of the product
     * @param price price of the product
     * @param type type of the product
     * @param inventory initial inventory of the product
     * @return whether the database query was successful
     */
    private boolean addProduct(String name, int price, String type, int inventory) {
        Db db = new Db();

        if (db.query(
                "INSERT INTO product (product_type, name, price, inventory) VALUES ('%s', '%s', %s, %s) RETURNING id;",
                type, name, price, inventory) == null) {
            return false;
        }
        return true;
    }

    /**
     * Creates connection to database.
     */
    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:postgresql://csce-315-db.engr.tamu.edu/team_61_db",
                    "team_61",
                    "6161");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the inventory panel's table.
     */
    private void loadInventory() {
        tableModel.setRowCount(0); // Clear the table before reloading
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT product.name, product.inventory AS inventory_count " +
                            "FROM product ORDER BY inventory_count DESC;");
            while (rs.next()) {
                String name = rs.getString("name");
                int inventory = rs.getInt("inventory_count");
                tableModel.addRow(new Object[] { name, inventory, "Order More" });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Refreshes the price panel's table.
     */
    private void loadPriceTable() {
        priceTableModel.setRowCount(0); // Clear table before reloading
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT product.name, product.price FROM product ORDER BY price DESC;");
            while (rs.next()) {
                String name = rs.getString("name");
                // Convert cents to dollars for display
                double price = rs.getDouble("price") / 100.0;
                priceTableModel.addRow(new Object[] {
                        name,
                        String.format("$%.2f", price),
                        "Edit Price"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads data for report.
     */
    private void loadReportData() {
        ArrayList<String> itemNames = new ArrayList<>();
        ArrayList<Integer> inventoryCounts = new ArrayList<>();
        ArrayList<String> recentItems = new ArrayList<>();
        ArrayList<Double> recentPrices = new ArrayList<>();
        ArrayList<String> recentPayments = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();

            // Query for Low Inventory Items
            ResultSet rs = stmt
                    .executeQuery("SELECT name, inventory FROM product WHERE inventory < 50 ORDER BY inventory ASC;");
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
                            "LIMIT 5;");

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

    /**
     * Button Renderer for Inventory "Order More" column.
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {
        /**
         * Default constructor, sets text to "Order More".
         */
        public ButtonRenderer() {
            setText("Order More");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }

    /**
     * Button Editor for Inventory "Order More" column.
     */
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;

        /**
         * Constructor
         *
         * @param checkBox checkBox that confirms order more action
         */
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
                JTable table, Object value, boolean isSelected, int row, int column) {
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

    /**
     * Orders more items by updating inventory for products in the database.
     *
     * @param itemName name of the item
     * @param row row of the item in table
     */
    private void orderMoreItem(String itemName, int row) {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter amount to add for " + itemName + ":",
                "Order More",
                JOptionPane.PLAIN_MESSAGE);
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
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Updates inventory in database
     *
     * @param itemName name of product to update
     * @param amountToAdd additional inventory to add
     */
    private void updateInventoryInDatabase(String itemName, int amountToAdd) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE product SET inventory = inventory + ? WHERE name = ?");
            pstmt.setInt(1, amountToAdd);
            pstmt.setString(2, itemName);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Renderer for Price "Edit Price" button column.
     */
    class PriceButtonRenderer extends JButton implements TableCellRenderer {
        /**
         * Default constructor, sets to text to "Edit Price"
         */
        public PriceButtonRenderer() {
            setText("Edit Price");
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return this;
        }
    }

    /**
     * Editor for Price "Edit Price" button column.
     */
    class PriceButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int selectedRow;

        /**
         * Constructor
         *
         * @param checkBox checkBox that confirms change price action
         */
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
                JTable table, Object value, boolean isSelected, int row, int column) {
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

    /**
     * Edits price of an item in the database based on the entry in price table.
     *
     * @param itemName name of the product to update
     * @param row row of the product in price table
     */
    private void editPriceItem(String itemName, int row) {
        String input = JOptionPane.showInputDialog(
                this,
                "Enter new price for " + itemName + " in dollars:",
                "Edit Price",
                JOptionPane.PLAIN_MESSAGE);
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
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Updates price of an product in the database.
     *
     * @param itemName name of the product to update
     * @param newPriceCents new price of the product in cents
     */
    private void updatePriceInDatabase(String itemName, int newPriceCents) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE product SET price = ? WHERE name = ?");
            pstmt.setInt(1, newPriceCents);
            pstmt.setString(2, itemName);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs out of manager page and spawn new Job Selection Page
     */
    private void handleLogout() {
        dispose();
        JFrame jobSelectionFrame = new JobSelectionPage(new Db(), employeeId);
        jobSelectionFrame.setVisible(true);
    }

    /**
     * Testing main function
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManagerPage mp = new ManagerPage(1);
            mp.setVisible(true);
        });
    }
}
