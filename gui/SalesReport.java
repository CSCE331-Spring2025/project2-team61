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
    Db db;

    public SalesReport() {
        db = new Db();
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JLabel headerLabel = new JLabel("Sales Report: Sales Per Item", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.BLUE);

        JLabel dateLabel = new JLabel("Enter Times (YYYY-MM-DD HR:MN:SC):");
        dateInputFieldOne = new JTextField(10);
        dateInputFieldOne.setText("2025-02-26"); // Default to a working date
        dateInputFieldTwo = new JTextField(10);
        dateInputFieldTwo.setText("2025-02-26");
        JButton loadButton = new JButton("Load Report");

        // Load report when button is clicked
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSalesReport(dateInputFieldOne.getText(), dateInputFieldTwo.getText()); // Pass user-selected date
            }
        });

        topPanel.add(headerLabel);
        topPanel.add(dateLabel);
        topPanel.add(dateInputFieldOne);
        topPanel.add(dateInputFieldTwo);
        topPanel.add(loadButton);

        String[] columnNames = { "Menu Item", "Total Orders", "Total Sales ($)"};
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

        // Ensure everything is initialized before loading data
        if (tableModel != null) {
            loadSalesReport("2025-02-26", "2025-02-26");
        }
    }

    public void loadSalesReport(String selectedDateOne, String selectedDateTwo) {}
}
