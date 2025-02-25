import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;

class ReportPanel extends JPanel {
    private ArrayList<String> itemNames;
    private ArrayList<Integer> inventoryCounts;

    public ReportPanel() {
        this.itemNames = new ArrayList<>();
        this.inventoryCounts = new ArrayList<>();
        setLayout(new BorderLayout());

        // Center Panel with a Grid Layout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // Allow components to expand both horizontally and vertically

        // North Panel (Graphs)
        JPanel northPanel = new JPanel(new GridLayout(1, 3));
        northPanel.add(new GraphOne());
        northPanel.add(new GraphTwo());
        northPanel.add(new GraphThree());

        // South Panel (Tables)
        JPanel southPanel = new JPanel(new GridLayout(1, 2));
        southPanel.add(new TableOne());
        southPanel.add(new TableTwo());

        // Add northPanel to centerPanel with weight
        gbc.gridx = 0;
        gbc.gridy = 0; // these two mean the top left corner (or in this case, top row)
        
        gbc.weightx = 1.0; // Full width
        gbc.weighty = 0.6; // 60% of height (adjust as needed)
        centerPanel.add(northPanel, gbc);

        // Add southPanel to centerPanel with weight
        gbc.gridy = 1;
        gbc.weighty = 0.4; // 40% of height (adjust as needed)
        centerPanel.add(southPanel, gbc);

        // Add center panel to the main panel
        add(centerPanel, BorderLayout.CENTER);
    }

    public void updateData(ArrayList<String> itemNames, ArrayList<Integer> inventoryCounts) {
        this.itemNames = itemNames;
        this.inventoryCounts = inventoryCounts;
        repaint();
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
        public TableOne() {
            setBackground(Color.YELLOW);
        }
    }

    class TableTwo extends JPanel {
        public TableTwo() {
            setBackground(Color.ORANGE);
        }
    }
}