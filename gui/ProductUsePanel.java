import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * Panel to display the use of products.
 */
public class ProductUsePanel extends JPanel {
    private Connection connection;
    private JTextField timeStartField;
    private DefaultTableModel tableModel;
    private JTextField timeEndField;
    private JTextField dateField;
    private JTable ProductUseTable;

    /**
     * Constructor given a database connection.
     *
     * @param connection database connection
     */
    public ProductUsePanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Check connection
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed in ProductUse!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Header
        JPanel topPanel = new JPanel();

        JLabel headerLabel = new JLabel("Product Usage Chart  ", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 30));
        headerLabel.setForeground(Color.decode("#AE82D9"));
        topPanel.add(headerLabel, BorderLayout.NORTH);

        // Time Window
        JLabel dateLabel = new JLabel("Enter Date (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 16));
        dateField = new JTextField(10);
        dateField.setText("2025-02-26"); // Default

        JLabel startLabel = new JLabel("  Enter Time as Hour (0-23):");
        startLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeStartField = new JTextField(8);
        timeStartField.setText("0"); // Default

        JLabel endLabel = new JLabel("");
        endLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timeEndField = new JTextField(8);
        timeEndField.setText("23"); // Default

        JButton loadButton = new JButton("Load Chart");

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadProduceUseData(timeStartField.getText(), timeEndField.getText(), dateField.getText());

            }
        });

        topPanel.add(dateLabel);
        topPanel.add(dateField);
        topPanel.add(startLabel);
        topPanel.add(timeStartField);
        topPanel.add(endLabel);
        topPanel.add(timeEndField);
        topPanel.add(loadButton);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Amount Used");

        ProductUseTable = new JTable(tableModel);

        ProductUseTable.setFont(new Font("Arial", Font.PLAIN, 14));
        ProductUseTable.setRowHeight(30);
        ProductUseTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        add(new JScrollPane(ProductUseTable), BorderLayout.CENTER);

    }

    /**
     * Loads product use data from the database given time interval and date.
     *
     * @param startTime start of time interval
     * @param endTime end of time interval
     * @param date day of inspection
     */
    public void loadProduceUseData(String startTime, String endTime, String date) {

        // System.out.println("Loading Product Usage");

        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection lost!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        tableModel.setRowCount(0);

        try {
            String query = "SELECT DATE(transaction.time) AS day, " +
                    "product.name AS product_name, COUNT(transaction_item.product_id) AS transaction_count " +
                    "FROM transaction_item " +
                    "JOIN transaction ON transaction_item.transaction_id = transaction.id " +
                    "JOIN product ON product.id = transaction_item.product_id " +
                    "WHERE DATE(transaction.time) = CAST(? AS DATE) " +
                    "AND EXTRACT(HOUR FROM transaction.time) BETWEEN CAST(? AS INT) AND CAST(? AS INT) " +
                    "GROUP BY product.name, day";

            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, date);
            stmt.setString(2, startTime);
            stmt.setString(3, endTime);
            // System.out.println("Executing SQL Query...");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int totalOrders = rs.getInt("transaction_count");
                String productName = rs.getString("product_name");

                tableModel.addRow(new Object[] {
                        productName,
                        totalOrders
                });
            }

            rs.close();
            stmt.close();
            // System.out.println("Product Usage Loaded Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading Product Use data: " + e.getMessage(), "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
