package com.twelvemonkeys.spice.osx;

import com.twelvemonkeys.spice.osx.appkit.NSBitmapImageRep;
import com.twelvemonkeys.spice.osx.appkit.NSImage;
import com.twelvemonkeys.spice.osx.foundation.CFDataRef;
import com.twelvemonkeys.spice.osx.foundation.NSData;
import com.twelvemonkeys.spice.osx.quartzcore.CGImageRef;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSSize;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static com.twelvemonkeys.spice.osx.appkit.NSTIFFCompression.NSTIFFCompressionPackBits;
import static com.twelvemonkeys.spice.osx.foundation.Foundation.*;
import static com.twelvemonkeys.spice.osx.quartzcore.QuartzCore.*;

/**
 * Converts common OS X image types like {@code CGImage} and {@code NSImage}
 * to Java {@link BufferedImage}s.
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: OSXImageUtil.java,v 1.0 Mar 9, 2010 3:35:59 PM haraldk Exp$
 */
public final class OSXImageUtil {
    private static final boolean DEBUG = false;

    private OSXImageUtil() {}

    /**
     * Converts a native OS X image to {@link BufferedImage} the fastest possible way.
     * The returned image may be of type {@link BufferedImage#TYPE_CUSTOM}.
     *
     * @param ref a reference to an OS X {@code CGImage}.
     * @return a new {@code BufferedImage}.
     */
    public static BufferedImage toBufferedImage(final CGImageRef ref) {
        long start = DEBUG ? System.currentTimeMillis() : 0l;

        CFDataRef dataRef = CGDataProviderCopyData(CGImageGetDataProvider(ref));
        int length = CFDataGetLength(dataRef);
        ByteBuffer byteBuffer = CFDataGetBytePtr(dataRef).getByteBuffer(0, length);

        int bytesPerRow = CGImageGetBytesPerRow(ref);
        int width = CGImageGetWidth(ref);
        int height = CGImageGetHeight(ref);
        int bitsPerPixel = CGImageGetBitsPerPixel(ref);
        int bytesPerPixel = bitsPerPixel / 8;

//        DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
//        DataBufferByte buffer = new DataBufferByte(length);
//        byteBuffer.get(buffer.getData());

//        int bitmapInfo = CGImageGetBitmapInfo(ref);
//        System.out.printf("bitmapInfo: 0x%08x%n", bitmapInfo);


        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        DataBufferInt buffer = new DataBufferInt(length / bytesPerPixel);
        intBuffer.get(buffer.getData());

        CFRelease(ref);

//        WritableRaster raster = Raster.createInterleavedRaster(buffer, width, height, bytesPerRow, 4, new int[]{0, 1, 2, 3}, new Point());
//        ColorModel cm = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
//        BufferedImage buffered = new BufferedImage(cm, raster, false, null); // Creates a TYPE_CUSTOM image, but layout is same as INT_RGBA / 4BYTE_RGBA...

        // TODO: Consult CGImageGetAlphaInfo to ensure correct layout
        ColorModel cm = new DirectColorModel(
                bitsPerPixel,
                0x000000ff,        // Blue
                0x0000ff00,        // Green
                0x00ff0000,        // Red
                0xff000000         // Alpha
        );

        WritableRaster raster = Raster.createPackedRaster(buffer, width, height, bytesPerRow / bytesPerPixel, ((DirectColorModel) cm).getMasks(), new Point());
        BufferedImage buffered = new BufferedImage(cm, raster, false, null); // Creates a TYPE_CUSTOM image (INT_BGRA)

//        if (buffered.getType() == BufferedImage.TYPE_CUSTOM) {
//            BufferedImage img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height);
//            Graphics2D g = img.createGraphics();
//            try {
//                g.drawImage(buffered, 0, 0, null);
//            }
//            finally {
//                g.dispose();
//            }
//
//            buffered = img;
//        }

        if (DEBUG) {
            System.out.println("image: " + buffered);
            System.out.println("NSImage to BufferedImage conversion time: " + (System.currentTimeMillis() - start) + " ms");
        }

        return buffered;
    }

    public static BufferedImage toBufferedImage(final NSBitmapImageRep rep) {
        long start = DEBUG ? System.currentTimeMillis() : 0l;

        // PackBits seems to be fastest way of converting, especially for large images
        return convertTIFFRepresentation(rep.TIFFRepresentationUsingCompression(NSTIFFCompressionPackBits, 0), start);
    }

    public static BufferedImage toBufferedImage(final NSImage image) {
        return toBufferedImage(image, null);
    }

    public static BufferedImage toBufferedImage(final NSImage image, final Dimension size) {
        long start = DEBUG ? System.currentTimeMillis() : 0l;

        if (size != null) {
            image.setSize(new NSSize(size)); // -- This breaks with later JNA (between 3.2.2 and 3.2.4) releases...
            image.lockFocus();
            image.unlockFocus();
        }

        // PackBits seems to be fastest way of converting, especially for large images
        return convertTIFFRepresentation(image.TIFFRepresentationUsingCompression(NSTIFFCompressionPackBits, 0), start);
    }

    private static BufferedImage convertTIFFRepresentation(final NSData data, final long start) {
        byte[] bytes = new byte[data.length()];
        data.getBytes_length(bytes, bytes.length);

        try {
            BufferedImage buffered = ImageIO.read(new ByteArrayInputStream(bytes)); // Read TIFF data

            if (DEBUG) {
                System.out.println("image: " + buffered);
                System.out.println("NSImage to BufferedImage conversion time: " + (System.currentTimeMillis() - start) + " ms");
            }

            return buffered;
        }
        catch (IOException e) {
            // There's really no I/O here, unless we have a faulty TIFFImageReader
            throw new RuntimeException(e);
        }
    }

    public static NSImage toNSImage(final BufferedImage image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        boolean written = false;
        try {
            written = ImageIO.write(image, "PNG", stream); // Write data as PNG
        }
        catch (IOException ignore) {
        }

        if (!written) {
            return null;
        }

        // TODO: Might be possible to create directly from data bytes

        NSData data = NSData.CLASS.dataWithBytes_length(stream.toByteArray(), stream.size());
        return Rococoa.create("NSImage", NSImage.class).initWithData(data);
    }

    // TODO: toNSImage(final BufferedImage... images) with multiple representations
}
