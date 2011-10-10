package com.twelvemonkeys.spice.osx.quicklook;

import com.sun.jna.NativeLibrary;
import com.twelvemonkeys.spice.osx.appkit.NSPanel;
import org.rococoa.ID;
import org.rococoa.NSClass;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;

/**
* QLPreviewPanel
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: QLPreviewPanel.java,v 1.0 Mar 11, 2010 11:39:43 AM haraldk Exp$
*/
@RunOnMainThread
abstract class QLPreviewPanel implements NSPanel {
    private static final NativeLibrary LIB = NativeLibrary.getInstance("Quartz");

    public static final _Class CLASS = Rococoa.createClass("QLPreviewPanel", _Class.class);

    protected interface _Class extends NSClass {
        QLPreviewPanel sharedPreviewPanel();
        boolean sharedPreviewPanelExists();
    }

    public static boolean sharedPreviewPanelExists() {
        return CLASS.sharedPreviewPanelExists();
    }

    public static QLPreviewPanel sharedPreviewPanel() {
        return CLASS.sharedPreviewPanel();
    }

    public abstract ID currentController();

    public abstract void setDataSource(ID id);
    public abstract ID dataSource();

    public abstract void setCurrentPreviewItemIndex(int index);
    public abstract int currentPreviewItemIndex();
}
