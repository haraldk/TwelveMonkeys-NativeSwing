package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;
import org.rococoa.cocoa.foundation.NSSize;

@RunOnMainThread
public abstract class NSDrawer implements NSObject{

    public static NSDrawer create(NSSize size, int edge) {
        return Rococoa.create("NSDrawer", NSDrawer.class, "alloc").initWithContentSize_preferredEdge(size, edge);
    }

    abstract NSDrawer initWithContentSize_preferredEdge(NSSize contentSize, int edge);

    public abstract void setParentWindow(NSWindow window);
    public abstract NSWindow parentWindow();

    public abstract void open();
    public abstract void open(ID sender);

    public abstract void close();
    public abstract void close(ID sender);

    public abstract int state();

    public abstract void setDelegate(ID id);
    public abstract ID delegate();


    public abstract NSSize minContentSize();

    public abstract void setMinContentSize(NSSize nsSize);

    public abstract void setMaxContentSize(NSSize contentSize);

    public abstract void openOnEdge(int edge);

    public abstract void setContentView(NSView content);
    public abstract NSView contentView();

    public abstract NSSize contentSize();

    public static final int NSDrawerClosedState  = 0,
    NSDrawerOpeningState = 1,
    NSDrawerOpenState    = 2,
       NSDrawerClosingState = 3;

}
