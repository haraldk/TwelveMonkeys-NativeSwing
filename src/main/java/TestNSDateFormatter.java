import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * TestNSDateFormatter
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: TestNSDateFormatter.java,v 1.0 05.07.11 12.11 haraldk Exp$
 */
public class TestNSDateFormatter {

    public static void main(String[] args) {

//        NSAutoreleasePool pool = NSAutoreleasePool.new_();
//        NSDateFormatter formatter = NSDateFormatter.create();
//        System.err.println("formatter: " + formatter);
//        formatter.setTimeStyle();
//        formatter.setDateStyle();

        Date date = new Date();
//        System.out.println("Date (NS): " + NSDateFormatter.localizedStringFromDate(date, NSDateFormatter.NSDateFormatterStyle.NSDateFormatterLongStyle, NSDateFormatter.NSDateFormatterStyle.NSDateFormatterShortStyle));
//        NSLocale nsLocale = NSLocale.currentLocale();

//        Locale jLocale = new Locale(nsLocale.objectForKey(NSLocale.NSLocaleLanguageCode), nsLocale.objectForKey(NSLocale.NSLocaleCountryCode), nsLocale.objectForKey(NSLocale.NSLocaleVariantCode) != null ? nsLocale.objectForKey(NSLocale.NSLocaleVariantCode) : "");
        // Java Locale does not recognize "nn"/"nb" ?!?!!?
//        Locale jLocale = new Locale("no", nsLocale.objectForKey(NSLocale.NSLocaleCountryCode));
//        Locale jLocale = new Locale("en", "GB");
//        Locale jLocale = new Locale("en", "NO");

//        System.err.println("jLocale: " + jLocale);
//        System.out.println("Date (J1): " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, jLocale).format(date));
        System.out.println("Date (J2): " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, new Locale("xy", "ZY")).format(date));
        System.out.println("Date (JD): " + DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(date));

//        System.err.println("NSLocale.currentLocale(): " + NSLocale.currentLocale().localeIdentifier());
//        System.err.println("Locale.getDefault(): " + Locale.getDefault());
//        System.err.println("System.getProperty(): " + System.getProperty("user.language") + "_" + System.getProperty("user.country"));
//        System.err.println("NSLocale.preferredLanguages(): " + NSLocale.preferredLanguages());
    }
}
