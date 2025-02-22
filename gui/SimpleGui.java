import javax.swing.*;

public class SimpleGui extends JFrame {
    static JFrame f;

    public static void main(String[] args) {
        f = new JFrame("DB GUI");

        f.setSize(400, 400);
        f.setVisible(true);

        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
