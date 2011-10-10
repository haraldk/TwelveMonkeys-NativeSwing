package com.twelvemonkeys.spice.osx.foundation;

import org.rococoa.NSClass;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSDate;

import java.util.Date;

/**
* NSDateFormatter
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: NSDateFormatter.java,v 1.0 08.07.11 16.15 haraldk Exp$
*/
public abstract class NSDateFormatter extends NSFormatter {
    private static final _Class CLASS = Rococoa.createClass("NSDateFormatter", _Class.class);

    private static interface _Class extends NSClass {
        NSDateFormatter alloc();

        String localizedStringFromDate_dateStyle_timeStyle(NSDate date, int dateStyle, int timeStyle);
    }

    static NSDateFormatter create() {
        return CLASS.alloc().init();
    }

    protected abstract NSDateFormatter init();

    public static String localizedStringFromDate(Date date, int dateStyle, int timeStyle) {
        return CLASS.localizedStringFromDate_dateStyle_timeStyle(NSDate.CLASS.dateWithTimeIntervalSince1970(date.getTime() / 1000), dateStyle, timeStyle);
    }

    public static interface NSDateFormatterStyle {
        final int NSDateFormatterNoStyle = 0,
                NSDateFormatterShortStyle = 1,
                NSDateFormatterMediumStyle = 2,
                NSDateFormatterLongStyle = 3,
                NSDateFormatterFullStyle = 4;
    }
}
