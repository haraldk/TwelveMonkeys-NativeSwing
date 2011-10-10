package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.OSXImageUtil;
import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * NSWorkspace
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSWorkspace.java,v 1.0 Jan 16, 2010 2:38:10 PM haraldk Exp$
 */
public abstract class NSWorkspace implements NSObject {
    private static final _Class CLASS = Rococoa.createClass("NSWorkspace", _Class.class);

    private interface _Class extends NSClass {
        NSWorkspace sharedWorkspace();
    }

    public static NSWorkspace sharedWorkspace() {
        return CLASS.sharedWorkspace();
    }

    public abstract NSImage iconForFile(String fullPath);

    // TODO: Consider moving this method to IconFactory
    public final BufferedImage iconForFile(final File path, final Dimension size) {
        return OSXImageUtil.toBufferedImage(iconForFile(path.getAbsolutePath()), size);
    }

    public abstract NSImage iconForFileType(String type);    

    // TODO: Consider moving this method to IconFactory
    public final BufferedImage iconForFileType(final String fileType, final Dimension size) {
        return OSXImageUtil.toBufferedImage(iconForFileType(fileType), size);
    }

    public abstract boolean isFilePackageAtPath(String fullPath);

    public final boolean isFilePackage(final File path) {
        return isFilePackageAtPath(path.getAbsolutePath());
    }
}
