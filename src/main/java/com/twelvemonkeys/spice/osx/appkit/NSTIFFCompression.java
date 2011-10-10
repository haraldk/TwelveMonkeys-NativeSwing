package com.twelvemonkeys.spice.osx.appkit;

/**
* NSTIFFCompression
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: NSTIFFCompression.java,v 1.0 Mar 9, 2010 3:46:10 PM haraldk Exp$
*/
public interface NSTIFFCompression {
    final int NSTIFFCompressionNone = 1;
    final int NSTIFFCompressionCCITTFAX3 = 3;
    final int NSTIFFCompressionCCITTFAX4 = 4;
    final int NSTIFFCompressionLZW = 5;
    final int NSTIFFCompressionJPEG = 6;
    final int NSTIFFCompressionNEXT = 32766;
    final int NSTIFFCompressionPackBits = 32773;
    final int NSTIFFCompressionOldJPEG = 32865;
}
