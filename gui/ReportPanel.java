import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

class ReportPanel extends JPanel {
    private ArrayList<String> itemNames;
    private ArrayList<Integer> inventoryCounts;
    private TableOne tableOne; // Reference to the low supply table
    private TableTwo tableTwo; // Reference to the recent order table


    public ReportPanel() {
        this.itemNames = new ArrayList<>();
        this.inventoryCounts = new ArrayList<>();
        setLayout(new BorderLayout());

        // Center Panel with a Grid Layout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // North Panel (Graphs)
        JPanel northPanel = new JPanel(new GridLayout(1, 3));
        northPanel.add(new GraphOne());
        northPanel.add(new GraphTwo());
        northPanel.add(new GraphThree());

        // South Panel (Tables)
        tableOne = new TableOne();
        tableTwo = new TableTwo(); 
        JPanel southPanel = new JPanel(new GridLayout(1, 2));
        southPanel.add(tableOne);
        southPanel.add(tableTwo);

        // Add northPanel to centerPanel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.9;
        centerPanel.add(northPanel, gbc);

        // Add southPanel to centerPanel
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        centerPanel.add(southPanel, gbc);

        // Add center panel to the main panel
        add(centerPanel, BorderLayout.CENTER);
    }

    public void updateData(ArrayList<String> itemNames, ArrayList<Integer> inventoryCounts) {
        this.itemNames = itemNames;
        this.inventoryCounts = inventoryCounts;
        repaint();

        // Forward the data to TableOne for low-supply tracking
        tableOne.updateLowSupplyTable(itemNames, inventoryCounts);
    }

    public void updateLowSupplyTable(ArrayList<String> itemNames, ArrayList<Integer> inventoryCounts) {
        tableOne.updateLowSupplyTable(itemNames, inventoryCounts);
    }
    
    public void updateRecentOrders(ArrayList<String> items, ArrayList<Double> prices, ArrayList<String> paymentMethods) {
        if (tableTwo != null) { // Prevent NullPointerException
            tableTwo.updateRecentOrders(items, prices, paymentMethods);
        } else {
            System.err.println("Error: TableTwo (Recent Orders) is not initialized!");
        }
    }

    class GraphOne extends JPanel {
        public GraphOne() {
            setBackground(Color.BLUE);
        }
    }

    class GraphTwo extends JPanel {
        public GraphTwo() {
            setBackground(Color.GREEN);
        }
    }

    class GraphThree extends JPanel {
        public GraphThree() {
            setBackground(Color.RED);
        }
    }

    class TableOne extends JPanel {
        private JTable lowSupplyTable;
        private DefaultTableModel tableModel;

        public TableOne() {
            setLayout(new BorderLayout());
            
            JLabel headerLabel = new JLabel("⚠️ Low Supply Items (≤ 50)", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(Color.RED); 

            // Table Column Names
            String[] columnNames = {"Item", "Inventory"};

            // Create Table Model
            tableModel = new DefaultTableModel(columnNames, 0);
            lowSupplyTable = new JTable(tableModel);

            // Table Styling
            lowSupplyTable.setFont(new Font("Arial", Font.PLAIN, 14));
            lowSupplyTable.setRowHeight(30);
            lowSupplyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            JScrollPane scrollPane = new JScrollPane(lowSupplyTable);

            add(headerLabel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        // Update Table with Items ≤ 50
        public void updateLowSupplyTable(ArrayList<String> itemNames, ArrayList<Integer> inventoryCounts) {
            tableModel.setRowCount(0); // Clear previous data

            for (int i = 0; i < itemNames.size(); i++) {
                if (inventoryCounts.get(i) <= 50) {
                    tableModel.addRow(new Object[]{itemNames.get(i), inventoryCounts.get(i)});
                }
            }
        }
    }

    class TableTwo extends JPanel {
        private JTable recentOrdersTable;
        private DefaultTableModel tableModel;
    
        public TableTwo() {
            setLayout(new BorderLayout());
    
            // Create Header Label
            JLabel headerLabel = new JLabel("🛒 5 Most Recent Orders", SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
            headerLabel.setForeground(Color.BLUE); 
    
            // Table Column Names
            String[] columnNames = {"Item", "Price ($)", "Payment Method"};
    
            // Create Table Model
            tableModel = new DefaultTableModel(columnNames, 0);
            recentOrdersTable = new JTable(tableModel);
    
            // Table Styling
            recentOrdersTable.setFont(new Font("Arial", Font.PLAIN, 14));
            recentOrdersTable.setRowHeight(30);
            recentOrdersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    
            JScrollPane scrollPane = new JScrollPane(recentOrdersTable);
    
            // Add Components
            add(headerLabel, BorderLayout.NORTH); 
            add(scrollPane, BorderLayout.CENTER);
        }
    
        public void updateRecentOrders(ArrayList<String> items, ArrayList<Double> prices, ArrayList<String> paymentMethods) {
            tableModel.setRowCount(0); // Clear previous data
    
            for (int i = 0; i < items.size(); i++) {
                tableModel.addRow(new Object[]{items.get(i), String.format("$%.2f", prices.get(i)), paymentMethods.get(i)});
            }
        }
    }
}