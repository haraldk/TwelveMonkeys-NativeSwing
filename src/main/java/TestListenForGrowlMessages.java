import com.twelvemonkeys.spice.osx.appkit.NSDistributedNotificationCenter;
import com.twelvemonkeys.spice.osx.appkit.NSNotification;
import org.rococoa.Foundation;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSAutoreleasePool;

import javax.swing.*;

/**
 * TestListenForGrowlMessages
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestListenForGrowlMessages.java,v 1.0 14.01.11 09.34 haraldk Exp$
 */
public class TestListenForGrowlMessages {
    private static NSObject proxy;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NSAutoreleasePool.new_();
                NSDistributedNotificationCenter notificationCenter = NSDistributedNotificationCenter.defaultCenter();
//                NSDistributedNotificationCenter notificationCenter = NSDistributedNotificationCenter.notificationCenterForType("GrowlNotification");

                System.err.println("notificationCenter: " + notificationCenter);

                final Observer observer = new Observer();
                proxy = Rococoa.proxy(observer);

//                notificationCenter.addObserver(proxy.id(), Foundation.selector("message:"), "GrowlNotification", null);
                notificationCenter.addObserver(proxy.id(), Foundation.selector("message:"), null, null);

                new JFrame("Test").setVisible(true);
//                // Sleep without blocking (TODO: Find a better way)
//                new Thread(new Runnable() {
//                    public void run() {
//                        synchronized (observer) {
//                            try {
//                                observer.wait();
//                            }
//                            catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }
//                    }
//                }).start();
            }
        });
    }

    static class Observer {
        public void message(NSNotification notification) {
            // <appname>-<id>-"GrowlClicked!" for click callbacks?
            // <appname>-<id>-"GrowlTimedOut!" otherwise
            String name = notification.name();
            if (name.equals("GrowlNotification")) {
                System.err.println("Notification: " + notification);
            }
            if (name.endsWith("GrowlClicked!")) {
                System.err.println("Clicked: " + notification);
            }
            else if (name.endsWith("GrowlTimedOut!")) {
                System.err.println("Timed out: " + notification);
            }
//            Thread.dumpStack();
        }
    }
}
