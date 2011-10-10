import com.sun.jna.Library;
import com.sun.jna.Native;
import org.junit.Test;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.RunOnMainThread;

import static org.junit.Assert.*;

/**
 * NSViewHiddenTestCase
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSViewHiddenTestCase.java,v 1.0 Feb 9, 2010 9:25:53 PM haraldk Exp$
 */
public class NSViewHiddenTestCase {

//    public static interface RococoaNSViewTest extends Library {
//    }
//
//    public final static RococoaNSViewTest LIBRARY = (RococoaNSViewTest) Native.loadLibrary("RococoaNSViewTest", RococoaNSViewTest.class);

    @RunOnMainThread
    public static interface NSView extends NSObject {
        void setHidden(boolean hidden); // How it should be
        boolean isHidden();
        boolean isHiddenOrHasHiddenAncestor();
        NSView superview();
    }

    @RunOnMainThread
    public static interface NSViewSetHiddenWorkaround extends NSObject {
        void setHidden(int hidden);     // My workaround...
    }

    private NSView createNSView() {
        return Rococoa.create("NSView", NSView.class);
//        LIBRARY.toString();
        
//        return Rococoa.create("MyView", NSView.class);
    }

    private NSView createNonHiddenView() {
        NSView view = createNSView();
        assertFalse("Precondition failed", view.isHidden()); // Sanity check
        return view;
    }

    private NSView createHiddenView() {
        NSView view = createNSView();
        Rococoa.cast(view, NSViewSetHiddenWorkaround.class).setHidden(1);
        assertTrue("Precondition failed", view.isHidden()); // Sanity check
        return view;
    }

    @Test
    public void testSetHiddenFalseOnNonHidden() {
        NSView view = createNonHiddenView();

        view.setHidden(false);
        assertFalse("Should not be hidden", view.isHidden()); // ok
    }

    @Test
    public void testSetHiddenTrueOnNonHidden() {
        NSView view = createNonHiddenView();

        view.setHidden(true);
        assertTrue("Should be hidden", view.isHidden()); // blows up...
    }

    @Test
    public void testSetHiddenFalseOnHidden() {
        NSView view = createHiddenView();

        view.setHidden(false);
        assertFalse("Should not be hidden", view.isHidden()); // ok
    }

    @Test
    public void testSetHiddenTrueOnHidden() {
        NSView view = createHiddenView();

        view.setHidden(true);
        assertTrue("Should be hidden", view.isHidden()); // blows up...
    }

    @Test
    public void testSuperviewIsNull() {
        // This is more like a sanity check, but ok...
       assertNull("View outside view hierarchy should not have a super view", createHiddenView().superview());
    }

    @Test
    public void testIsHiddenOrHasHiddenAncestorAfterSetHiddenTrue() {
        NSView view = createHiddenView();
        assertTrue(view.isHiddenOrHasHiddenAncestor()); // Sanity check

        view.setHidden(true);
        assertTrue("Should be hidden", view.isHiddenOrHasHiddenAncestor());
    }

    @Test
    public void testIsHiddenOrHasHiddenAncestorAfterSetHiddenFalse() {
        NSView view = createHiddenView();
        assertTrue(view.isHiddenOrHasHiddenAncestor()); // Sanity check

        view.setHidden(false);
        assertFalse("Should not be hidden", view.isHiddenOrHasHiddenAncestor());
    }

    @Test
    public void testIsHiddenOrHasHiddenAncestorAfterSetHiddenTrueNonHidden() {
        NSView view = createNonHiddenView();
        assertFalse(view.isHiddenOrHasHiddenAncestor()); // Sanity check

        view.setHidden(true);
        assertTrue("Should be hidden", view.isHiddenOrHasHiddenAncestor());
    }

    @Test
    public void testIsHiddenOrHasHiddenAncestorAfterSetHiddenFalseNonHidden() {
        NSView view = createNonHiddenView();
        assertFalse(view.isHiddenOrHasHiddenAncestor()); // Sanity check

        view.setHidden(false);
        assertFalse("Should not be hidden", view.isHiddenOrHasHiddenAncestor());
    }
}
