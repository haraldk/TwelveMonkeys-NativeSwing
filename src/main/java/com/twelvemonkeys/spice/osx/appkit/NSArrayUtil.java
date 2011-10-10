package com.twelvemonkeys.spice.osx.appkit;

import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * NSArrayUtil
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSArrayUtil.java,v 1.0 Apr 5, 2010 2:33:57 PM haraldk Exp$
 */
public class NSArrayUtil {
    public static NSArray toNSArray(final String... strings) {
        if (strings == null) {
            return null;
        }

        NSObject[] objects = new NSObject[strings.length + 1]; // + 1 for trailing null...

        for (int i = 0; i < strings.length; i++) {
            objects[i] = NSString.stringWithString(strings[i]);
        }

        return NSArray.CLASS.arrayWithObjects(objects);
    }

    public static NSArray toNSArray(final List<String> strings) {
        if (strings == null) {
            return null;
        }

        return toNSArray(strings.toArray(new String[strings.size()]));
    }

    public static List<String> asList(final NSArray array) {
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < array.count(); i++) {
            NSString string = Rococoa.cast(array.objectAtIndex(i), NSString.class);
            list.add(string.toString());
        }

        return Collections.unmodifiableList(list);
    }
}
