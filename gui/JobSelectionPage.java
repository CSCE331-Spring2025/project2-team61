import javax.swing.*;
import java.awt.Font;

/**
 * The {@code JobSelectionPage} class represents a JFrame window that allows employees
 * to select their role upon logging in. It provides options to navigate to either
 * the Cashier page or the Manager page, along with a logout button.
 * 
 * This frame:
 * - Displays "Cashier" and "Manager" job selection buttons.
 * - Navigates to the appropriate interface when a selection is made.
 * - Provides a "Logout" button to return to the login screen.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class JobSelectionPage extends JFrame {
    static final int textFieldWidth = 200;
    static final int textFieldHeight = 50;
    static final Font selectionButtonFont = new Font("Arial", Font.BOLD, 32);
    Db db;
    int employeeId;

    /**
     * Constructs the {@code JobSelectionPage} window where employees choose their role.
     * 
     * @param db         The database connection used for authentication.
     * @param employeeId The ID of the logged-in employee.
     */
    public JobSelectionPage(Db db, int employeeId) {
        super("Job Selection");
        this.db = db;
        this.employeeId = employeeId;
        FrameStyle.StyleFrame(this);
        initializeComponents();
    }

    /**
     * Initializes the user interface components, including job selection buttons
     * and a logout button.
     */
    private void initializeComponents() {
        setLayout(null);
        int windowWidth = FrameStyle.screenWidth;
        int windowHeight = FrameStyle.screenHeight;
        int loginButtonYPosition = windowHeight / 2;

        // Cashier Button
        JButton job1Button = new JButton("Cashier");
        job1Button.setFont(selectionButtonFont);
        job1Button.setBounds(windowWidth / 2 - textFieldWidth - 100, loginButtonYPosition, textFieldWidth,
                textFieldHeight);

        // Manager Button
        JButton job2Button = new JButton("Manager");
        job2Button.setFont(selectionButtonFont);
        job2Button.setBounds(windowWidth / 2, loginButtonYPosition, textFieldWidth, textFieldHeight);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(selectionButtonFont);
        logoutButton.setBounds(windowWidth / 2 - textFieldWidth + 50, loginButtonYPosition * 5 / 4, textFieldWidth,
                textFieldHeight);

        /**
         * Action listener for navigating to the Cashier page when the button is clicked.
         */
        job1Button.addActionListener(e -> {
            this.dispose();
            new CashierPage(db, employeeId).setVisible(true);
        });

        /**
         * Action listener for navigating to the Manager page when the button is clicked.
         */
        job2Button.addActionListener(e -> {
            this.dispose();
            new ManagerPage(employeeId).setVisible(true);
        });

        /**
         * Action listener for logging out and returning to the Login screen.
         */
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginScreen().setVisible(true);
        });

        add(job1Button);
        add(job2Button);
        add(logoutButton);

        setSize(windowWidth, windowHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centers the window on the screen
    }

    /**
     * Main method for testing the {@code JobSelectionPage}.
     * 
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Db db = new Db();
            JobSelectionPage jobSelectionPage = new JobSelectionPage(db, 1);
            jobSelectionPage.setVisible(true);
        });
    }
}

