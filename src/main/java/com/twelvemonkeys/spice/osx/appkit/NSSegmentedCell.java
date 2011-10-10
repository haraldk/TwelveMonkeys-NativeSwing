package com.twelvemonkeys.spice.osx.appkit;

/**
 * NSSegmentedCell
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSSegmentedCell.java,v 1.0 Dec 20, 2010 11:20:33 AM haraldk Exp$
 */
public interface NSSegmentedCell extends NSCell {

    final static public int NSSegmentSwitchTrackingSelectOne = 0;
    final static public int NSSegmentSwitchTrackingSelectAny = 1;
    final static public int NSSegmentSwitchTrackingMomentary = 2;

    void setTrackingMode(int trackingMode);
    int trackingMode();
}
