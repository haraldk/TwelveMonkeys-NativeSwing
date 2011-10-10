package com.twelvemonkeys.spice;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;

/**
 * NativeFileChooser
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NativeFileChooser.java,v 1.0 Mar 23, 2008 9:28:09 PM haraldk Exp$
 */
public class NativeFileChooser extends JFileChooser {

    final FileChooserDelegate delegate;

    public NativeFileChooser() {
        this((String) null);
    }

    public NativeFileChooser(final String currentDirectoryPath) {
        super(currentDirectoryPath);

        // TODO: SPI based?
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            delegate = new OSXFileChooserDelegate();
        }
        else {
            delegate = null;
        }
    }

    public NativeFileChooser(final File currentDirectory) {
        this(currentDirectory != null ? currentDirectory.getAbsolutePath() : null);
    }

    @Override
    public void setFileSystemView(final FileSystemView fsv) {
        // NOTE: This is called once BEFORE the constructor is done, so final semantics might not apply here..
        if (delegate != null) {
            throw new UnsupportedOperationException("Custom file system view not supported for NativeFileChooser, please use JFileChooser instead");
        }

        super.setFileSystemView(fsv);
    }

    @Override
    public void setAccessory(final JComponent newAccessory) {
        // TODO: We might be able to support this in the future, not a priority now
        throw new UnsupportedOperationException("Accessory component not supported for NativeFileChooser, please use JFileChooser instead");
    }

    @Override
    public int showDialog(final Component parent, final String approveButtonText) throws HeadlessException {
        if (delegate != null) {
            return delegate.showDialog(this, parent, approveButtonText);
        }
        else {
            return super.showDialog(parent, approveButtonText);
        }
    }

    @Override
    protected JDialog createDialog(final Component parent) throws HeadlessException {
        return super.createDialog(parent);
    }
}
