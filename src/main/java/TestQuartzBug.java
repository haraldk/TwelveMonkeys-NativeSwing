import javax.swing.*;
import java.awt.*;

/**
 * TestQuartzBug
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 */
public class TestQuartzBug {
    public static void main(String[] args) {
        System.setProperty("apple.awt.graphics.UseQuartz", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();

                JLabel label = new JLabel("The quick brown fox jumps the lazy dog", JLabel.CENTER) {
                    @Override
                    public Dimension getPreferredSize() {
                        Dimension size = super.getPreferredSize();
                        size.width = Math.max(300, size.width);
                        size.height = Math.max(200, size.height);

                        return size;
                    }
                };

                frame.getContentPane().add(label);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
