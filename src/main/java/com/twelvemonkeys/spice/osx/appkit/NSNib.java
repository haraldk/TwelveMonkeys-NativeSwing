package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.ID;
import org.rococoa.NSObject;
import org.rococoa.NSObjectByReference;
import org.rococoa.cocoa.foundation.NSURL;

/**
 * NSNib
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSNib.java,v 1.0 Jan 13, 2009 6:03:48 PM haraldk Exp$
 */
public interface NSNib extends NSObject {
    NSNib initWithContentsOfURL(NSURL url);
//    ID initWithNibNamed_bundle(String name, NSBundle bundle)

    boolean instantiateNibWithOwner_topLevelObjects(ID owner, NSObjectByReference topLevelObjects);
//    * ? instantiateNibWithExternalNameTable:

}
