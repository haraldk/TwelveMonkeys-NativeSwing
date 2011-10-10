package com.twelvemonkeys.spice;

import javax.swing.*;
import java.awt.*;

/**
 * FileChooserDelegate
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: FileChooserDelegate.java,v 1.0 Mar 23, 2008 9:37:29 PM haraldk Exp$
 */
public interface FileChooserDelegate {
    int showDialog(JFileChooser chooser, Component parent, String approveButtonText);
}
