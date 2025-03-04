import java.sql.*;
import javax.swing.*;
import java.awt.Font;
import java.awt.Color;

/**
 * The {@code LoginScreen} class represents a JFrame window for user authentication.
 * It allows employees to log in using their username and password.
 * 
 * This frame:
 * - Provides input fields for username and password.
 * - Validates credentials against the database.
 * - Navigates to the job selection page upon successful login.
 * - Displays an error message if login fails.
 * - Includes an exit button to close the application.
 * 
 * @author Luke Conran
 * @author Kamryn Vogel
 * @author Christian Fadal
 * @author Macsen Casaus
 * @author Surada Suwansathit
 */
public class LoginScreen extends JFrame {
    static final int textFieldWidth = 220;
    static final int textFieldHeight = 50;

    static final Font textFieldFont = new Font("Arial", Font.PLAIN, 24);
    static final Font loginButtonFont = new Font("Arial", Font.BOLD, 32);
    static final Font errorLabelFont = new Font("Arial", Font.PLAIN, 18);

    /**
     * Constructs the {@code LoginScreen} window.
     * Initializes the UI components and applies the standard frame style.
     */
    public LoginScreen() {
        super("Login");
        FrameStyle.StyleFrame(this);
        initializeComponents();
    }

    /**
     * Initializes the user interface components for the login screen.
     */
    private void initializeComponents() {
        setLayout(null);
        int windowWidth = FrameStyle.screenWidth;
        int windowHeight = FrameStyle.screenHeight;
        int loginButtonYPosition = windowHeight / 2;

        Db db = new Db();

        // Username input field
        JTextField usernameField = new JTextField(50);
        usernameField.setFont(textFieldFont);
        usernameField.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition - 2 * textFieldHeight,
                textFieldWidth, textFieldHeight);

        // Password input field
        JPasswordField passwordField = new JPasswordField(50);
        passwordField.setFont(textFieldFont);
        passwordField.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition - textFieldHeight,
                textFieldWidth, textFieldHeight);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setFont(loginButtonFont);
        loginButton.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition, textFieldWidth,
                textFieldHeight);

        // Exit button
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(loginButtonFont);
        exitButton.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition * 5 / 4, textFieldWidth,
                textFieldHeight);

        // Error label for incorrect login attempts
        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(errorLabelFont);
        errorLabel.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition + textFieldHeight + 10,
                textFieldWidth + 80, textFieldHeight);
        errorLabel.setForeground(Color.RED);

        /**
         * Action listener for handling login authentication.
         * It validates the username and password against the database.
         */
        loginButton.addActionListener(e -> {
            String name = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String passwordHash = PasswordHash.hash(password);

            // Query database to check credentials
            ResultSet rs = db.query("SELECT id FROM employee WHERE name='%s' AND password='%s';", name, passwordHash);

            try {
                if (rs.next()) {
                    int employeeId = rs.getInt(1);
                    this.dispose();
                    JobSelectionPage nextFrame = new JobSelectionPage(db, employeeId);
                    nextFrame.setVisible(true);
                } else {
                    errorLabel.setText("Incorrect Username or Password");
                }
            } catch (SQLException se) {
                System.out.println(se);
            }
        });

        /**
         * Action listener for exiting the application.
         * Disposes the login frame when the exit button is clicked.
         */
        exitButton.addActionListener(e -> {
            this.dispose();
        });

        this.add(usernameField);
        this.add(passwordField);
        this.add(loginButton);
        this.add(exitButton);
        this.add(errorLabel);

        setSize(windowWidth, windowHeight);
    }
}

