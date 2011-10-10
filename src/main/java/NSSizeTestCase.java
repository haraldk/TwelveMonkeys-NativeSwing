import com.twelvemonkeys.spice.osx.foundation.NSSize;
import org.junit.Test;
import org.rococoa.Rococoa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 */
public class NSSizeTestCase {
    // NOTE: final field modifier makes JNA treat fields as read-only, meaning they'll never get synced.
    // Removing final from width and height in NSSize fixes the problem.

    @Test
    public void testNSSize() {
        NSSize size = new NSSize(100, -100);
        assertEquals(100, size.width.intValue());
        assertEquals(-100, size.height.intValue());
    }

    @Test
    public void testNSSizeEquals() {
        assertEquals(new NSSize(100, -22), new NSSize(100, -22));
    }

    @Test
    public void testNSSizeNotEquals() {
        assertFalse("different size should not be equal", new NSSize(100, -22).equals(new NSSize(99, 42)));
    }

    @Test
    public void testImageSize() {
        NSImage image = Rococoa.create("NSImage", NSImage.class).initWithSize(new NSSize(10, 20));
        assertEquals(10, image.size().width.intValue());
        assertEquals(20, image.size().height.intValue());
    }

    @Test
    public void testImageSetSize() {
        NSImage image = Rococoa.create("NSImage", NSImage.class).initWithSize(new NSSize(10, 20));

        image.setSize(new NSSize(50, 77));
        assertEquals(50, image.size().width.intValue());
        assertEquals(77, image.size().height.intValue());
    }

    public static abstract class NSImage implements org.rococoa.cocoa.foundation.NSImage {
        public abstract NSImage initWithSize(NSSize size);
        public abstract NSSize size();
        public abstract void setSize(NSSize size);
    }
}
