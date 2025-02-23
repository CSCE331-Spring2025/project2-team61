import java.sql.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ManagerPage extends JFrame {
    private JTable inventoryTable, priceTable;
    private DefaultTableModel tableModel, priceTableModel;
    private Connection connection;

    public ManagerPage() {
        super("Manager Inventory Interface");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
         // Logout Button
         JButton logoutButton = new JButton("Logout [>]");
         logoutButton.setFont(new Font("Arial", Font.BOLD, 30));
         logoutButton.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 handleLogout();
             }
         });
         JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(logoutButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Left Panel (Inventory Section)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(600, getHeight()));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        leftPanel.setBackground(Color.decode("#E0F6F1"));

        JLabel inventoryLabel = new JLabel("Inventory");
        inventoryLabel.setFont(new Font("Arial", Font.BOLD, 50));
        inventoryLabel.setForeground(Color.BLACK);
        inventoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(inventoryLabel, BorderLayout.NORTH);

        // Inventory Table with Order More column
        String[] columns = {"Item", "Inventory Count", "Order More"};
        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Arial", Font.PLAIN, 20));
        inventoryTable.setRowHeight(40);

        // Add Button Column
        TableColumn orderMoreColumn = inventoryTable.getColumnModel().getColumn(2);
        orderMoreColumn.setCellRenderer(new ButtonRenderer());
        orderMoreColumn.setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        leftPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Right Panel (Price Table)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(600, getHeight()));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(Color.decode("#E0F6F1"));

        JLabel priceLabel = new JLabel("Price Table");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 50));
        priceLabel.setForeground(Color.BLACK);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(priceLabel, BorderLayout.NORTH);

        // Price Table
        String[] priceColumns = {"Item", "Price ($)"};
        priceTableModel = new DefaultTableModel(priceColumns, 0);
        priceTable = new JTable(priceTableModel);
        priceTable.setFont(new Font("Arial", Font.PLAIN, 20));
        priceTable.setRowHeight(40);

        JScrollPane priceScrollPane = new JScrollPane(priceTable);
        rightPanel.add(priceScrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        // Connect to database and load data
        connectToDatabase();
        loadInventory();
        loadPriceTable();

        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/team_61_db", "team_61", "6161");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInventory() {
        tableModel.setRowCount(0); // Clear the table before reloading
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT product.name, product.inventory AS inventory_count FROM product ORDER BY inventory_count DESC;");
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
            ResultSet rs = stmt.executeQuery("SELECT product.name, product.price FROM product ORDER BY price DESC;");
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price") / 100.0; // Convert cents to dollars
                priceTableModel.addRow(new Object[]{name, String.format("$%.2f", price)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Button Renderer for JTable
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setText("Order More");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // Button Editor for JTable
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
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
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
        String input = JOptionPane.showInputDialog(this, "Enter amount to add for " + itemName + ":", "Order More", JOptionPane.PLAIN_MESSAGE);
        if (input != null) {
            try {
                int amountToAdd = Integer.parseInt(input);
                if (amountToAdd > 0) {
                    updateInventoryInDatabase(itemName, amountToAdd);
                    int updatedInventory = (int) tableModel.getValueAt(row, 1) + amountToAdd;
                    tableModel.setValueAt(updatedInventory, row, 1);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateInventoryInDatabase(String itemName, int amountToAdd) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE product SET inventory = inventory + ? WHERE name = ?");
            pstmt.setInt(1, amountToAdd);
            pstmt.setString(2, itemName);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void handleLogout() {
        dispose();
        JFrame jobSelectionFrame = new JobSelectionPage();
        jobSelectionFrame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManagerPage::new);
    }
}
