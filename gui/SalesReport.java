import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SalesReport extends JPanel {
    private JTable theSalesReport;
    private DefaultTableModel tableModel;
    private JTextField dateInputFieldOne;
    private JTextField dateInputFieldTwo;
    private Db db;

    public SalesReport() {
        db = new Db();
        setLayout(new BorderLayout());

        // Top Panel Setup
        JPanel topPanel = new JPanel();
        JLabel headerLabel = new JLabel("Sales Report: Sales Per Item", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.BLUE);

        JLabel dateLabel = new JLabel("Enter Times (YYYY-MM-DD):");
        dateInputFieldOne = new JTextField(10);
        dateInputFieldOne.setText("2025-02-26"); // Default to a working date
        dateInputFieldTwo = new JTextField(10);
        dateInputFieldTwo.setText("2025-02-26");
        JButton loadButton = new JButton("Load Report");

        // Load report when button is clicked
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSalesReport(dateInputFieldOne.getText(), dateInputFieldTwo.getText());
            }
        });

        topPanel.add(headerLabel);
        topPanel.add(dateLabel);
        topPanel.add(dateInputFieldOne);
        topPanel.add(dateInputFieldTwo);
        topPanel.add(loadButton);

        // Table Setup
        String[] columnNames = {"Menu Item", "Total Orders", "Total Sales ($)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        theSalesReport = new JTable(tableModel);

        // Table Styling
        theSalesReport.setFont(new Font("Arial", Font.PLAIN, 14));
        theSalesReport.setRowHeight(30);
        theSalesReport.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(theSalesReport);

        // Add Components
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initial data load
        loadSalesReport("2025-02-26", "2025-02-26");
    }

    public void loadSalesReport(String startTime, String endTime) {
        tableModel.setRowCount(0); // Clear previous data

        String sql = "SELECT p.name AS menu_item, COUNT(DISTINCT t.id) AS total_orders, " +
                    "SUM(CASE WHEN t.payment_type = 'cash' THEN ti.subtotal ELSE 0 END) AS cash_sales " +
                    "FROM product p " +
                    "JOIN transaction_item ti ON p.id = ti.product_id " +
                    "JOIN transaction t ON t.id = ti.transaction_id " +
                    "WHERE t.time BETWEEN CAST('%s' AS TIMESTAMP) AND CAST('%s' AS TIMESTAMP) " +
                    "GROUP BY p.name " +
                    "ORDER BY p.name;";

        try (ResultSet rs = db.query(sql, startTime, endTime)) {
            // System.out.println("Executing SQL Query: " + sql);
            // System.out.println("Parameters: " + startTime + " to " + endTime);

            while (rs.next()) {
                String menuItem = rs.getString("menu_item");
                int totalOrders = rs.getInt("total_orders");
                double cashSales = rs.getDouble("cash_sales") / 100.0; // Convert cents to dollars

                // Debugging Output
                // System.out.println("Item: " + menuItem + " | Orders: " + totalOrders + 
                //                  " | Cash Sales: " + cashSales);

                tableModel.addRow(new Object[]{
                    menuItem,
                    totalOrders,
                    String.format("$%.2f", cashSales)
                });
            }
            // System.out.println("Sales Report Loaded Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading Sales Report data: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
