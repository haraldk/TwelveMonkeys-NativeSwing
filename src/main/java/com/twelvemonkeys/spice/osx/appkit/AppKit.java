package com.twelvemonkeys.spice.osx.appkit;

import com.sun.jna.Library;
import com.sun.jna.Native;
import org.rococoa.ID;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSString;

/**
 * AppKit
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: AppKit.java,v 1.0 Apr 2, 2010 8:34:33 PM haraldk Exp$
 */
public final class AppKit {

    private static final AKLibrary INSTANCE = (AKLibrary) Native.loadLibrary("AppKit", AKLibrary.class);

    public static interface AKLibrary extends Library {
        ID NSCreateFileContentsPboardType(ID type);
    }

    /**
     * Returns an String to a pasteboard type representing a file's contents based on the supplied string {@code fileType}.
     * {@code fileType} should generally be the extension part of a filename.
     * The conversion from a named file type to a pasteboard type is simple; no mapping to standard pasteboard types
     * is attempted.
     *
     * @param fileType the file type
     * @return the pasteboard type
     *
     * @deprecated The file contents pboard type allowed you to synthesize a pboard type for a file?s contents based on
     *             the file?s extension. Using the UTI of a file to represent its contents now replaces this functionality.
     */
    @Deprecated
    public static String NSCreateFileContentsPboardType(final String fileType) {
        // TODO: Does it have to be this ugly??
        return Rococoa.wrap(INSTANCE.NSCreateFileContentsPboardType(NSString.stringWithString(fileType).id()), NSString.class).toString();
    }
}
