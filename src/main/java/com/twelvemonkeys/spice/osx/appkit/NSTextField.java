package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.RunOnMainThread;

/**
 * NSTextField
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSTextField.java,v 1.0 Jan 11, 2009 9:24:01 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSTextField extends NSControl {

    void setEditable(boolean editable);
    boolean isEditable();

    void setSelectable(boolean selectable);
    boolean isSelectable();

    void setBezeled(boolean bezeled);
    boolean isBezeled();

    /*typedef enum {
   NSTextFieldSquareBezel  = 0,
   NSTextFieldRoundedBezel = 1
} NSTextFieldBezelStyle;
*/
    void setBezelStyle(int style);
    int bezelStyle();
    void setBordered(boolean bordered);
    boolean isBordered();

//    void setBackgroundColor(NSColor color);
//    NSColor backgroundColor()
    void setDrawsBackground(boolean draw);
    boolean drawsBackground();

    void setTitleWithMnemonic(String string);
}
