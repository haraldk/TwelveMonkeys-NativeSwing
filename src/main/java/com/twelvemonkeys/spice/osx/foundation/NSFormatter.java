package com.twelvemonkeys.spice.osx.foundation;

import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;

/**
* NSFormatter
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: NSFormatter.java,v 1.0 08.07.11 16.14 haraldk Exp$
*/
public abstract class NSFormatter implements NSObject {
    private static final _Class CLASS = Rococoa.createClass("NSFormatter", _Class.class);

    private static interface _Class extends NSClass {
    }

}
