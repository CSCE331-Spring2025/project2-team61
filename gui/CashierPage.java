import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
// import java.util.HashMap;
// import java.util.Map;

public class CashierPage extends JFrame {
    Db database;

    public CashierPage() {
        super("Cashier Page");
        FrameStyle.StyleFrame(this);
        initializeComponents();
    }

    public void initializeComponents() {
        database = new Db();

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
        payButton.setPreferredSize(new Dimension(FrameStyle.screenWidth / 3, 100)); // idk why it wont be a wider pay button here
        leftPanel.add(payButton, BorderLayout.SOUTH);

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // addTransactionToDatabase(); // TODO, need to make a function to add transaction to database
            }
        });

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

        




        //////////////////// CENTER PANEL (Menu Buttons) ////////////////////

        JPanel menuPanel = new JPanel(new GridLayout(4, 3, 5, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        String[] colors = {"#D7D982", "#81D8D0", "#AE82D9", "#D99E82",
                           "#D7D982", "#81D8D0", "#AE82D9", "#D99E82",
                           "#D7D982", "#81D8D0", "#AE82D9", "#D99E82"};
        String[] labels = {"Classic Milk Tea", "Okinawa Milk Tea", "Mango Fruit Tea", "Strawberry Fruit Tea", 
                        "Earl Gray Tea", "Jasmine Green Tea", "Fresh Taro Milk", "Matcha Fresh Milk", 
                        "Mocha Ice Blended", "Caramel Ice Blended", "Lemon Tea Mojito", "Passionfruit Tea Mojito"}; //This isnt all the menu items
        for (int i = 0; i < 12; i++) {
            JButton button = new JButton();
            button.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // idk why this isnt creating a border
            button.setBackground(Color.decode(colors[i]));
            button.setFont(new Font("Arial", Font.BOLD, 30));
            button.setForeground(Color.WHITE);
            button.setText(labels[i]);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setIcon(new ImageIcon("path/to/coffee_icon.png")); // Replace with actual icon path for a coffee picture - TODO
            button.setOpaque(true);
            button.setBorderPainted(false);

            // Add ActionListener to each button
            final String item = labels[i];
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addItemToOrder(itemsPanel, item, totalPriceLabel);
                }
            });
            menuPanel.add(button);
        }





        //////////////////// RIGHT PANEL ////////////////////
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(FrameStyle.screenWidth / 3, FrameStyle.screenHeight));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // SEARCH BAR
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        JTextField searchField = new JTextField("Search item...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 50));
        JButton searchButton = new JButton("☼");
        searchButton.setBackground(Color.decode("#81D8D0"));
        searchButton.setFont(new Font("Arial", Font.BOLD, 50));
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // NAVIGATION BAR
        String[] navOptions = {"Keyboard", "Library", "Menu", "Logout [>]"};
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
                }
            }
        });

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(menuPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }



    // Method to add an item to the order list and update total
    private void addItemToOrder(JPanel itemsPanel, String item, JLabel totalLabel) {
        ResultSet rs = database.query("SELECT price FROM product WHERE name='%s';", item);
    
        try {
            if (rs.next()) {
                int priceInt = rs.getInt(1); 
                double price = priceInt / 100.0;
                
                JLabel itemLabel = new JLabel(item + " $" + String.format("%.2f", price)); // Format as decimal
                itemLabel.setFont(new Font("Arial", Font.PLAIN, 50));
                
                itemsPanel.add(itemLabel, 0); // Insert at the top
    
                double currentTotal = Double.parseDouble(totalLabel.getText().replace("Total Price: $", ""));
                currentTotal += price;
                totalLabel.setText("Total Price: $" + String.format("%.2f", currentTotal));
    
                itemsPanel.revalidate();
                itemsPanel.repaint();

                // addTransactionItemToDatabase(item); // TODO, need to make a function to add transaction_item to database  

                /* Becuase we have a quantity column in our transaction_item table,
                 * we can't really add a transaction_item to the database until we know the quantity
                 * of that same item in this transaction, which means we are likely going to have to make
                 * an array for every type of menu item, and increment it for every time that it is ordered.
                 * Then, when the pay button is pressed, we can add all of the transaction_items to the database
                 * with their respective quantities. Or, we can change the transaction_item table to not have
                 * the quantity column, and instead have the quantity column in the transaction table.
                 * I don't know which one is better, so I'm going to leave it as is for now.
                 */
            }
        } catch (SQLException se) {
            System.out.println("SQL Exception: " + se.getMessage());
        } catch (NumberFormatException nfe) {
            System.out.println("Number Format Exception: " + nfe.getMessage());
        }
    }

    // Logout handler
    private void handleLogout() {
        dispose();
        JFrame jobSelectionFrame = new JobSelectionPage();
        jobSelectionFrame.setVisible(true);
    }

    // Main method to run the CashierPage for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CashierPage frame = new CashierPage();
            frame.setVisible(true);
        });
    }
}