import java.sql.*;
import java.awt.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * The {@code ItemList} class represents a JFrame window that displays a list of items 
 * belonging to a specific product type. The user can select an item, which triggers a 
 * callback function.
 * 
 * This frame:
 * - Queries the database to fetch available items based on the product type.
 * - Displays the items as buttons, with corresponding price labels.
 * - Calls a callback function when an item is selected.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class ItemList extends JFrame {
    private JPanel itemList;
    final static Font font = new Font("Arial", Font.BOLD, 48);

    /**
     * Constructs an {@code ItemList} window that displays products of a given type.
     * 
     * @param db                   The database connection used to fetch item data.
     * @param productType          The internal database identifier for the product type.
     * @param productTypeReadable  The user-friendly name of the product type.
     * @param itemSelectedCallback The callback function triggered when an item is selected.
     */
    public ItemList(Db db, String productType, String productTypeReadable,
            Consumer<ProductEntry> itemSelectedCallback) {

        setTitle(String.format("Select %s", productTypeReadable));
        setSize(FrameStyle.screenWidth / 3, FrameStyle.screenHeight);

        /**
         * Queries the database to retrieve items of the given product type.
         */
        ResultSet rs = db.query("SELECT id, name, price FROM product WHERE product_type = '%s';", productType);

        HashMap<Integer, ProductEntry> items = new HashMap<>();
        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getInt("price") / 100.0;
                ProductEntry entry = new ProductEntry(id, name, price);
                items.put(id, entry);
            }
        } catch (SQLException se) {
            System.err.println(se);
            System.exit(1);
        }

        /**
         * Creates a panel containing buttons for each product.
         * Clicking a button triggers the callback function and closes the frame.
         */
        itemList = new JPanel(new GridLayout(0, 2));
        itemList.setFont(font);

        for (Integer itemId : items.keySet()) {
            ProductEntry entry = items.get(itemId);

            JButton button = new JButton();
            button.setSize(100, 100);
            button.setText(entry.name);
            button.setFont(font);
            button.setBackground(Color.decode("#FFFFFF"));
            button.setOpaque(true);
            button.addActionListener(e -> {
                itemSelectedCallback.accept(entry);
                dispose();
            });

            JLabel label = new JLabel(String.format("$%.2f", entry.price));
            label.setFont(font);

            itemList.add(button);
            itemList.add(label);
        }

        itemList.setPreferredSize(new Dimension(FrameStyle.screenWidth / 3, 100 * items.size()));

        setLayout(new FlowLayout());
        add(itemList);
        pack();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}

