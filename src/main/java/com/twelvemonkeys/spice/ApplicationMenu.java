package com.twelvemonkeys.spice;

import com.twelvemonkeys.spice.osx.appkit.NSMenuItem;
import org.rococoa.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ApplicationMenu
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: ApplicationMenu.java,v 1.0 Jan 14, 2010 10:56:55 PM haraldk Exp$
 */
public final class ApplicationMenu {
    // TODO: When done, validate that we have no resource leaks...

    // TODO: Should probably be thread group local, not static, but who cares... :-P
    private static final JMenu INSTANCE = createDefault();

    public static JMenu getMenu() {
        return INSTANCE;
    }

    private static JMenu createDefault() {
        // TODO: Use SPI...
        if (System.getProperty("os.name").startsWith("Mac OS X")) {
            // Special care
            return new OSXAppMenuProxy();
        }

        return null;
    }

    public static class MenuItemActionProxy /*implements NSMenuValidation*/ {
        private final JMenuItem mItem;

        MenuItemActionProxy(final JMenuItem pItem) {
            mItem = pItem;
        }

        @SuppressWarnings({"UnusedDeclaration"}) // Invoked via Reflection
        public void actionPerformed(final ID id) {
            final Action action = mItem.getAction();
            final ActionListener[] listeners = mItem.getActionListeners();
            if (listeners != null && listeners.length > 0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // TODO: Modifiers?
                        ActionEvent event = new ActionEvent(
                                mItem,
                                ActionEvent.ACTION_PERFORMED,
                                (String) action.getValue(Action.ACTION_COMMAND_KEY),
                                System.currentTimeMillis(),
                                0
                        );

                        for (ActionListener listener : listeners) {
                            listener.actionPerformed(event);
                        }
                    }
                });
            }
        }

        public boolean validateMenuItem(final NSMenuItem item) {
            System.out.println("ApplicationMenu$MenuItemActionProxy.validateMenuItem: " + item);
            return true;
        }
    }

}
