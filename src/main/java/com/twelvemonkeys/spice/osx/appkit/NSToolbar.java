package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.*;

/**
 * NSToolbar
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSToolbar.java,v 1.0 Jun 23, 2009 12:11:58 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSToolbar extends NSObject {
    public static _Class CLASS = Rococoa.createClass("NSToolbar", _Class.class);

    @RunOnMainThread
    static abstract class _Class implements NSClass {
        public NSToolbar initWithIdentifier(String identifier) {
            return Rococoa.create("NSToolbar", NSToolbar.class, "alloc").initWithIdentifier(identifier);
        }
    }

    NSToolbar initWithIdentifier(String identifier);

    void setDelegate(ID id);
    ID delegate();

    void insertItemWithItemIdentifier_atIndex(String identifier, int index);

    void setShowsBaselineSeparator(boolean show);
    boolean showsBaselineSeparator();

    void setDisplayMode(int mode);
    int displayMode();

    boolean allowsUserCustomization();
    void setAllowsUserCustomization(boolean allow); 

    void runCustomizationPalette(ID pID);
    boolean customizationPaletteIsRunning();

    void setVisible(boolean visible);
    boolean isVisible();
    /*
typedef enum {
   NSToolbarDisplayModeDefault,
   NSToolbarDisplayModeIconAndLabel,
   NSToolbarDisplayModeIconOnly,
   NSToolbarDisplayModeLabelOnly
} NSToolbarDisplayMode;

     */

    final int NSToolbarDisplayModeDefault = 0;
    final int NSToolbarDisplayModeIconAndLabel = 1;
    final int NSToolbarDisplayModeIconOnly = 2;
    final int NSToolbarDisplayModeLabelOnly = 3;

    /*
typedef enum {
   NSToolbarSizeModeDefault,
   NSToolbarSizeModeRegular,
   NSToolbarSizeModeSmall
} NSToolbarSizeMode;

     */

    final int NSToolbarSizeModeDefault = 0;
    final int NSToolbarSizeModeRegular = 1;
    final int NSToolbarSizeModeSmall = 2;
}
