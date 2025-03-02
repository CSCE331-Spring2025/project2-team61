import javax.swing.*;
import java.awt.Font;

public class JobSelectionPage extends JFrame {
    static final int textFieldWidth = 200;
    static final int textFieldHeight = 50;
    static final Font selectionButtonFont = new Font("Arial", Font.BOLD, 32);
    Db db;
    int employeeId;

    public JobSelectionPage(Db db, int employeeId) {
        super("Job Selection");
        this.db = db;
        this.employeeId = employeeId;
        FrameStyle.StyleFrame(this);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(null);
        int windowWidth = FrameStyle.screenWidth;
        int windowHeight = FrameStyle.screenHeight;
        int loginButtonYPosition = windowHeight / 2;

        JButton job1Button = new JButton("Cashier");
        job1Button.setFont(selectionButtonFont);
        job1Button.setBounds(windowWidth / 2 - textFieldWidth - 100, loginButtonYPosition, textFieldWidth,
                textFieldHeight);

        JButton job2Button = new JButton("Manager");
        job2Button.setFont(selectionButtonFont);
        job2Button.setBounds(windowWidth / 2, loginButtonYPosition, textFieldWidth, textFieldHeight);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(selectionButtonFont);
        logoutButton.setBounds(windowWidth / 2 - textFieldWidth + 50, loginButtonYPosition * 5 / 4, textFieldWidth,
                textFieldHeight);

        job1Button.addActionListener(e -> {
            this.dispose();
            new CashierPage(db, employeeId).setVisible(true);
        });

        job2Button.addActionListener(e -> {
            this.dispose();
            new ManagerPage(employeeId).setVisible(true);
        });

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

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Db db = new Db();
            JobSelectionPage jobSelectionPage = new JobSelectionPage(db, 1);
            jobSelectionPage.setVisible(true);
        });
    }
}
