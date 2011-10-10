import com.apple.eio.FileManager;
import com.twelvemonkeys.spice.NativeFileChooser;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * TestOpenPanel
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestOpenPanel.java,v 1.0 Mar 21, 2008 8:23:14 PM haraldk Exp$
 */
public class TestNativeFileChooser {
    public static void main(String[] pArgs) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFileChooser chooser = new NativeFileChooser();
                chooser.setName(getClass().getName());

                NSAutoreleasePool pool = NSAutoreleasePool.new_();

                JMenu fileMenu = new JMenu("File");
                fileMenu.add(new JMenuItem(new OpenFilesAction(chooser)));
                fileMenu.add(new JMenuItem(new OpenDirAction(chooser)));
                fileMenu.add(new JMenuItem(new AbstractAction("save") {
                    {
                        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                        putValue(Action.NAME, "Save");
                        setEnabled(false);
                    }

                    public void actionPerformed(ActionEvent e) {
                    }
                }));
                fileMenu.add(new JMenuItem(new SaveAsFileAction(chooser)));

                JMenu settingsMenu = new JMenu("Settings");
                settingsMenu.add(new JCheckBoxMenuItem(new ToggleSheetsAction(chooser)));
                settingsMenu.add(new JCheckBoxMenuItem(new TogglePackageIsTraversableAction(chooser)));

                JMenuBar menubar = new JMenuBar();
                menubar.add(fileMenu);
                menubar.add(settingsMenu);

