import com.twelvemonkeys.spice.NativeFileChooser;
import com.twelvemonkeys.spice.osx.appkit.NSOpenPanel;
import com.twelvemonkeys.spice.osx.appkit.NSSavePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Arrays;

/**
 * TestOpenPanel
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestOpenPanel.java,v 1.0 Mar 21, 2008 8:23:14 PM haraldk Exp$
 */
public class TestOpenPanel {
    public static void main(String[] pArgs) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JMenu fileMenu = new JMenu("File");
                fileMenu.add(new JMenuItem(new OpenAction()));
                fileMenu.add(new JMenuItem(new AbstractAction("save") {
                    {
                        putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
                        putValue(Action.NAME, "Save");
                        setEnabled(false);
                    }

                    public void actionPerformed(ActionEvent e) {
                    }
                }));
                fileMenu.add(new JMenuItem(new SaveAsAction()));

                JMenuBar menubar = new JMenuBar();
                menubar.add(fileMenu);

                // Need a frame (or something that is the "application"?)
                JFrame frame = new JFrame("The frame...");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setJMenuBar(menubar);
                frame.setVisible(true);
            }
        });
    }

    private static class OpenAction extends AbstractAction {
        final JFileChooser chooser;

        public OpenAction() {
            super("open");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            putValue(Action.NAME, "Open File...");

            chooser = new NativeFileChooser();
            chooser.setName(getClass().getName());
            chooser.setDialogTitle("Open file");
            chooser.setMultiSelectionEnabled(true);
        }

        public void actionPerformed1(ActionEvent e) {
            chooser.showOpenDialog(null);
            System.out.println("chooser.getSelectedFile(): " + chooser.getSelectedFile());
            System.out.println("chooser.getSelectedFiles(): " + Arrays.toString(chooser.getSelectedFiles()));
            if (chooser.getSelectedFile() != null) {
                System.out.println("chooser.getSelectedFile().exists(): " + chooser.getSelectedFile().exists());
            }
        }


        public void actionPerformed(ActionEvent e) {
            NSOpenPanel open = NSOpenPanel.openPanel();
            open.setFrameAutosaveName(getClass().getName());
            open.setTitle("Open file");
            open.setPrompt("Open");
            open.setAllowsMultipleSelection(true);
            open.setCanChooseDirectories(true);
//            open.setCanCreateDirectories(true);

//            System.out.println("open.title(): " + open.title());
//            System.out.println("open.prompt(): " + open.prompt());
//            System.out.println("open.message(): " + open.message());
//            System.out.println("open.canChooseDirectories(): " + open.canChooseDirectories());
//            System.out.println("open.canChooseFiles(): " + open.canChooseFiles());
//            System.out.println("open.canCreateDirectories(): " + open.canCreateDirectories());

//            open.setDirectory(System.getProperty("user.home"));
//            System.out.println("open.directory(): " + open.directory());

            // runModalForTypes seems to work (!)
            // But have some reapint issues, with large/slow directories? (contents does not show)
            // Seems to happen more frequently when using a filter
            // Also, there seems to be some modality issues, if I press Apple Q for example...
//            int result = open.runModal();
//            int result = open.runModalForTypes(
//                    null
////                NSArray.CLASS.arrayWithObjects(NSString.CLASS.stringWithString("txt"))
//            );
            // runModalForDirectory_file_types still fails, with the "*** -[NSOpenPanel runModalForDirectory_file_types:]: selector not recognized [self = 0x396800]" message
            int result = open.runModal(
                    new File(System.getProperty("user.home")),
                    null,
                    Arrays.asList("txt")
            );
            if (result == NSSavePanel.NSOKButton) {
                System.out.println("open.filenames: " + open.filenames());
                System.out.println("open.URLs: " + open.URLs());
            }
//        open.beginForDirectory(
//                null,
//                null,
//                null,
//                new ID(),
//                new Selector("Hurra", 0),
//                null
//                );
        }
    }

    private static class SaveAsAction extends AbstractAction {
        final JFileChooser chooser;

        public SaveAsAction() {
            super("save-as");
            putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK));
            putValue(Action.NAME, "Save as...");

            chooser = new NativeFileChooser();
            chooser.setName(getClass().getName());
            chooser.setDialogTitle("Save as");
        }

        public void actionPerformed1(ActionEvent e) {
            chooser.showSaveDialog(null);
            System.out.println("chooser.getSelectedFile(): " + chooser.getSelectedFile());
            System.out.println("chooser.getSelectedFiles(): " + Arrays.toString(chooser.getSelectedFiles()));
            if (chooser.getSelectedFile() != null) {
                System.out.println("chooser.getSelectedFile().exists(): " + chooser.getSelectedFile().exists());
            }
        }

        public void actionPerformed(ActionEvent e) {
            NSSavePanel save = NSSavePanel.savePanel();
            save.setFrameAutosaveName(getClass().getName());
            save.setTitle("Save file");
            save.setPrompt("Save");
            save.setCanCreateDirectories(true);

//            System.out.println("save.title(): " + save.title());
//            System.out.println("save.prompt(): " + save.prompt());
//            System.out.println("save.message(): " + save.message());
//            System.out.println("save.canCreateDirectories(): " + save.canCreateDirectories());
//
//            save.setDirectory(System.getProperty("user.home"));
//            System.out.println("save.directory(): " + save.directory());

            // runModalForTypes seems to work (!)
            // But have some reapint issues, with large/slow directories? (contents does not show)
            // Seems to happen more frequently when using a filter
            // Also, there seems to be some modality issues, if I press Apple Q for example...
            int result = save.runModal();
//            save.beginSheetForDirectory_file_modalDelegate_didEndSelector_contextInfo(
//                    null, null,
//
//
//            );
            // runModalForDirectory_file_types still fails, with the "*** -[NSSavePanel runModalForDirectory_file_types:]: selector not recognized [self = 0x396800]" message
//                int result = save.runModalForDirectory_file_types(
//                        System.getProperty("user.home"),
//                        null,
//                        NSArray.CLASS.arrayWithObjects(NSString.CLASS.stringWithString("txt"))
//                );
            if (result == NSSavePanel.NSOKButton) {
//                System.out.println("save.filename: " + save.filename()); // always null?!
                File file = new File(save.URL().toString());
                System.out.println("save.URL: " + file);
                System.out.println("file.exists(): " + file.exists());
            }
//        save.beginForDirectory(
//                null,
//                null,
//                null,
//                new ID(),
//                new Selector("Hurra", 0),
//                null
//                );
        }
    }

}
