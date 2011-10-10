import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 */
public class TestMenuMouseListenerBug {
    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

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

                JMenu fileMenu = new JMenu("File");
                createItem(fileMenu, "Open...");
                createItem(fileMenu, "Save");
                createItem(fileMenu, "Save as...");
                fileMenu.addSeparator();
                createItem(fileMenu, "Import...");
                createItem(fileMenu, "Export...");

                JMenu editMenu = new JMenu("Edit");
                createItem(editMenu, "Undo");
                createItem(editMenu, "Redo");
                editMenu.addSeparator();
                createItem(editMenu, "Cut");
                createItem(editMenu, "Copy");
                createItem(editMenu, "Paste");

                JMenu windowMenu = new JMenu("Window");
                createItem(windowMenu, "Dummy 1");
                createItem(windowMenu, "Dummy 2");
                createItem(windowMenu, "Dummy 3");

                JMenu helpMenu = new JMenu("Help");
                createItem(helpMenu, "Help...");
                createItem(helpMenu, "Help!");
                createItem(helpMenu, "HELP!?!");

                JMenuBar menuBar = new JMenuBar();
                menuBar.add(fileMenu);
                menuBar.add(editMenu);
                menuBar.add(windowMenu);
                menuBar.add(helpMenu);

                frame.setJMenuBar(menuBar);

                frame.getContentPane().add(label);

                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    private static void createItem(JMenu menu, final String text) {
        JMenuItem item = new JMenuItem(text);
        item.addMouseListener(new DebugAdapter());
        menu.add(item);
    }

    private static class DebugAdapter extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            System.err.println("Over " + ((JMenuItem) e.getComponent()).getText());
        }

        @Override
        public void mouseExited(MouseEvent e) {
            System.err.println("Exited " + ((JMenuItem) e.getComponent()).getText());
        }
    }
}
