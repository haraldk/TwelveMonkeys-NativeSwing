package com.twelvemonkeys.spice.osx.quicklook;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.twelvemonkeys.spice.osx.OSXImageUtil;
import com.twelvemonkeys.spice.osx.appkit.NSWindow;
import com.twelvemonkeys.spice.osx.foundation.*;
import com.twelvemonkeys.spice.osx.quartzcore.CGImageRef;
import org.rococoa.*;
import org.rococoa.cocoa.foundation.NSSize;
import org.rococoa.cocoa.foundation.NSURL;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * QuickLook
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: QuickLook.java,v 1.0 Mar 2, 2010 10:13:51 PM haraldk Exp$
 */
public final class QuickLook {

    private static final QLLibrary INSTANCE = (QLLibrary) Native.loadLibrary("QuickLook", QLLibrary.class);

    private static interface QLLibrary extends Library {
        // QL_EXPORT CGImageRef QLThumbnailImageCreate(CFAllocatorRef allocator, CFURLRef url, CGSize maxThumbnailSize, CFDictionaryRef options);
        // NSURL, NSSize and NSDictionary should be interchangeable here
        CGImageRef QLThumbnailImageCreate(CFAllocatorRef allocator, CFURLRef fileURL, NSSize size, CFDictionaryRef dict);
    }

    public static BufferedImage createThumbnail(final File file, final Dimension size) {
        CGImageRef ref = QuickLook.INSTANCE.QLThumbnailImageCreate(
                CFAllocatorRef.DEFAULT,
                new CFURLRef(NSURL.CLASS.fileURLWithPath(file.getAbsolutePath())),
                new NSSize(size),
                null
        );

        if (ref == null) {
            return null;
        }

        return OSXImageUtil.toBufferedImage(ref);
    }

    // TODO: Method to set QL controller for window?!
    static NSObject controller;

    // TODO: Remove this method, and instead document how to use QLPreviewPanel
    // OS X 10.6+ only.
    public static void showQuickLookPanel(final Window frame, final File... files) {
        // TODO: Keep reference to controller to avoid GC
        // TODO: Remove panel to avoid hanging apps (the release below helps, but is it kosher?)
        // TODO: Avoid replacing the window delegate, create wrapper, maybe using delegation..
        // TODO: Create controller per window?
        NSWindow window = NSWindow.CLASS.windowFromAWT(frame);
        if (controller == null) {
            controller = Rococoa.proxy(new QLPreviewController(files));
            System.out.println("Created controller: " + controller);
        }

        ID oldDelegate = window.delegate();
        System.out.println("window.delegate(): " + oldDelegate);

        // QLPreviewPanel.h: "You generally implement these methods in your window controller or delegate."
        window.setDelegate(controller.id());

        QLPreviewPanel panel = QLPreviewPanel.sharedPreviewPanel();
        panel.makeKeyAndOrderFront(null);

//        window.setDelegate(oldDelegate);
//        panel.release();
    }

    public static class QLPreviewController {
        private final File[] files;

        NSObject source = Rococoa.proxy(new Object() {
            public int numberOfPreviewItemsInPreviewPanel(QLPreviewPanel panel) {
                return files.length;
            }

            public ID previewPanel_previewItemAtIndex(QLPreviewPanel panel, int index) {
//                System.out.println("QuickLook$QLPreviewController.previewPanel_previewItemAtIndex: " + index);
                return NSURL.CLASS.fileURLWithPath(files[index].getAbsolutePath()).id();
            }
        });

        public QLPreviewController(final File... files) {
            this.files = files;
        }

        public boolean acceptsPreviewPanelControl(QLPreviewPanel panel) {
            return true;
        }

        public void beginPreviewPanelControl(QLPreviewPanel panel) {
            panel.retain();
            panel.setDataSource(source.id());
        }

        public void endPreviewPanelControl(QLPreviewPanel panel) {
            panel.setDataSource(null);
            panel.release();
        }
    }

    // TODO: This implementation is much slower (150-180%) than getting the entire byte array first and using ByteArrayIS.
    // However, it does use less resources for large NSData objects.
    private static class NSDataInputStream extends InputStream {
        private final byte[] buf = new byte[1024];
        private final NSData data;
        private int pos;

        private NSRange range = new NSRange();

        public NSDataInputStream(final NSData data) {
            this.data = data;
        }

        @Override
        public int read() throws IOException {
            int len = read(buf, pos, 1);
            if (len < 0) {
                return len;
            }

            pos++;

            return buf[0] & 0xFF;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (off < 0 || b.length < (off + len)) {
                throw new IndexOutOfBoundsException();
            }

            if (pos >= data.length()) {
                return -1; // EOF
            }

            int length = Math.min(data.length() - pos, len);

            if (off == 0) {
                // Easy variant
                range.location.setValue(pos);
                range.length.setValue(length);
                data.getBytes_range(b, range);
                pos += length;

                return length;
            }
            else {
                // Source offset, heavy variant
                length = Math.min(buf.length, length);
                range.location.setValue(pos);
                range.length.setValue(length);
                data.getBytes_range(buf, range);
                System.arraycopy(buf, 0, b, off, length);
                pos += length;

                return length;
            }
        }
    }
}
