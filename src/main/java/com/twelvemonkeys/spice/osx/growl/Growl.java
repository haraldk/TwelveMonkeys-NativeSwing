package com.twelvemonkeys.spice.osx.growl;

import com.twelvemonkeys.spice.osx.OSXImageUtil;
import com.twelvemonkeys.spice.osx.appkit.NSApplication;
import com.twelvemonkeys.spice.osx.appkit.NSDistributedNotificationCenter;
import org.rococoa.NSObject;
import org.rococoa.cocoa.foundation.*;

import java.awt.image.BufferedImage;
import java.util.*;

/**
 * A class that encapsulates the work of talking to Growl.
 *
 * @author Karl Adam (original code using CocoaJavaBridge)
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a> (port to Rococoa).
 * @author last modified by $Author: haraldk$
 * @version $Id: Growl.java,v 1.0 Mar 26, 2009 12:24:28 PM haraldk Exp$
 */
public final class Growl {
    // TODO: Consider using Map instead of NSDictionary
    // TODO: Support Growl for Windows using JNA?
    // TODO: Make constants private?
    // TODO: Make Growl bring application to front, probably using GrowlApplicationBridge/GrowlApplicationBridgeDelegate

    // defines
    /** The name of the growl registration notification for DNC. */
    public static final String GROWL_APP_REGISTRATION = "GrowlApplicationRegistrationNotification";

    //  Ticket Defines
    /** Ticket key for the application name. */
    public static final String GROWL_APP_NAME = "ApplicationName";
    /** Ticket key for the application icon. */
    public static final String GROWL_APP_ICON = "ApplicationIcon";
    /** Ticket key for the default notifactions. */
    public static final String GROWL_NOTIFICATIONS_DEFAULT = "DefaultNotifications";
    /** Ticket key for all notifactions. */
    public static final String GROWL_NOTIFICATIONS_ALL = "AllNotifications";

    //  Notification Defines
    /** The name of the growl notification for DNC. */
    public static final String GROWL_NOTIFICATION = "GrowlNotification";
    /** Notification key for the name. */
    public static final String GROWL_NOTIFICATION_NAME = "NotificationName";
    /** Notification key for the title. */
    public static final String GROWL_NOTIFICATION_TITLE = "NotificationTitle";
    /** Notification key for the description. */
    public static final String GROWL_NOTIFICATION_DESCRIPTION = "NotificationDescription";
    /** Notification key for the icon. */
    public static final String GROWL_NOTIFICATION_ICON = "NotificationIcon";
    /** Notification key for the application icon. */
    public static final String GROWL_NOTIFICATION_APP_ICON = "NotificationAppIcon";
    /** Notification key for the sticky flag. */
    public static final String GROWL_NOTIFICATION_STICKY = "NotificationSticky";
    /** Notification key for the identifier. */
    public static final String GROWL_NOTIFICATION_IDENTIFIER = "GrowlNotificationIdentifier";

    // Actual instance data
    // We should only register once
    private boolean registered;
    // "Application" Name
    private String appName;
    // "application" Icon
    private NSImage appIcon;
    // All notifications
    private List<String> allNotes;
    // Default enabled notifications
    private List<String> defaultNotes;

    // The notification center
    private final NSDistributedNotificationCenter notificationCenter;

    private static NSArray toNSArray(final List<String> strings) {
        if (strings == null) {
            return null;
        }

        return toNSArray(strings.toArray(new String[strings.size()]));
    }

    private static NSArray toNSArray(final String... strings) {
        if (strings == null) {
            return null;
        }

        NSObject[] types = new NSObject[strings.length];
        for (int i = 0; i < strings.length; i++) {
            types[i] = NSString.stringWithString(strings[i]);

        }

        return NSArray.CLASS.arrayWithObjects(types);
    }

    private static NSDictionary toNSDictionary(final Map<String, ?> map) {
        NSMutableDictionary dict = NSMutableDictionary.dictionaryWithCapacity(map.size());

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            // Wrap Java objects, or cast to NSObject...
            Object value = entry.getValue();
            NSObject obj = value instanceof NSObject ? (NSObject) value : wrapNativeValue(value);
            dict.setValue_forKey(obj, entry.getKey());
        }

        // TODO: Return non-mutable?
        return dict;
    }

    private static NSObject wrapNativeValue(final Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return NSString.stringWithString((String) value);
        }
        // TODO: NSInteger does not extend NSObject... Use ID?
