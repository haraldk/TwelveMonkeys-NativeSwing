package com.twelvemonkeys.spice.osx.appkit;

import com.sun.jna.Pointer;
import org.rococoa.*;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSURL;

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
public abstract class NSSavePanel implements NSPanel {
    public static final int NSOKButton = 1;
    public static final int NSCancelButton = 0;

    // TODO: Consider moving these to NSArray (maybe rename to arrayWithObjects?)

    private static final _Class CLASS = Rococoa.createClass("NSSavePanel", _Class.class);

    @RunOnMainThread
    private static interface _Class extends NSClass {
        NSSavePanel savePanel();
    }

    public static NSSavePanel savePanel() {
        return CLASS.savePanel();
    }

    public abstract NSView accessoryView();
    public abstract void setAccessoryView(NSView view);

    public abstract String title();
    public abstract void setTitle(String title);

    public abstract String prompt();
    public abstract void setPrompt(String prompt);

    public abstract String nameFieldLabel();
    public abstract void setNameFieldLabel(String label);

    public abstract String message();
    public abstract void setMessage(String message);

    public abstract boolean canCreateDirectories();
    public abstract void setCanCreateDirectories(boolean can);

    public abstract boolean isExtensionHidden();
    public abstract void setExtensionHidden(boolean hidden);

    public abstract String requiredFileType();
    public abstract void setRequiredFileType(String type);

    public abstract boolean canSelectHiddenExtension();
    public abstract void setCanSelectHiddenExtension(boolean can);

    abstract NSArray allowedFileTypes();
    public final List<String> getAllowedFileTypes() {
        return NSArrayUtil.asList(allowedFileTypes());
    }

    abstract void setAllowedFileTypes(NSArray types);

    public final void setAllowedFileTypes(List<String> types) {
        setAllowedFileTypes(NSArrayUtil.toNSArray(types));
    }

    public abstract boolean allowsOtherFileTypes();
    public abstract void setAllowsOtherFileTypes(boolean allow);

    public abstract boolean treatsFilePackagesAsDirectories();
    public abstract void setTreatsFilePackagesAsDirectories(boolean packageAsDir);

    /**
     * @deprecated as of OS X 10.6. Use {@link #directoryURL()} instead.
     */
    public abstract String directory();

    /**
     * @deprecated as of OS X 10.6. Use {@link #setDirectoryURL(org.rococoa.cocoa.foundation.NSURL)} instead.
     */
    abstract void setDirectory(String directory);

    public final void setDirectory(File directory) {
        // TODO: Test for OS X 10.6 and use setDirectoryURL instead?
        setDirectory(directory != null ? directory.getAbsolutePath() : null);
    }

    public final File getDirectory() {
        // TODO: Test for OS X 10.6 and use directoryURL instead?
        return new File(directory());
    }

    abstract void setDirectoryURL(NSURL url);
    abstract NSURL directoryURL();

    abstract void beginSheetModalForWindow_completionHandler(NSWindow window, ID completionHandler); 

//    public final void beginSheet(Window window, Callback completionHandler) {
//        beginSheetModalForWindow_completionHandler(NSWindow.CLASS.windowFromAWT(window), );
//    }

    /**
     * @deprecated as of OS X 10.6. Use {@link #beginSheetModalForWindow_completionHandler} instead.
     */
    abstract void beginSheetForDirectory_file_modalForWindow_modalDelegate_didEndSelector_contextInfo(String absoluteDirectoryPath,
                                                String file,
                                                NSWindow docWindow,
                                                ID modalDelegate,
                                                Selector didEndSelector,
                                                Pointer contextInfo);
    /**
     * @deprecated as of OS X 10.6. Use {@link #beginSheet(Window, Object)} instead.
     *             Call {@link #setDirectoryURL(org.rococoa.cocoa.foundation.NSURL)} to set directory. 
     */
    public final void beginSheet(File directory,
                                 File file,
                                 Window docWindow,
                                 ID modalDelegate,
                                 Selector didEndSelector,
                                 Pointer contextInfo) {
        beginSheetForDirectory_file_modalForWindow_modalDelegate_didEndSelector_contextInfo(
                directory != null ? directory.getAbsolutePath() : null,
                file != null ? file.getName() : null,
                NSWindow.CLASS.windowFromAWT(docWindow),
                modalDelegate,
                didEndSelector,
                contextInfo
        );
    }

    public abstract int runModal();

    /**
     * @deprecated as of OS X 10.6. Use {@link #runModal()} instead.
     */
    public final int runModal(final File directory, final File file) {
        // TODO: What if file is not in directory? What if directory is null, and file is not?
        // TODO: This method is deprecated as of OS X 10.6
        return runModalForDirectory_file(
                directory != null ? directory.getAbsolutePath() : null,
                file != null ? file.getName() : null
        );
    }

    /**
     * @deprecated as of OS X 10.6. Use {@link #runModal()} instead.
     */
    abstract int runModalForDirectory_file(String directory, String file);

    public abstract void validateVisibleColumns();
//    * ? panel:compareFilename:with:caseSensitive:  delegate method
//    * ? panel:isValidFilename:  delegate method
//    * ? panel:userEnteredFilename:confirmed:  delegate method
//    * ? validateVisibleColumns
//    * ? panel:shouldShowFilename:  delegate method
//    * ? panel:willExpand:  delegate method
//    * ? panel:directoryDidChange:  delegate method
//    * ? panelSelectionDidChange:  delegate method


    /** @return the absolute pathname of the file currently shown in the receiver.
     * @deprecated as of OS X 10.6. Use {@link #URL()} instead. */
    public abstract String filename();

    /** @return the absolute pathname of the file currently shown in the receiver as a URL. */
    public abstract NSURL URL();

    /** @return whether the receiver is expanded. */
    public abstract boolean isExpanded();
}