                JFrame frame = new JFrame("The frame...");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setPreferredSize(new Dimension(300, 200));
                frame.add(new JLabel("Pure Java. Almost...", JLabel.CENTER));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setJMenuBar(menubar);
                frame.setVisible(true);
            }
        });
    }

    private static Component getOwnerFromSource(ActionEvent e) {
        Component source = (Component) e.getSource();
        if (source instanceof JMenuItem) {
            JMenuItem item = (JMenuItem) source;

            Container container = item.getParent();
            if (container instanceof JPopupMenu) {
                JPopupMenu menu = (JPopupMenu) container;
                source = menu.getInvoker();
            }
        }
        return source;
    }

    private static class OpenFilesAction extends AbstractAction {
        static final FileFilter FILTER_IMAGE = new FileFilter() {
            public boolean accept(final File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".png")
                        || name.endsWith(".gif")
                        || name.endsWith(".jpg") || name.endsWith(".jpeg")
                        || name.endsWith(".tif") || name.endsWith(".tiff")
                        || name.endsWith(".psd")
                        || name.endsWith(".pct") || name.endsWith(".pict");
            }

            public String getDescription() {
                return "Image files";
            }
        };

        static final FileFilter FILTER_TEXT = new FileFilter() {
            public boolean accept(final File f) {
                if (System.getProperty("os.name").startsWith("Mac OS X")) {
                    // TODO: A cross-platform solution (that compiles too)...
                    try {
                        int fileType = FileManager.getFileType(f.getAbsolutePath());
                        if (fileType == FileManager.OSTypeToInt("TEXT")) {
                            return true;
                        }
                    }
                    catch (IOException ignore) {
                    }
                    catch (LinkageError ignore) {
                    }
                }
                return f.getName().toLowerCase().endsWith(".txt");
            }

            public String getDescription() {
                return "Text files";
            }
        };

        final JFileChooser chooser;
        FileFilter currentFilter;

        public OpenFilesAction(final JFileChooser fileChooser) {
            super("open-file");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            putValue(Action.NAME, "Open File...");

            chooser = fileChooser;
        }

        public void actionPerformed(ActionEvent e) {
            chooser.setDialogTitle("Open files or folder");
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setMultiSelectionEnabled(true);

            chooser.resetChoosableFileFilters();
            chooser.addChoosableFileFilter(chooser.getAcceptAllFileFilter());
            chooser.addChoosableFileFilter(FILTER_TEXT);
            chooser.addChoosableFileFilter(FILTER_IMAGE);
            chooser.setFileFilter(currentFilter != null ? currentFilter : FILTER_TEXT);

            if (chooser.showOpenDialog(getOwnerFromSource(e)) == JFileChooser.APPROVE_OPTION) {
                currentFilter = chooser.getFileFilter();
                System.out.println("chooser.getSelectedFile(): " + chooser.getSelectedFile());
                System.out.println("chooser.getSelectedFiles(): " + Arrays.toString(chooser.getSelectedFiles()));
                if (chooser.getSelectedFile() != null) {
                    System.out.println("chooser.getSelectedFile().exists(): " + chooser.getSelectedFile().exists());
                    for (File file : chooser.getSelectedFiles()) {
                        if (file.isDirectory()) {
                             System.out.println("file.listFiles(): " + Arrays.toString(file.listFiles()));
                         }
                     }
                }
            }
        }
    }

    private static class OpenDirAction extends AbstractAction {
        final JFileChooser chooser;

        public OpenDirAction(final JFileChooser fileChooser) {
            super("open-dir");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK));
            putValue(Action.NAME, "Choose Folder...");

            chooser = fileChooser;
        }

        public void actionPerformed(ActionEvent e) {
            chooser.setDialogTitle("Choose folder");
            chooser.setApproveButtonText("Choose");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.resetChoosableFileFilters();
            chooser.setMultiSelectionEnabled(false);

            if (chooser.showOpenDialog(getOwnerFromSource(e)) == JFileChooser.APPROVE_OPTION) {
                System.out.println("chooser.getSelectedFile(): " + chooser.getSelectedFile());
                System.out.println("chooser.getSelectedFiles(): " + Arrays.toString(chooser.getSelectedFiles()));
                if (chooser.getSelectedFile() != null) {
                    System.out.println("chooser.getSelectedFile().exists(): " + chooser.getSelectedFile().exists());
                }
            }
        }
    }

    private static class SaveAsFileAction extends AbstractAction {
        final JFileChooser chooser;

        public SaveAsFileAction(final JFileChooser fileChooser) {
            super("save-as");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK));
            putValue(Action.NAME, "Save as...");

            chooser = fileChooser;
        }

        public void actionPerformed(ActionEvent e) {
            chooser.setDialogTitle(null);
            chooser.setDialogTitle("Save as");
            chooser.setApproveButtonText(null);
            chooser.setMultiSelectionEnabled(false);
            chooser.resetChoosableFileFilters();

            if (chooser.showSaveDialog(getOwnerFromSource(e)) == JFileChooser.APPROVE_OPTION) {
                System.out.println("chooser.getSelectedFile(): " + chooser.getSelectedFile());
                System.out.println("chooser.getSelectedFiles(): " + Arrays.toString(chooser.getSelectedFiles()));
                if (chooser.getSelectedFile() != null) {
                    System.out.println("chooser.getSelectedFile().exists(): " + chooser.getSelectedFile().exists());
                }
            }
        }
    }

    private static class ToggleSheetsAction extends AbstractAction {
        private final static boolean AVAILABLE = isJava6();

        private static boolean isJava6() {
            try {
                Class<?> dialogClass = Class.forName("java.awt.Dialog");
                Class<?>[] declaredClasses = dialogClass.getDeclaredClasses();
                for (Class<?> declaredClass : declaredClasses) {
                    if (declaredClass.getSimpleName().equals("ModalityType")) {
                        return true;
                    }
                }
            }
            catch (Throwable ignore) {
            }

            return false;
        }

        private final JFileChooser mChooser;

        public ToggleSheetsAction(final JFileChooser pChooser) {
            super("sheets");
            mChooser = pChooser;

            putValue(Action.NAME, "Use sheets (document modal, Java 6+)");
            putValue(Action.SELECTED_KEY, false);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));

            if (!AVAILABLE) {
                setEnabled(false);
            }
        }

        public void actionPerformed(final ActionEvent e) {
            boolean selected = (Boolean) getValue(Action.SELECTED_KEY);

            if (AVAILABLE) {
                mChooser.putClientProperty("JFileChooser.modalityType", selected ? Dialog.ModalityType.DOCUMENT_MODAL : Dialog.ModalityType.APPLICATION_MODAL);
            }
        }
    }

    private static class TogglePackageIsTraversableAction extends AbstractAction {
        private final JFileChooser chooser;

        public TogglePackageIsTraversableAction(final JFileChooser chooser) {
            super("packages");
            this.chooser = chooser;

            putValue(Action.NAME, "Allow traversing packages");
            putValue(Action.SELECTED_KEY, false);
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, 0));
        }

        public void actionPerformed(final ActionEvent e) {
            chooser.putClientProperty("JFileChooser.packageIsTraversable", ((JCheckBoxMenuItem) e.getSource()).isSelected() ? "always" : "never");
        }
    }
}