//        if (value instanceof Integer) {
//            return new NSInteger(((Integer) value).longValue());
//        }

        throw new IllegalArgumentException("No converter for native value " + value);
    }

    //************  Constructors **************//
    /**
     * Convenience method to construct a growl instance, defers to Growl(String
     * inAppName, NSData inImageData, NSArray inAllNotes, NSArray inDefNotes,
     * boolean registerNow) with empty arrays for your notifications.
     *
     * @param applicationName The name of your application
     * @param icon     Your application's icon, or {@code null} to use your application's default application icon.
     */
    public Growl(String applicationName, BufferedImage icon) {
        this(applicationName, icon, null, null, false);
    }

    /**
     * Convenience method to construct a growl instance, defers to Growl(String
     * inAppName, NSData inImageData, NSArray inAllNotes, NSArray inDefNotes,
     * boolean registerNow) with the arrays passed here and empty Data for the icon.
     *
     * @param applicationName  The Name of your application
     * @param allNotes  The list of Strings of all your notifications
     * @param defaultNotes  The list of Strings of your default notifications
     */
    public Growl(String applicationName, List<String> allNotes, List<String> defaultNotes) {
        this(applicationName, null, allNotes, defaultNotes, false);
    }

    /**
     * Convenience method to construct a growl instance, defers to Growl(String
     * inAppName, NSData inImageData, NSArray inAllNotes, NSArray inDefNotes,
     * boolean registerNow) with empty arrays for your notifications.
     *
     * @param applicationName   The name of your application
     * @param icon     Your application's icon, or {@code null} to use your application's default application icon.
     * @param allNotes  The list of Strings of all your notifications
     * @param defaultNotes  The list of Strings of your default notifications
     * @param registerNow Since we have all the necessary info we can go ahead
     */
    public Growl(String applicationName, BufferedImage icon, List<String> allNotes, List<String> defaultNotes, boolean registerNow) {
        if (applicationName == null) {
            throw new IllegalArgumentException("Application name may not be null");
        }

        appName = applicationName;
        appIcon = icon != null ? OSXImageUtil.toNSImage(icon) : NSApplication.NSApp.applicationIconImage();

        setAllowedNotifications(allNotes);
        setDefaultNotifications(defaultNotes);

        notificationCenter = NSDistributedNotificationCenter.defaultCenter();

        if (registerNow) {
            register();
        }
    }

    //************  Commonly Used Methods **************//

    // TODO: What's the point of this return value? It's always true...

    /**
     * Register all our notifications with Growl, this should only be called once.
     *
     * @return {@code true}.
     */
    public final boolean register() {
        if (!registered) {
            // Construct our dictionary
            // Make the arrays of objects then keys
            NSArray objects = NSArray.CLASS.arrayWithObjects(
                    NSString.stringWithString(appName),
                    toNSArray(allNotes),
                    toNSArray(defaultNotes),
                    appIcon != null ? appIcon.TIFFRepresentation() : null
            );

            NSArray keys = NSArray.CLASS.arrayWithObjects(
                    NSString.stringWithString(GROWL_APP_NAME),
                    NSString.stringWithString(GROWL_NOTIFICATIONS_ALL),
                    NSString.stringWithString(GROWL_NOTIFICATIONS_DEFAULT),
                    appIcon != null ? NSString.stringWithString(GROWL_APP_ICON) : null
            );

            // Make the Dictionary
            NSDictionary regDict = NSDictionary.dictionaryWithObjects_forKeys(objects, keys);

            notificationCenter.postNotification(
                    GROWL_APP_REGISTRATION, // notificationName
                    null,                   // anObject
                    regDict,                // userInfoDictionary
                    true                    // deliverImmediately
            );

            registered = true;
        }

        return true;
    }

    /**
     * The fun part is actually sending those notifications we worked so hard for
     * so here we let growl know about things we think the user would like, and growl
     * decides if that is the case.
     *
     * @param notificationName The name of one of the notifications we told growl about.
     * @param icon             The the icon for this notification. If {@code null}, the default application icon will be used.
     * @param title            The title of this notification as Growl will show it
     * @param description      The description of our notification as Growl will display it
     * @param extraInfo        Growl is flexible and allows Display Plugins to do as they
     *                           please with their own special keys and values, you may use
     *                           them here. These may be ignored by either the user's
     *                           preferences or the current Display Plugin.
     *                           This parameter may be {@code null}
     * @param sticky           Whether the Growl notification should be sticky
     * @param identifier       Notification identifier for coalescing. May be {@code null}.
     * @throws IllegalArgumentException When a notification is not known
     */
    public void postNotification(String notificationName, BufferedImage icon, String title, String description,
                                 NSDictionary extraInfo, boolean sticky, String identifier) {
        NSMutableDictionary noteDict = NSMutableDictionary.dictionaryWithCapacity(0);

        if (!allNotes.contains(notificationName)) {
            throw new IllegalArgumentException("Undefined Notification attempted: " + notificationName);
        }

        noteDict.setValue_forKey(NSString.stringWithString(notificationName), GROWL_NOTIFICATION_NAME);
        noteDict.setValue_forKey(title != null ? NSString.stringWithString(title) : null, GROWL_NOTIFICATION_TITLE);
        noteDict.setValue_forKey(description != null ? NSString.stringWithString(description) : null, GROWL_NOTIFICATION_DESCRIPTION);
        noteDict.setValue_forKey(NSString.stringWithString(appName), GROWL_APP_NAME);
        if (icon != null) {
            noteDict.setValue_forKey(OSXImageUtil.toNSImage(icon).TIFFRepresentation(), GROWL_NOTIFICATION_ICON);
        }

        if (sticky) {
            noteDict.setValue_forKey(NSNumber.CLASS.numberWithInt(1), GROWL_NOTIFICATION_STICKY);
        }

        if (identifier != null) {
            noteDict.setValue_forKey(NSString.stringWithString(identifier), GROWL_NOTIFICATION_IDENTIFIER);
        }

        if (extraInfo != null) {
            noteDict.addEntriesFromDictionary(extraInfo);
        }

        notificationCenter.postNotification(GROWL_NOTIFICATION, null, noteDict, true);
    }

    /**
     * Convenienve method that defers to postNotificationGrowlOf(String inNotificationName,
     * NSData inIconData, String inTitle, String inDescription,
     * NSDictionary inExtraInfo, boolean inSticky, String inIdentifier) with
     * <code>null</code> passed for icon, extraInfo and identifier arguments
     *
     * @param notificationName The name of one of the notifications we told growl about.
     * @param title            The Title of our Notification as Growl will show it
     * @param description      The Description of our Notification as Growl will
     *                           display it
     * @throws IllegalArgumentException When a notification is not known
     */
    public void postNotification(String notificationName, String title, String description) {
        postNotification(notificationName, null, title, description, null, false, null);
    }

    /**
     * Convenience method that defers to postNotificationGrowlOf(String inNotificationName,
     * NSData inIconData, String inTitle, String inDescription,
     * NSDictionary inExtraInfo, boolean inSticky)
     * with <code>null</code> passed for icon and extraInfo arguments.
     *
     * @param notificationName The name of one of the notifications we told growl about.
     * @param title            The Title of our Notification as Growl will show it
     * @param description      The Description of our Notification as Growl will display it
     * @param sticky           Whether our notification should be sticky
     * @throws IllegalArgumentException When a notification is not known
     */
    public void postNotification(String notificationName, String title, String description, boolean sticky) {
        postNotification(notificationName, null, title, description, null, sticky, null);
    }


    //************  Accessors **************//

    /**
     * Accessor for the currently set "Application" Name
     *
     * @return String Application Name
     */
    public String applicationName() {
        return appName;
    }

    /**
     * Accessor for the list of allowed notifications.
     *
     * @return the list of allowed notifications.
     */
    public List<String> allowedNotifications() {
        return allNotes;
    }

    /**
     * Accessor for the list of default notifications.
     *
     * @return the list of default notifications.
     */
    public List<String> defaultNotifications() {
        return defaultNotes;
    }

    //************  Mutators **************//

    /**
     * Sets the name of the Application talking to growl.
     *
     * @param applicationName The application name
     * @throws IllegalStateException if already registered
     */
    public void setApplicationName(final String applicationName) {
        if (registered) {
            throw new IllegalStateException("Already registered");
        }

        appName = applicationName;
    }

    /**
     * Set the list of allowed Notifications
     *
     * @param inAllNotes The array of allowed Notifications
     * @throws IllegalStateException if already registered
     */
    public void setAllowedNotifications(final List<String> inAllNotes) {
        if (registered) {
            throw new IllegalStateException("Already registered");
        }

        allNotes = Collections.unmodifiableList(new ArrayList<String>(inAllNotes));
    }


    /**
     * Set the list of Default Notfiications
     *
     * @param inDefNotes The default Notifications
     * @throws IllegalArgumentException when an element of the array is not in the
     *                                  allowedNotifications
     * @throws IllegalStateException    if already registered
     */
    public void setDefaultNotifications(final List<String> inDefNotes) {
        if (registered) {
            throw new IllegalStateException("Already registered");
        }

        for (String inDefNote : inDefNotes) {
            if (!allNotes.contains(inDefNote)) {
                // TODO: This check is not done in the constructor
                throw new IllegalArgumentException("Array Element not in Allowed Notifications");
            }
        }

        defaultNotes = Collections.unmodifiableList(new ArrayList<String>(inDefNotes));
    }
}
