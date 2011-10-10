package com.twelvemonkeys.spice.osx.appkit;

/**
 * NSSText
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSSText.java,v 1.0 Jan 12, 2009 2:32:21 PM haraldk Exp$
 */
public interface NSText extends NSView {
    String string();
    void setString(String string);

}
