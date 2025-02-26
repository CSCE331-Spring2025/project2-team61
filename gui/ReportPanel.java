import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

class ReportPanel extends JPanel {
    // private ArrayList<String> itemNames;
    // private ArrayList<Integer> inventoryCounts;
    private TableOne tableOne; // Reference to the low supply table
    private TableTwo tableTwo; // Reference to the recent order table
    Db db;


    public ReportPanel() {
        db = new Db();
        // this.itemNames = new ArrayList<>();
        // this.inventoryCounts = new ArrayList<>();
        setLayout(new BorderLayout());

        // Center Panel with a Grid Layout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // North Panel (Graphs)
        JPanel northPanel = new JPanel(new GridLayout(1, 3));
        northPanel.add(new GraphOne(db));
        northPanel.add(new GraphTwo(db));
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
        // this.itemNames = itemNames;
        // this.inventoryCounts = inventoryCounts;
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
        private ArrayList<String> itemNames;
        private ArrayList<Integer> inventoryCounts;

        public GraphOne(Db db) {
            this.itemNames = new ArrayList<>();
            this.inventoryCounts = new ArrayList<>();

            ResultSet rs = db.query("SELECT name, inventory FROM product ORDER BY inventory DESC;");
            try {
                while (rs.next()) {
                    itemNames.add(rs.getString("name"));
                    inventoryCounts.add(rs.getInt("inventory"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            int padding = 25;
            int labelPadding = 25;
            int barWidth = (width - 2 * padding - labelPadding) / itemNames.size();
            int maxInventory = inventoryCounts.stream().max(Integer::compare).orElse(1);

            // Draw y-axis
            g2d.drawLine(padding + labelPadding, height - padding, padding + labelPadding, padding);
            // Draw x-axis
            g2d.drawLine(padding + labelPadding, height - padding, width - padding, height - padding);

            // Draw y-axis title
            String yAxisTitle = "Quantity";
            g2d.drawString(yAxisTitle, padding, padding - 10);

            // Draw bars
            for (int i = 0; i < itemNames.size(); i++) {
                int barHeight = (int) ((double) inventoryCounts.get(i) / maxInventory * (height - 2 * padding));
                g2d.setColor(Color.decode("#AE82D9"));
                g2d.fillRect(padding + labelPadding + i * barWidth, height - padding - barHeight, barWidth - 5, barHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(padding + labelPadding + i * barWidth, height - padding - barHeight, barWidth - 5, barHeight);

                // Draw item names
                g2d.drawString(itemNames.get(i), padding + labelPadding + i * barWidth + barWidth / 2 - g2d.getFontMetrics().stringWidth(itemNames.get(i)) / 2, height - padding + g2d.getFontMetrics().getHeight());
            }
            // Draw y-axis labels
            int numberYDivisions = 10;
            for (int i = 0; i < numberYDivisions + 1; i++) {
                int x0 = padding + labelPadding;
                int x1 = barWidth * itemNames.size() + padding + labelPadding;
                int y0 = height - ((i * (height - padding * 2)) / numberYDivisions + padding);
                int y1 = y0;
                if (itemNames.size() > 0) {
                    g2d.setColor(Color.BLACK);
                    String yLabel = ((int) ((maxInventory * ((i * 1.0) / numberYDivisions))) + "");
                    FontMetrics metrics = g2d.getFontMetrics();
                    int labelWidth = metrics.stringWidth(yLabel);
                    g2d.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
                }
                g2d.drawLine(x0, y0, x1, y1);
            }
        }
    }

    class GraphTwo extends JPanel {
        private ArrayList<String> topSellers;
        private ArrayList<Double> sales;

        public GraphTwo(Db db) {
            this.topSellers = new ArrayList<>();
            this.sales = new ArrayList<>();

            // SELECT product.name AS top_seller, SUM(transaction_item.subtotal) AS sales
            // FROM product
            // JOIN transaction_item ON transaction_item.product_id = product.id
            // GROUP BY product.name
            // ORDER BY sales DESC
            // LIMIT 10;
            ResultSet rs = db.query("SELECT product.name AS top_seller, SUM(transaction_item.subtotal) AS sales FROM product JOIN transaction_item ON transaction_item.product_id = product.id GROUP BY product.name ORDER BY sales DESC LIMIT 10;");
            try {
                while (rs.next()) {
                    topSellers.add(rs.getString("top_seller"));
                    sales.add(rs.getDouble("sales"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            int padding = 25;
            int labelPadding = 25;
            int barWidth = (width - 2 * padding - labelPadding) / topSellers.size();
            double maxSales = sales.stream().max(Double::compare).orElse(1.0);

            // Draw y-axis
            g2d.drawLine(padding + labelPadding, height - padding, padding + labelPadding, padding);
            // Draw x-axis
            g2d.drawLine(padding + labelPadding, height - padding, width - padding, height - padding);

            // Draw y-axis title
            String yAxisTitle = "Sales";
            g2d.drawString(yAxisTitle, padding, padding - 10);

            // Draw bars
            for (int i = 0; i < topSellers.size(); i++) {
                int barHeight = (int) ((sales.get(i) / maxSales) * (height - 2 * padding));
                g2d.setColor(Color.decode("#82D9AE"));
                g2d.fillRect(padding + labelPadding + i * barWidth, height - padding - barHeight, barWidth - 5, barHeight);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(padding + labelPadding + i * barWidth, height - padding - barHeight, barWidth - 5, barHeight);

                // Draw top seller names
                g2d.drawString(topSellers.get(i), padding + labelPadding + i * barWidth + barWidth / 2 - g2d.getFontMetrics().stringWidth(topSellers.get(i)) / 2, height - padding + g2d.getFontMetrics().getHeight());
            }

            // Draw y-axis labels
            int numberYDivisions = 10;
            for (int i = 0; i < numberYDivisions + 1; i++) {
                int x0 = padding + labelPadding;
                int x1 = barWidth * topSellers.size() + padding + labelPadding;
                int y0 = height - ((i * (height - padding * 2)) / numberYDivisions + padding);
                int y1 = y0;
                if (topSellers.size() > 0) {
                    g2d.setColor(Color.BLACK);
                    String yLabel = String.format("%.2f", (maxSales * ((i * 1.0) / numberYDivisions)));
                    FontMetrics metrics = g2d.getFontMetrics();
                    int labelWidth = metrics.stringWidth(yLabel);
                    g2d.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
                }
                g2d.drawLine(x0, y0, x1, y1);
            }
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