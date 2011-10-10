package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.NSObject;
import org.rococoa.RunOnMainThread;

/**
 * NSResponder
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSResponder.java,v 1.0 Mar 22, 2008 8:40:31 PM haraldk Exp$
 */
@RunOnMainThread
public interface NSResponder extends NSObject {
    void setNextResponder(NSResponder responder);    
}
