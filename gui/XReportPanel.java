import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

/**
 * This class represents a panel for displaying an X-Report, 
 * which shows sales data per hour for a selected date.
 * The report is retrieved from a PostgreSQL database.
 * 
 * The panel includes a table to display transaction statistics
 * and allows users to input a date to generate the report dynamically.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class XReportPanel extends JPanel {
    private JTable xReportTable;
    private DefaultTableModel tableModel;
    private Connection connection;
    private JTextField dateInputField;

    /**
     * Constructs the XReportPanel with a database connection.
     * Initializes the user interface, including a table for displaying sales data.
     * 
     * @param connection The database connection used to fetch X-Report data.
     */
    public XReportPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout());

        // Ensure connection is valid
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Header Panel with Date Selector
        JPanel topPanel = new JPanel();
        JLabel headerLabel = new JLabel("X-Report: Sales Per Hour", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.BLUE);

        JLabel dateLabel = new JLabel("Enter Date (YYYY-MM-DD):");
        dateInputField = new JTextField(10);
        dateInputField.setText("2025-02-26"); // Default to a working date
        JButton loadButton = new JButton("Load Report");

        // Load report when button is clicked
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadXReportData(dateInputField.getText()); // Pass user-selected date
            }
        });

        topPanel.add(headerLabel);
        topPanel.add(dateLabel);
        topPanel.add(dateInputField);
        topPanel.add(loadButton);

        // Initialize Table Model
        String[] columnNames = {
            "Hour", "Total Orders", "Total Sales ($)", "Cash ($)", "Card ($)", "Check ($)", "Gift Card ($)", 
            "Returns ($)", "Voids ($)", "Discards ($)"  
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        xReportTable = new JTable(tableModel);

        // Table Styling
        xReportTable.setFont(new Font("Arial", Font.PLAIN, 14));
        xReportTable.setRowHeight(30);
        xReportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(xReportTable);

        // Add Components
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Ensure everything is initialized before loading data
        if (tableModel != null) {
            loadXReportData("2025-02-26");
        }
    }

    /**
     * Loads the X-Report data from the database for a given date.
     * Retrieves sales and transaction data for each hour of the selected date.
     * 
     * @param selectedDate The date for which the X-Report should be generated, in YYYY-MM-DD format.
     */
    public void loadXReportData(String selectedDate) {
        System.out.println("Loading X-Report for date: " + selectedDate);
    
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection lost!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        tableModel.setRowCount(0); // Clear previous data
    
        try {
            PreparedStatement stmt = connection.prepareStatement(
                "SELECT EXTRACT(HOUR FROM t.time) AS hour, COUNT(DISTINCT t.id) AS total_orders, SUM(ti.subtotal) AS total_sales, " +
                "SUM(CASE WHEN t.payment_type = 'cash' THEN ti.subtotal ELSE 0 END) AS cash_sales, " +
                "SUM(CASE WHEN t.payment_type = 'card' THEN ti.subtotal ELSE 0 END) AS card_sales, " +
                "SUM(CASE WHEN t.payment_type = 'check' THEN ti.subtotal ELSE 0 END) AS check_sales, " +
                "SUM(CASE WHEN t.payment_type = 'gift_card' THEN ti.subtotal ELSE 0 END) AS gift_card_sales, " +
                "SUM(CASE WHEN t.transaction_type = 'return' THEN ti.subtotal ELSE 0 END) AS returns, " +  
                "SUM(CASE WHEN t.transaction_type = 'void' THEN ti.subtotal ELSE 0 END) AS voids, " +      
                "SUM(CASE WHEN t.transaction_type = 'discard' THEN ti.subtotal ELSE 0 END) AS discards " + 
                "FROM transaction t " +
                "JOIN transaction_item ti ON t.id = ti.transaction_id " +
                "WHERE DATE(t.time) = CAST(? AS DATE) " +  
                "GROUP BY hour ORDER BY hour;"
            );

            stmt.setString(1, selectedDate); // Set the selected date
            System.out.println("Executing SQL Query...");
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                int hour = rs.getInt("hour");
                int totalOrders = rs.getInt("total_orders");
                double totalSales = rs.getDouble("total_sales") / 100.0;
                double cashSales = rs.getDouble("cash_sales") / 100.0;
                double cardSales = rs.getDouble("card_sales") / 100.0;
                double checkSales = rs.getDouble("check_sales") / 100.0;
                double giftCardSales = rs.getDouble("gift_card_sales") / 100.0;
                double returns = rs.getDouble("returns") / 100.0;
                double voids = rs.getDouble("voids") / 100.0;
                double discards = rs.getDouble("discards") / 100.0;
            
                // Debugging Output
                System.out.println("Hour: " + hour + " | Orders: " + totalOrders + " | Sales: " + totalSales +
                    " | Returns: " + returns + " | Voids: " + voids + " | Discards: " + discards);
            
                tableModel.addRow(new Object[]{
                    hour,
                    totalOrders,
                    String.format("$%.2f", totalSales),
                    String.format("$%.2f", cashSales),
                    String.format("$%.2f", cardSales),
                    String.format("$%.2f", checkSales),
                    String.format("$%.2f", giftCardSales),
                    String.format("$%.2f", returns),  
                    String.format("$%.2f", voids),     
                    String.format("$%.2f", discards)   
                });
            }
    
            System.out.println("X-Report Loaded Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading X-Report data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }  
}

