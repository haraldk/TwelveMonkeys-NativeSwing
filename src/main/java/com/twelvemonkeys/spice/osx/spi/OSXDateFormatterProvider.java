package com.twelvemonkeys.spice.osx.spi;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.util.Arrays;
import java.util.Locale;

/**
 * OSXDateFormatterProvider
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: OSXDateFormatterProvider.java,v 1.0 08.07.11 16.01 haraldk Exp$
 */
public class OSXDateFormatterProvider extends DateFormatProvider {

//    private final NSLocale locale = NSLocale.autoupdatingCurrentLocale();

    public OSXDateFormatterProvider() {
        System.err.println("OSXDateFormatterProvider.OSXDateFormatterProvider");
    }

    @Override
    public DateFormat getTimeInstance(int style, Locale locale) {
        System.err.println("OSXDateFormatterProvider.getTimeInstance");
        throw new UnsupportedOperationException("Method getTimeInstance not implemented"); // TODO: Implement
    }

    @Override
    public DateFormat getDateInstance(int style, Locale locale) {
        System.err.println("OSXDateFormatterProvider.getDateInstance");
        throw new UnsupportedOperationException("Method getDateInstance not implemented"); // TODO: Implement
    }

    @Override
    public DateFormat getDateTimeInstance(final int dateStyle, final int timeStyle, Locale locale) {
        System.err.println("OSXDateFormatterProvider.getDateTimeInstance");

        return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

//        return new DateFormat() {
//            @Override
//            public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
//                System.err.println("OSXDateFormatterProvider.format");
//                Thread.dumpStack();
//
//                return toAppendTo.append(NSDateFormatter.localizedStringFromDate(date, convertStyle(dateStyle), convertStyle(timeStyle)));
//            }
//
//            private int convertStyle(final int style) {
//                switch (style) {
//                    case DateFormat.SHORT:
//                        return NSDateFormatter.NSDateFormatterStyle.NSDateFormatterShortStyle;
//                    case DateFormat.MEDIUM:
//                        return NSDateFormatter.NSDateFormatterStyle.NSDateFormatterMediumStyle;
//                    case DateFormat.LONG:
//                        return NSDateFormatter.NSDateFormatterStyle.NSDateFormatterLongStyle;
//                    case DateFormat.FULL:
//                        return NSDateFormatter.NSDateFormatterStyle.NSDateFormatterFullStyle;
//                    default:
//                        throw new IllegalArgumentException(String.format("No such style: %d", style));
//                }
//            }
//
//            @Override
//            public Date parse(String source, ParsePosition pos) {
//                throw new UnsupportedOperationException("Method parse not implemented"); // TODO: Implement
//            }
//        };
    }

    @Override
    public Locale[] getAvailableLocales() {
        System.err.println("OSXDateFormatterProvider.getAvailableLocales");
        System.err.println("Locale.getAvailableLocales(): " + Arrays.toString(Locale.getAvailableLocales()));

//        return new Locale[] { new Locale("en", "US")};
        return new Locale[] { new Locale("xy", "ZY"), Locale.getDefault() };
//        return Locale.getAvailableLocales();
    }
}
