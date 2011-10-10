package com.twelvemonkeys.spice.osx.foundation;

import com.sun.jna.NativeLibrary;
import com.twelvemonkeys.spice.osx.appkit.NSArrayUtil;
import org.rococoa.ID;
import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSString;

import java.util.List;

/**
* NSLocale
*
* @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
* @author last modified by $Author: haraldk$
* @version $Id: NSLocale.java,v 1.0 08.07.11 16.17 haraldk Exp$
*/
public abstract class NSLocale implements NSObject {
    private static final _Class CLASS = Rococoa.createClass("NSLocale", _Class.class);

    private static interface _Class extends NSClass {
        NSLocale currentLocale();

        NSLocale systemLocale();

        NSArray preferredLanguages();

        NSLocale autoupdatingCurrentLocale();
    }

    private static NSString getFoundationGlobalString(String globalVarName) {
        return Rococoa.wrap(ID.fromLong(NativeLibrary.getInstance("Foundation").getGlobalVariableAddress(globalVarName).getNativeLong(0).longValue()), NSString.class);
    }

    public static NSLocale currentLocale() {
        return CLASS.currentLocale();
    }

    // TODO: Listen to NSCurrentLocaleDidChangeNotification to keep up to date...
    public static NSLocale autoupdatingCurrentLocale() {
        return CLASS.autoupdatingCurrentLocale();
    }

    public static NSLocale systemLocale() {
        return CLASS.systemLocale();
    }

    public abstract String localeIdentifier();

    public abstract String objectForKey(NSString key);

    public static List<String> preferredLanguages() {
        return NSArrayUtil.asList(CLASS.preferredLanguages());
    }

    public static final NSString NSLocaleLanguageCode = getFoundationGlobalString("NSLocaleLanguageCode");
    public static final NSString NSLocaleCountryCode = getFoundationGlobalString("NSLocaleCountryCode");
    public static final NSString NSLocaleVariantCode = getFoundationGlobalString("NSLocaleVariantCode");

}
