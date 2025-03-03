import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;



//ProductUsePanel

public class ProductUsePanel extends JPanel {
    private Connection connection;
    private JTextField timeStartField;
    private DefaultTableModel tableModel;
    private JTextField timeEndField;
    private JTable ProductUseTable;

        public ProductUsePanel(Connection connection) {
            this.connection = connection;
        setLayout(new BorderLayout());

        // Check connection
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection failed in ProductUse!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Header 
        JPanel topPanel = new JPanel();

        JLabel headerLabel = new JLabel("Product Usage Chart", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.decode("#AE82D9"));
        topPanel.add(headerLabel, BorderLayout.NORTH);


        // Time Window
        JLabel startLabel = new JLabel("Enter Start Time (XX:XX):"); //TODO: is this the right time format
        timeStartField = new JTextField(5);
        timeStartField.setText("01:00"); // Default 

        JLabel endLabel = new JLabel("Enter End Time (XX:XX):");
        timeEndField = new JTextField(5);
        timeEndField.setText("23:00"); // Default 

        JButton loadButton = new JButton("Load Chart");

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadProduceUseData(timeStartField.getText(), timeEndField.getText());

            }
        });

        //topPanel.add(headerLabel);
        topPanel.add(startLabel);
        topPanel.add(endLabel);
        topPanel.add(timeStartField);
        topPanel.add(timeEndField);
        topPanel.add(loadButton);

        //topPanel.add(inputPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Product Name");
        tableModel.addColumn("Amount Used");
      
        ProductUseTable = new JTable(tableModel);
        add(new JScrollPane(ProductUseTable), BorderLayout.CENTER);





        // (inventory at time start) - (inventory at time end)



    }

    public void loadProduceUseData(String startTime, String endTime) {

        // TODO what to do for this, needs to get inventory @ specified time

        System.out.println("Loading Product Usage");
    
        if (connection == null) {
            JOptionPane.showMessageDialog(this, "Database connection lost!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        tableModel.setRowCount(0);

        try {
        String query = "SELECT product.name, (time1.inventory - time2.inventory) AS product_usage " +
                       "FROM inventory time1 JOIN inventory time2 ON time1.name = time2.name " +
                       "JOIN products product ON product.name = time1.name " +
                       "WHERE time1.time = ? AND time2.time = ?";
        ;
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, startTime);
        stmt.setString(2, endTime);
        System.out.println("Executing SQL Query...");
        ResultSet rs = stmt.executeQuery();


        while (rs.next()) {
            //int productId = rs.getInt("product_id");
            String productName = rs.getString("product_name");
            int usage = rs.getInt("usage");

            tableModel.addRow(new Object[]{productName, usage});
        }

        rs.close();
        stmt.close();
        System.out.println("Product Usage Loaded Successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading X-Report data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}