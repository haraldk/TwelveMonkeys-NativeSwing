package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.*;
import org.rococoa.cocoa.foundation.NSDictionary;

/**
 * NSDistributedNotificationCenter
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSDistributedNotificationCenter.java,v 1.0 Mar 26, 2009 5:47:51 PM haraldk Exp$
 */
public abstract class NSDistributedNotificationCenter implements NSObject {
    public static final _Class CLASS = Rococoa.createClass("NSDistributedNotificationCenter", _Class.class);  //$NON-NLS-1$

    public interface _Class extends NSClass {
        NSDistributedNotificationCenter defaultCenter();

        // Returns the distributed notification center for a particular notification center type.
        NSDistributedNotificationCenter notificationCenterForType(String notificationCenterType);
    }

    public static NSDistributedNotificationCenter defaultCenter() {
        return CLASS.defaultCenter();
    }

    public static NSDistributedNotificationCenter notificationCenterForType(String notificationCenterType) {
        return CLASS.notificationCenterForType(notificationCenterType);
    }

    abstract void postNotificationName_object_userInfo_deliverImmediately(String notificationName, String notificationSender, NSDictionary userInfo, boolean deliverImmediately);

    public final void postNotification(String notificationName, String notificationSender, NSDictionary userInfo, boolean deliverImmediately) {
        postNotificationName_object_userInfo_deliverImmediately(notificationName, notificationSender, userInfo, deliverImmediately);
    }

    abstract void addObserver_selector_name_object(ID notificationObserver, Selector notificationSelector, String notificationName, String notificationSender);

    public final void addObserver(ID notificationObserver, Selector notificationSelector, String notificationName, String notificationSender) {
        addObserver_selector_name_object(notificationObserver, notificationSelector, notificationName, notificationSender);
    }

    // Registers an object to receive a notification with a specified behavior when notification delivery is suspended.
    abstract void addObserver_selector_name_object_suspensionBehavior(ID notificationObserver, Selector notificationSelector, String notificationName, String notificationSender, int suspendedDeliveryBehavior);

    // Specifies that an object no longer wants to receive certain notificat
    abstract void removeObserver_name_object(ID notificationObserver, String notificationName, String notificationSender);

    public final void removeObserver(ID notificationObserver, String notificationName, String notificationSender) {
        removeObserver_name_object(notificationObserver, notificationName, notificationSender);
    }

    public abstract boolean suspended();

    public abstract void setSuspended(boolean suspended);

    public static interface NSNotificationSuspensionBehavior {
       int NSNotificationSuspensionBehaviorDrop = 1,
       NSNotificationSuspensionBehaviorCoalesce = 2,
       NSNotificationSuspensionBehaviorHold = 3,
       NSNotificationSuspensionBehaviorDeliverImmediately = 4;
    }
}

