import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;

public class CashierPage extends JFrame {
    Db db;
    int employeeId;

    // productId -> quantity
    HashMap<Integer, Integer> quantities;

    private double currentTotal;

    ArrayList<JFrame> childFrames;

    public CashierPage(Db db, int employeeId) {
        super("Cashier Page");
        this.db = db;
        this.employeeId = employeeId;
        this.quantities = new HashMap<>();
        this.currentTotal = 0.0;
        this.childFrames = new ArrayList<>();
        FrameStyle.StyleFrame(this);
        initializeComponents();
    }

    public void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        //////////////////// LEFT PANEL ////////////////////
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(FrameStyle.screenWidth / 3, FrameStyle.screenHeight));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Need this panel for vertical stacking
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // PAY BUTTON
        JButton payButton = new JButton("Pay");
        payButton.setBackground(Color.decode("#D99E82"));
        payButton.setFont(new Font("Arial", Font.BOLD, 50));
        payButton.setForeground(Color.WHITE);
        payButton.setPreferredSize(new Dimension(FrameStyle.screenWidth / 3, 100)); // idk why it wont be a wider pay
                                                                                    // button here
        leftPanel.add(payButton, BorderLayout.SOUTH);

        // ITEMS PANEL
        JPanel itemsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // TOTAL PRICE PANEL
        JPanel totalPricePanel = new JPanel(new BorderLayout());
        JLabel totalPriceLabel = new JLabel("Total Price: $0.00"); // Start at $0.00, need to update dynamically
        totalPriceLabel.setFont(new Font("Arial", Font.PLAIN, 50));
        totalPricePanel.add(totalPriceLabel, BorderLayout.CENTER);
        leftPanel.add(totalPricePanel, BorderLayout.SOUTH);

        contentPanel.add(itemsPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(totalPricePanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(payButton);

        leftPanel.add(contentPanel, BorderLayout.SOUTH);

        payButton.addActionListener(e -> {
            ResultSet rs = db.query("INSERT INTO transaction (price, employee_id) VALUES (%d, %d) RETURNING id;",
                    (int) (currentTotal * 100), employeeId);

            int transactionId = 0;
            try {
                if (rs.next()) {
                    transactionId = rs.getInt("id");
                } else {
                    System.err.println("Insert transaction query did not return id");
                    System.exit(1);
                }
            } catch (SQLException se) {
                System.err.println(se);
                System.exit(1);
            }

            for (Integer pId : quantities.keySet()) {
                Integer quantity = quantities.get(pId);

                db.query(
                        "INSERT INTO transaction_item (transaction_id, product_id, quantity, subtotal) SELECT %d, %d, %d, %d * price FROM product WHERE id = %d RETURNING id;",
                        transactionId, pId, quantity, quantity, pId);

            }

            quantities.clear();

            currentTotal = 0.0;
            totalPriceLabel.setText("Total Price: $" + String.format("%.2f", currentTotal));

            itemsPanel.removeAll();
            itemsPanel.revalidate();
            itemsPanel.repaint();
        });

        //////////////////// CENTER PANEL (Menu Buttons) ////////////////////
        String[] productTypes = new String[12];
        String[] productTypesReadable = new String[12];
        ResultSet rs = db.query(
                "SELECT enumlabel FROM pg_enum JOIN pg_type ON pg_type.oid = pg_enum.enumtypid WHERE pg_type.typname = 'product_type';");

        try {
            for (int i = 0; rs.next() && i < productTypes.length; i++) {
                String productType = rs.getString("enumlabel");
                productTypes[i] = productType;
                productTypesReadable[i] = Utils.snakeToReadable(productType);
            }
        } catch (SQLException se) {
            System.err.println(se);
            System.exit(1);
        }

        JPanel menuPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] colors = { "#D7D982", "#81D8D0", "#AE82D9", "#D99E82",
                "#D7D982", "#81D8D0", "#AE82D9", "#D99E82",
                "#D7D982", "#81D8D0", "#AE82D9", "#D99E82" };
        String[] pictures = { "soda.png", "iced-coffee.png", "lemonade.png", "drink.png",
                "coffee.png", "coffee-cup.png", "coffee.png", "coffee-cup.png",
                "cold-coffee.png", "cold-coffee.png", "cuba-libre.png", "drink.png" };
        for (int i = 0; i < 12; i++) {
            JButton button = new JButton();
            button.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // idk why this isnt creating a border
            button.setBackground(Color.decode(colors[i]));
            button.setFont(new Font("Arial", Font.BOLD, 30));
            button.setForeground(Color.WHITE);
            button.setText(productTypesReadable[i]);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setIcon(new ImageIcon("icons/" + pictures[i]));
            button.setOpaque(true);
            button.setBorderPainted(false);

            // Add ActionListener to each button
            final String productType = productTypes[i];
            final String productTypeReadable = productTypesReadable[i];
            button.addActionListener(e -> {
                if (productType == null)
                    return;

                Consumer<ProductEntry> addItemSelected = pe -> {
                    if (!quantities.containsKey(pe.id)) {
                        quantities.put(pe.id, 1);
                    } else {
                        quantities.put(pe.id, quantities.get(pe.id) + 1);
                    }

                    JLabel itemLabel = new JLabel(pe.name + " $" + String.format("%.2f", pe.price)); // Format as
                                                                                                     // decimal
                    itemLabel.setFont(new Font("Arial", Font.PLAIN, 50));

                    itemsPanel.add(itemLabel, 0); // Insert at the top

                    currentTotal += pe.price;
                    totalPriceLabel.setText("Total Price: $" + String.format("%.2f", currentTotal));

                    itemsPanel.revalidate();
                    itemsPanel.repaint();
                };

                ItemList itemSelectionFrame = new ItemList(db, productType, productTypeReadable, addItemSelected);
                itemSelectionFrame.setAlwaysOnTop(true);

                itemSelectionFrame.setVisible(true);
                childFrames.add(itemSelectionFrame);
            });
            menuPanel.add(button);
        }

        //////////////////// RIGHT PANEL ////////////////////
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(FrameStyle.screenWidth / 3, FrameStyle.screenHeight));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // SEARCH BAR
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField("");
        searchField.setFont(new Font("Arial", Font.PLAIN, 50));
        JButton searchButton = new JButton("+");
        searchButton.setBackground(Color.decode("#81D8D0"));
        searchButton.setFont(new Font("Arial", Font.BOLD, 50));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // NAVIGATION BAR
        String[] navOptions = { "Keyboard", "Library", "Menu", "Logout [>]" };
        JList<String> navList = new JList<>(navOptions);
        navList.setBackground(Color.decode("#E0F6F1"));
        navList.setForeground(Color.BLACK);
        navList.setFont(new Font("Arial", Font.PLAIN, 50));
        JScrollPane navScroll = new JScrollPane(navList);
        rightPanel.add(navScroll, BorderLayout.CENTER);

        // Add ListSelectionListener for navigation
        navList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = navList.getSelectedValue();
                if ("Logout [>]".equals(selected)) {
                    handleLogout();
                } else if ("Keyboard".equals(selected)) {
                    JFrame keyboardFrame = new JFrame("Keyboard");
                    keyboardFrame.setSize(750, 750);
                    keyboardFrame.setLayout(new GridLayout(4, 7));
                    for (char c = 'A'; c <= 'Z'; c++) {
                        JButton button = new JButton(String.valueOf(c));
                        button.setFont(new Font("Arial", Font.BOLD, 30));
                        keyboardFrame.add(button);
                    }
                    keyboardFrame.setVisible(true);
                    childFrames.add(keyboardFrame);
                } else if ("Library".equals(selected)) {
                    JFrame libraryFrame = new JFrame("Library");
                    libraryFrame.setSize(750, 750);
                    libraryFrame.setLayout(new GridLayout(4, 3));
                    for (String category : productTypesReadable) {
                        JButton button = new JButton(category);
                        button.setFont(new Font("Arial", Font.BOLD, 30));
                        libraryFrame.add(button);
                    }
                    libraryFrame.setVisible(true);
                    childFrames.add(libraryFrame);
                } else if ("Menu".equals(selected)) {
                    JFrame menuFrame = new JFrame("Menu");
                    menuFrame.setSize(750, 750);
                    menuFrame.setLayout(new GridLayout(5, 4));
                    for (int i = 0; i < 20; i++) {
                        JButton button = new JButton("Item " + (i + 1));
                        button.setFont(new Font("Arial", Font.BOLD, 30));
                        menuFrame.add(button);
                    }
                    menuFrame.setVisible(true);
                    childFrames.add(menuFrame);
                }
            }
        });

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    // Logout handler
    private void handleLogout() {
        dispose();
        for (JFrame c : childFrames) {
            c.dispose();
        }
        JFrame jobSelectionFrame = new JobSelectionPage(db, employeeId);
        jobSelectionFrame.setVisible(true);
    }

    public static void main(String[] args) {
        Db db = new Db();
        CashierPage cashierPage = new CashierPage(db, 1);
        cashierPage.setVisible(true);
    }
}
