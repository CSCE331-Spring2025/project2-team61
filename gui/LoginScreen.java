import javax.swing.*;
import java.awt.Font;

public class LoginScreen extends JFrame {
    static final int textFieldWidth = 220;
    static final int textFieldHeight = 50;

    static final Font textFieldFont = new Font("Arial", Font.PLAIN, 24);
    static final Font loginButtonFont = new Font("Arial", Font.BOLD, 32);

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

        JTextField usernameField = new JTextField(50);
        usernameField.setFont(textFieldFont);
        usernameField.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition - 2 * textFieldHeight, textFieldWidth, textFieldHeight);

        JPasswordField passwordField = new JPasswordField(50);
        passwordField.setFont(textFieldFont);
        passwordField.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition - textFieldHeight, textFieldWidth, textFieldHeight);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(loginButtonFont);
        loginButton.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonYPosition, textFieldWidth, textFieldHeight);

        add(usernameField);
        add(passwordField);
        add(loginButton);

        setSize(windowWidth, windowHeight);
    }
}