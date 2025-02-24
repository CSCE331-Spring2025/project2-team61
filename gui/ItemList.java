import java.sql.*;
import java.awt.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.function.Consumer;

class ProductEntry {
    public int id;
    public String name;
    public double price;

    public ProductEntry(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}

public class ItemList extends JFrame {
    // private DefaultListModel<String> listModel;
    // private JList<String> itemList;

    private JPanel itemList;

    final static Font font = new Font("Arial", Font.BOLD, 48);

    public ItemList(Db db, String productType, String productTypeReadable, Consumer<ProductEntry> itemSelectedCallback) {
        // listModel = new DefaultListModel<>();
        // itemList = new JList<>(listModel);

        setTitle(String.format("Select %s", productTypeReadable));
        setSize(FrameStyle.screenWidth / 3, FrameStyle.screenHeight);
        
        ResultSet rs = db.query("SELECT id, name, price FROM product WHERE product_type = '%s';", productType);
        
        HashMap<Integer, ProductEntry> items = new HashMap<>();
        try {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getInt("price") / 100.0;
                ProductEntry entry = new ProductEntry(id, name, price);
                // listModel.addElement(name);
                items.put(id, entry);
            }
        } catch (SQLException se) {
            System.err.println(se);
            System.exit(1);
        }

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
