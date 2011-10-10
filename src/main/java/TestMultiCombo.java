import javax.swing.*;

/**
 * TestMultiCombo
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestMultiCombo.java,v 1.0 Aug 18, 2010 4:19:29 PM haraldk Exp$
 */
public class TestMultiCombo {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Test");

                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

                JComboBox comboOne = new JComboBox(new String[] {"one", "two", "three"});
                JComboBox comboTwo = new JComboBox(comboOne.getModel());

                panel.add(comboOne);
                panel.add(comboTwo);

                frame.getContentPane().add(panel);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
