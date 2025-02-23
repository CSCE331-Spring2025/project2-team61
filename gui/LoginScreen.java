import java.sql.*;
import javax.swing.*;
import java.awt.Font;
import java.awt.Color;

public class LoginScreen extends JFrame {
    static final int textFieldWidth = 220;
    static final int textFieldHeight = 50;

    static final Font textFieldFont = new Font("Arial", Font.PLAIN, 24);
    static final Font loginButtonFont = new Font("Arial", Font.BOLD, 32);
    static final Font errorLabelFont = new Font("Arial", Font.PLAIN, 18);

    public LoginScreen() {
        super("Login");
        FrameStyle.StyleFrame(this);
        initializeComponents();
    }

    private void initializeComponents() {
        setLayout(null);
        int windowWidth = FrameStyle.screenWidth;
        int windowHeight = FrameStyle.screenHeight;
        int loginButtonYPosition = windowHeight / 2;

        Db db = new Db();

        JTextField usernameField = new JTextField(50);
        usernameField.setFont(textFieldFont);
        usernameField.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition - 2 * textFieldHeight, textFieldWidth, textFieldHeight);

        JPasswordField passwordField = new JPasswordField(50);
        passwordField.setFont(textFieldFont);
        passwordField.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition - textFieldHeight, textFieldWidth, textFieldHeight);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(loginButtonFont);
        loginButton.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition, textFieldWidth, textFieldHeight);

        JLabel errorLabel = new JLabel("");
        errorLabel.setFont(errorLabelFont);
        errorLabel.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition + textFieldHeight + 10, textFieldWidth + 80, textFieldHeight);
        errorLabel.setForeground(Color.RED);

        loginButton.addActionListener(e -> {
            String name = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String passwordHash = PasswordHash.hash(password);

            System.out.printf("Login Clicked!, Name: %s, Password: %s, Hash: %s\n", name, password, passwordHash);

            ResultSet rs = db.query("SELECT COUNT(*) FROM employee WHERE name='%s' AND password='%s';", name, passwordHash);

            try {
                if (rs.next()) {
                    int count = rs.getInt(1);

                    if (count == 1) {
                        System.out.println("Successfully Logged In!");
                        this.dispose();
                        JobSelectionPage nextFrame = new JobSelectionPage(db);
                        nextFrame.setVisible(true);
                    } else {
                        System.out.println("Incorrect Username or Password");
                        errorLabel.setText("Incorrect Username or Password");
                    }
                }
            } catch (SQLException se) {
                System.out.println(se);
            }
        });

        this.add(usernameField);
        this.add(passwordField);
        this.add(loginButton);
        this.add(errorLabel);

        setSize(windowWidth, windowHeight);
    }
}
