import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ZReportPanel extends JPanel {
    private DefaultTableModel tableModel;
    private Connection connection;
    private JTextField dateInputField;
    private JButton closeDayButton;
    private JTextArea reportSummaryArea;
    private static final double TAX_RATE = 8.25; // Tax rate as percentage

    public ZReportPanel(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Ensure connection is valid
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Header Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JLabel headerLabel = new JLabel("Z-Report: Daily Summary", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setForeground(new Color(0, 102, 102));

        // Date selector
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateInputField = new JTextField(10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateInputField.setText(sdf.format(new Date())); // Default to today

        JButton loadButton = new JButton("View Report");
        loadButton.setBackground(new Color(240, 240, 240));
        loadButton.addActionListener(e -> loadZReportData(dateInputField.getText()));

        topPanel.add(headerLabel);
        topPanel.add(dateLabel);
        topPanel.add(dateInputField);
        topPanel.add(loadButton);

        // Summary area
        reportSummaryArea = new JTextArea(20, 50);
        reportSummaryArea.setEditable(false);
        reportSummaryArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        reportSummaryArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane summaryScrollPane = new JScrollPane(reportSummaryArea);
        summaryScrollPane.setBorder(BorderFactory.createTitledBorder("Daily Summary"));

        // Bottom panel with Close Day button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        closeDayButton = new JButton("CLOSE DAY");
        closeDayButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeDayButton.setBackground(new Color(204, 0, 0));
        closeDayButton.setForeground(Color.WHITE);
        closeDayButton.setPreferredSize(new Dimension(150, 50));

        closeDayButton.addActionListener(e -> confirmAndCloseDay());
        bottomPanel.add(closeDayButton);

        // Add Components
        add(topPanel, BorderLayout.NORTH);
        add(summaryScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load data for current date
        loadZReportData(dateInputField.getText());
    }

    // Load Z-Report data without hourly breakdown
    public void loadZReportData(String selectedDate) {
        System.out.println("Loading Z-Report for date: " + selectedDate);

        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection lost!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Generate summary with totals only
            generateDailySummary(selectedDate);

            System.out.println("Z-Report Loaded Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading Z-Report data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Generate a comprehensive daily summary with tax calculation
    private void generateDailySummary(String selectedDate) throws SQLException {
        // Get daily totals
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT " +
                        "COUNT(DISTINCT t.id) AS total_transactions, " +
                        "SUM(ti.subtotal) AS total_sales, " +
                        "SUM(CASE WHEN t.payment_type = 'cash' THEN ti.subtotal ELSE 0 END) AS cash_total, " +
                        "SUM(CASE WHEN t.payment_type = 'card' THEN ti.subtotal ELSE 0 END) AS card_total, " +
                        "SUM(CASE WHEN t.payment_type = 'check' THEN ti.subtotal ELSE 0 END) AS check_total, " +
                        "SUM(CASE WHEN t.payment_type = 'gift_card' THEN ti.subtotal ELSE 0 END) AS gift_card_total, " +
                        "SUM(CASE WHEN t.transaction_type = 'return' THEN ti.subtotal ELSE 0 END) AS returns_total, " +
                        "SUM(CASE WHEN t.transaction_type = 'void' THEN ti.subtotal ELSE 0 END) AS voids_total, " +
                        "SUM(CASE WHEN t.transaction_type = 'discard' THEN ti.subtotal ELSE 0 END) AS discards_total, " +
                        "COUNT(CASE WHEN t.transaction_type = 'return' THEN 1 END) AS returns_count, " +
                        "COUNT(CASE WHEN t.transaction_type = 'void' THEN 1 END) AS voids_count, " +
                        "COUNT(CASE WHEN t.transaction_type = 'discard' THEN 1 END) AS discards_count " +
                        "FROM transaction t " +
                        "JOIN transaction_item ti ON t.id = ti.transaction_id " +
                        "WHERE DATE(t.time) = CAST(? AS DATE)"
        );

        stmt.setString(1, selectedDate);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int totalTransactions = rs.getInt("total_transactions");
            double totalSales = rs.getDouble("total_sales");
            double cashTotal = rs.getDouble("cash_total");
            double cardTotal = rs.getDouble("card_total");
            double checkTotal = rs.getDouble("check_total");
            double giftCardTotal = rs.getDouble("gift_card_total");
            double returnsTotal = rs.getDouble("returns_total");
            double voidsTotal = rs.getDouble("voids_total");
            double discardsTotal = rs.getDouble("discards_total");
            int returnsCount = rs.getInt("returns_count");
            int voidsCount = rs.getInt("voids_count");
            int discardsCount = rs.getInt("discards_count");

            // Calculate net revenue (before tax)
            double netRevenue = totalSales - returnsTotal - voidsTotal - discardsTotal;

            // Calculate tax
            double salesTax = netRevenue * (TAX_RATE / 100);
            double totalWithTax = netRevenue + salesTax;

            // Build the summary
            StringBuilder summary = new StringBuilder();
            summary.append("====================== Z-REPORT SUMMARY ======================\n\n");
            summary.append("Date: ").append(selectedDate).append("\n\n");

            summary.append("SALES SUMMARY\n");
            summary.append("---------------------------------------------\n");
            summary.append(String.format("Total Transactions: %d\n", totalTransactions));
            summary.append(String.format("Gross Sales: $%.2f\n", totalSales));
            summary.append(String.format("Net Revenue (Before Tax): $%.2f\n", netRevenue));
            summary.append(String.format("Sales Tax (%.2f%%): $%.2f\n", TAX_RATE, salesTax));
            summary.append(String.format("Total With Tax: $%.2f\n\n", totalWithTax));

            summary.append("PAYMENT METHODS\n");
            summary.append("---------------------------------------------\n");
            summary.append(String.format("Cash Payments: $%.2f\n", cashTotal));
            summary.append(String.format("Card Payments: $%.2f\n", cardTotal));
            summary.append(String.format("Check Payments: $%.2f\n", checkTotal));
            summary.append(String.format("Gift Card Payments: $%.2f\n\n", giftCardTotal));

            summary.append("ADJUSTMENTS\n");
            summary.append("---------------------------------------------\n");
            summary.append(String.format("Returns: %d ($%.2f)\n", returnsCount, returnsTotal));
            summary.append(String.format("Voids: %d ($%.2f)\n", voidsCount, voidsTotal));
            summary.append(String.format("Discards: %d ($%.2f)\n\n", discardsCount, discardsTotal));

            // Update the summary text area
            reportSummaryArea.setText(summary.toString());
        }
    }

    // Confirm before closing the day
    private void confirmAndCloseDay() {
        String selectedDate = dateInputField.getText();

        // Confirmation dialog
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to close the day?\n" +
                        "This will generate a Z-Report and reset all daily totals.\n" +
                        "This action cannot be undone.",
                "Confirm Close Day",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            closeDay(selectedDate);
        }
    }

    // Close the day and reset totals
    private void closeDay(String selectedDate) {
        try {
            // Load Z-Report data first to display it
            loadZReportData(selectedDate);

            // Offer to print current Z-Report before resetting
            int printOption = JOptionPane.showConfirmDialog(
                    this,
                    "Would you like to print the Z-Report before resetting?",
                    "Print Report",
                    JOptionPane.YES_NO_OPTION
            );

            if (printOption == JOptionPane.YES_OPTION) {
                try {
                    // Create a formatted report for printing
                    JTextArea printArea = new JTextArea(reportSummaryArea.getText());
                    printArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    printArea.print();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                            this,
                            "Error printing report: " + e.getMessage(),
                            "Print Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

            // Reset Z-Report data to zero
            // TODO: reset
            resetZReportValues();

            // Notify success
            JOptionPane.showMessageDialog(
                    this,
                    "Z-Report has been reset to zero.",
                    "Reset Complete",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "Error resetting Z-Report: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // TODO
    // Helper method to reset all Z-Report values to zero
    private void resetZReportValues() {
        // Clear the report text area
    }
}