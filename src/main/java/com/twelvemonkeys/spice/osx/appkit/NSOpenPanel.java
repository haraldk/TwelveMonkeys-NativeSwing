package com.twelvemonkeys.spice.osx.appkit;

import com.sun.jna.Pointer;
import org.rococoa.*;
import org.rococoa.cocoa.foundation.NSArray;

import java.awt.*;
import java.io.File;
import java.util.List;

/**
 * NSOpenPanel
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSOpenPanel.java,v 1.0 Mar 21, 2008 8:26:25 PM haraldk Exp$
 */
@RunOnMainThread
public abstract class NSOpenPanel extends NSSavePanel {
    private static final _Class CLASS = Rococoa.createClass("NSOpenPanel", _Class.class);
    @RunOnMainThread
    private static interface _Class extends NSClass {
        NSOpenPanel openPanel();
    }

    static public NSOpenPanel openPanel() {
        return CLASS.openPanel();
    }

    public final void begin(File directory,
                      File file,
                      String[] fileTypes,
                      ID modelessDelegate,
                      Selector didEndSelector,
                      Pointer contextInfo) {

        beginForDirectory_file_types_modelessDelegate_didEndSelector_contextInfo(
                directory != null ? directory.getAbsolutePath() : null,
                file != null ? file.getName() : null,
                NSArrayUtil.toNSArray(fileTypes),
                modelessDelegate,
                didEndSelector,
                contextInfo
        );
    }

    abstract void beginForDirectory_file_types_modelessDelegate_didEndSelector_contextInfo(
            String absoluteDirectoryPath,
            String filename,
            NSArray fileTypes,
            ID modelessDelegate,
            Selector didEndSelector,
            Pointer contextInfo
    );

    public final void beginSheet(File directory,
                           File file,
                           String[] fileTypes,
                           Window docWindow,
                           ID modalDelegate,
                           Selector didEndSelector,
                           Pointer contextInfo) {

        beginSheetForDirectory_file_types_modalForWindow_modalDelegate_didEndSelector_contextInfo(
                directory != null ? directory.getAbsolutePath() : null,
                file != null ? file.getName() : null,
                NSArrayUtil.toNSArray(fileTypes),
                NSWindow.CLASS.windowFromAWT(docWindow),
                modalDelegate,
                didEndSelector,
                contextInfo
        );
    }
    abstract void beginSheetForDirectory_file_types_modalForWindow_modalDelegate_didEndSelector_contextInfo(
            String absoluteDirectoryPath,
            String filename,
            NSArray fileTypes,
            NSWindow docWindow,
            ID modalDelegate,
            Selector didEndSelector,
            Pointer contextInfo
    );

    public final int runModal(final File directory, final File file, List<String> fileTypes) {
        return runModalForDirectory_file_types(
                directory != null ? directory.getAbsolutePath() : null,
                file != null ? file.getName() : null,
                NSArrayUtil.toNSArray(fileTypes)
        );
    }
    abstract int runModalForDirectory_file_types(String absoluteDirectoryPath,
                                                           String filename,
                                                           NSArray fileTypes);

    public final int runModal(final List<String> fileTypes) {
        return runModalForTypes(NSArrayUtil.toNSArray(fileTypes));
    }
    abstract int runModalForTypes(NSArray fileTypes);

    /**
     * @return an array containing the absolute paths (as NSString objects) of the selected files and directories.
     */
    public abstract NSArray filenames();

    /**
     * @return an array containing the absolute paths of the selected files and directories as URLs.
     */
    public abstract NSArray URLs();

    public abstract boolean canChooseDirectories();

    public abstract void setCanChooseDirectories(boolean can);

    public abstract boolean canChooseFiles();

    public abstract void setCanChooseFiles(boolean can);

    public abstract boolean resolvesAliases();

    public abstract void setResolvesAliases(boolean resolve);

    public abstract boolean allowsMultipleSelection();

    public abstract void setAllowsMultipleSelection(boolean allow);
}
