import com.apple.eawt.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 */
public class TestDockMenuBug {
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();

                frame.setPreferredSize(new Dimension(300, 200));
                frame.add(new JLabel("Testing, testing, 1-2-3", JLabel.CENTER));

                // Test: Adding items to the dock menu, crashes
                Application app = Application.getApplication();
//                System.err.println(app.getDockMenu()); // <-- prints null

                PopupMenu popupMenu = new PopupMenu();

                // HACK: Workaround as suggested by Bino George: https://devforums.apple.com/message/290064#290064
                // Attach the popup to a MenuBar and call addNotify to give it a fake parent...
//                MenuBar mb = new MenuBar();
//                mb.add(popupMenu);
//                mb.addNotify();
                // /end hack

                System.err.println("popupMenu: " + popupMenu);

                app.setDockMenu(popupMenu);      // <-- Exception in thread "AWT-EventQueue-0" java.lang.IllegalArgumentException: illegal popup menu container class

                MenuItem foo = new MenuItem("Foo");
                foo.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        System.err.println("Foo");
                    }
                });
                MenuItem bar = new MenuItem("Bar");
                bar.addActionListener(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        System.err.println("Bar");
                    }
                });

                Menu menu = new Menu("Foo Menu");
                menu.add(foo);
                menu.add(bar);

                app.getDockMenu().add(menu);          // ...not reached

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}
