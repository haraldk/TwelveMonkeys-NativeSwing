package com.twelvemonkeys.spice.osx.appkit;

import com.sun.jna.NativeLibrary;
import org.rococoa.*;
import org.rococoa.cocoa.foundation.NSSize;
import org.rococoa.cocoa.foundation.NSString;

/**
 * NSToolbarItem
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSToolbarItem.java,v 1.0 Jun 23, 2009 12:29:53 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSToolbarItem extends NSObject {
    public static _Class CLASS = Rococoa.createClass("NSToolbarItem", _Class.class);

    @RunOnMainThread
    static abstract class _Class implements NSClass {
        public NSToolbarItem initWithItemIdentifier(String identifier) {
            return Rococoa.create("NSToolbarItem", NSToolbarItem.class, "alloc").initWithItemIdentifier(identifier);
        }

        static NSString getAppKitGlobalString(String globalVarName) {
            return Rococoa.wrap(ID.fromLong(NativeLibrary.getInstance("AppKit").getGlobalVariableAddress(globalVarName).getNativeLong(0).longValue()), NSString.class);
        }
    }

    NSToolbarItem initWithItemIdentifier(String identifier);
    String itemIdentifier();

    void setLabel(String label);
    String label();

    void setPaletteLabel(String label);
    String paletteLabel();

    void setEnabled(boolean enabled);
    boolean isEnabled();

    void validate();
    void setAutovalidates(boolean auto);
    boolean autovalidates();

    void setImage(NSImage image);
    NSImage image();

    void setMaxSize(NSSize size);
    NSSize maxSize();

    void setMinSize(NSSize size);
    NSSize minSize();

    void setView(NSView view);
    NSView view();

    void setTarget(ID id);
    void setAction(Selector selector);
    
    NSString NSToolbarSeparatorItemIdentifier = _Class.getAppKitGlobalString("NSToolbarSeparatorItemIdentifier");
    NSString NSToolbarSpaceItemIdentifier = _Class.getAppKitGlobalString("NSToolbarSpaceItemIdentifier");
    NSString NSToolbarFlexibleSpaceItemIdentifier = _Class.getAppKitGlobalString("NSToolbarFlexibleSpaceItemIdentifier");
    NSString NSToolbarShowColorsItemIdentifier = _Class.getAppKitGlobalString("NSToolbarShowColorsItemIdentifier");
    NSString NSToolbarShowFontsItemIdentifier = _Class.getAppKitGlobalString("NSToolbarShowFontsItemIdentifier");
    NSString NSToolbarCustomizeToolbarItemIdentifier = _Class.getAppKitGlobalString("NSToolbarCustomizeToolbarItemIdentifier");
    NSString NSToolbarPrintItemIdentifier = _Class.getAppKitGlobalString("NSToolbarPrintItemIdentifier");
}
