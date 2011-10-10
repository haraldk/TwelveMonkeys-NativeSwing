import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * TestShortcutTypedQuestionmark
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestShortcutTypedQuestionmark.java,v 1.0 Feb 1, 2010 6:34:35 PM haraldk Exp$
 */
public class TestShortcutTypedQuestionmark {
    static Robot sRobot;

    public static void main(final String[] pArgs) throws AWTException {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        sRobot = new Robot();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JMenuBar bar = new JMenuBar();
                JMenu menu = new JMenu("Help");
                bar.add(menu);

                menu.add(new JMenuItem(new QuickAction("Foo Help", KeyStroke.getKeyStroke("meta typed ?"))));
                menu.add(new JMenuItem(new QuickAction("Foo Help II", KeyStroke.getKeyStroke("meta shift typed +"))));
                menu.add(new JMenuItem(new QuickAction("Alt ?", KeyStroke.getKeyStroke("alt typed ?"))));
                menu.add(new JMenuItem(new QuickAction("Ctrl ?", KeyStroke.getKeyStroke("ctrl typed ?"))));
                menu.add(new JMenuItem(new QuickAction("shift ?", KeyStroke.getKeyStroke("shift typed ?"))));
                menu.add(new JMenuItem(new QuickAction("(none) ?", KeyStroke.getKeyStroke("typed ?"))));
                
                JFrame frame = new JFrame("Foo");
                Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                    public void eventDispatched(AWTEvent event) {
                        KeyEvent keyEvent = (KeyEvent) event;

                        if (keyEvent.getID() == KeyEvent.KEY_TYPED) {
                            System.out.println("event: " + event);
                            if ((keyEvent.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) != 0) {
                                System.out.print("alt ");
                            }
                            if ((keyEvent.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) != 0) {
                                System.out.print("ctrl ");
                            }
                            if ((keyEvent.getModifiersEx() & KeyEvent.META_DOWN_MASK) != 0) {
                                System.out.print("meta ");
                            }
                            if ((keyEvent.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0) {
                                System.out.print("shift ");
                            }
                            System.out.println();

                            if (event.getClass() == KeyEvent.class) { // Avoid loop
                                KeyEvent faked = new KeyEvent(
                                        (Component) keyEvent.getSource(),
                                        keyEvent.getID(),
                                        keyEvent.getWhen(),
                                        keyEvent.getModifiersEx() & ~KeyEvent.SHIFT_DOWN_MASK,
                                        keyEvent.getKeyCode(),
                                        // This mapping would be
                                        (keyEvent.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) != 0 ? '?' : keyEvent.getKeyChar(),
                                        keyEvent.getKeyLocation()
                                ) {};
//                                sRobot.keyPress();
                                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(faked);
                            }
                        }
                    }
                }, AWTEvent.KEY_EVENT_MASK);

                frame.setJMenuBar(bar);

                frame.add(new JLabel("Try cmd + ? (remember Norwegian input)"));

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private static class QuickAction extends AbstractAction {
        public QuickAction(final String pName, final KeyStroke pKeyStroke) {
            super(pName);
            putValue(Action.ACCELERATOR_KEY, pKeyStroke);
        }

        public void actionPerformed(ActionEvent e) {
            System.out.println(getValue(Action.NAME) + ".actionPerformed: " + e);
        }
    }
}
