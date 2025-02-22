import javax.swing.*;
import java.awt.Font;

class LoginScreen {
    static final int windowWidth = 1200;
    static final int windowHeight = 800;

    static final int textFieldWidth = 220;
    static final int testFieldHeight = 50;

    static final int loginButtonHeight = windowHeight / 2;

    static final Font textFieldFont = new Font("Arial", Font.PLAIN, 24);
    static final Font loginButtonFont = new Font("Arial", Font.BOLD, 32);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTextField usernameField = new JTextField(50);
        usernameField.setFont(textFieldFont);
        usernameField.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonHeight - 2 * testFieldHeight, 220, 50);

        JPasswordField passwordField = new JPasswordField(50);
        passwordField.setFont(textFieldFont);
        passwordField.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonHeight - testFieldHeight, 220, 50);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(loginButtonFont);
        loginButton.setBounds(windowWidth / 2 - textFieldWidth / 2, loginButtonHeight, 220, 50);

        frame.add(usernameField);
        frame.add(passwordField);
        frame.add(loginButton);

        frame.setSize(windowWidth, windowHeight);

        frame.setLayout(null);
        frame.setVisible(true);
    }
}
