package com.twelvemonkeys.spice.osx.appkit;

import com.sun.jna.Native;
import com.twelvemonkeys.spice.osx.foundation.NSRect;
import org.rococoa.ID;
import org.rococoa.NSClass;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.CGFloat;

import java.awt.*;

/**
 * NSWindow
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSWindow.java,v 1.0 Mar 22, 2008 8:40:16 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSWindow extends NSResponder {

    public static final _Class CLASS = Rococoa.createClass("NSWindow", _Class.class);

    static abstract class _Class implements NSClass {
        public NSWindow windowFromAWT(final Window window) {
            NSView view = Rococoa.wrap(ID.fromLong(Native.getWindowID(window)), NSView.class);
            return view.window();
        }

        public abstract NSRect contentRectForFrameRect_styleMask(NSRect frameRect, int styleMask);
    }

    NSWindowController windowController();
    void setWindowController(NSWindowController controller);

    String frameAutosaveName();
    void setFrameAutosaveName(String name);

    void setDelegate(ID delegate);
    ID delegate();

    NSDockTile dockTile();

    NSView contentView();
    
    NSRect frame();
    void setFrame_display(NSRect frame, boolean display);
    void setFrame_display_animate(NSRect frame, boolean display, boolean animate);

    boolean  preservesContentDuringLiveResize();
    void setPreservesContentDuringLiveResize(boolean preserve);

    boolean showsResizeIndicator();
    void setShowsResizeIndicator(boolean show);

    void _startLiveResize();
    void _endLiveResize();

    void setToolbar(NSToolbar pToolbar);
    NSToolbar toolbar();

    void setShowsToolbarButton(boolean show);
    boolean showsToolbarButton();

    int styleMask();

    void makeKeyAndOrderFront(ID sender);
    void orderOut(ID sender);

    void setAutorecalculatesContentBorderThickness_forEdge(boolean auto, int eddge);
    boolean autorecalculatesContentBorderThicknessForEdge(int eddge);

    void setContentBorderThickness_forEdge(CGFloat thickness, int edge);
    CGFloat contentBorderThicknessForEdge(int edge);
}
