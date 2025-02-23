import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ManagerPage extends JFrame {
    Db database;

    public ManagerPage() {
        super("Manager Page");
        FrameStyle.StyleFrame(this);
        initializeComponents();
    }

    public void initializeComponents() {
        database = new Db();
        
        JPanel mainPanel = new JPanel(new BorderLayout());

        //////////////////// LEFT PANEL (Inventory Table) ////////////////////
        JPanel inventoryPanel = new JPanel(new BorderLayout());
        inventoryPanel.setPreferredSize(new Dimension(FrameStyle.screenWidth / 2, FrameStyle.screenHeight));
        inventoryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel inventoryLabel = new JLabel("Inventory", JLabel.CENTER);
        inventoryLabel.setFont(new Font("Arial", Font.BOLD, 40));
        
        JTable inventoryTable = createTableFromDB(
            "SELECT product.name, product.inventory AS inventory_count FROM product ORDER BY inventory_count DESC;"
        );
        JScrollPane inventoryScroll = new JScrollPane(inventoryTable);

        inventoryPanel.add(inventoryLabel, BorderLayout.NORTH);
        inventoryPanel.add(inventoryScroll, BorderLayout.CENTER);


        //////////////////// RIGHT PANEL (Price Table) ////////////////////
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setPreferredSize(new Dimension(FrameStyle.screenWidth / 2, FrameStyle.screenHeight));
        pricePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel priceLabel = new JLabel("Price", JLabel.CENTER);
        priceLabel.setFont(new Font("Arial", Font.BOLD, 40));

        JTable priceTable = createPriceTable();
        JScrollPane priceScroll = new JScrollPane(priceTable);

        pricePanel.add(priceLabel, BorderLayout.NORTH);
        pricePanel.add(priceScroll, BorderLayout.CENTER);

        //////////////////// Add Panels to Main Panel ////////////////////
        mainPanel.add(inventoryPanel, BorderLayout.WEST);
        mainPanel.add(pricePanel, BorderLayout.EAST);

        add(mainPanel);
    }

    // Method to create a JTable for inventory and price tables
    private JTable createTableFromDB(String query) {
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        return table;
    }

    // Method to create the Price Table with price converted to dollars
    private JTable createPriceTable() {
        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        return table;
    }

    // Main method to run the ManagerPage
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManagerPage managerPage = new ManagerPage();
            managerPage.setVisible(true);
        });
    }
}
