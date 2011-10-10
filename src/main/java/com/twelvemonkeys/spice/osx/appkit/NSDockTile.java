package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.NSObject;
import org.rococoa.RunOnMainThread;

/**
 * NSDockTile
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSDockTile.java,v 1.0 Jan 11, 2009 10:10:41 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSDockTile extends NSObject {
    void setShowsApplicationBadge(boolean show);
    boolean showsApplicationBadge();

    void setBadgeLabel(String string);
    String badgeLabel();

    void display();

}
