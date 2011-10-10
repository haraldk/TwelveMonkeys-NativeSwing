package com.twelvemonkeys.spice.osx.appkit;

import com.twelvemonkeys.spice.osx.foundation.NSData;
import org.rococoa.NSClass;
import org.rococoa.NSObject;
import org.rococoa.Rococoa;
import org.rococoa.cocoa.foundation.NSArray;
import org.rococoa.cocoa.foundation.NSString;

import java.util.List;

/**
 * NSPasteboard
 *
 * @author <a href="mailto:harald.kuhr@gmail.com">Harald Kuhr</a>
 * @author last modified by $Author: haraldk$
 * @version $Id: NSPasteboard.java,v 1.0 Apr 2, 2010 6:28:09 PM haraldk Exp$
 */
public abstract class NSPasteboard implements NSObject {
    private static final _Class CLASS = Rococoa.createClass("NSPasteboard", _Class.class);

//    @RunOnMainThread
    private static interface _Class extends NSClass {
        NSPasteboard generalPasteboard();
        NSPasteboard pasteboardWithName(String name);
        NSArray typesFilterableTo(String type);

    }

    static public NSPasteboard generalPasteboard() {
        return CLASS.generalPasteboard();
    }

    static public NSPasteboard pasteboardWithName(final String name) {
        return CLASS.pasteboardWithName(name);
    }

    static public List<String> typesFilterableTo(String type) {
        return NSArrayUtil.asList(CLASS.typesFilterableTo(type));
    }

    public abstract String name();

    abstract NSArray types_(); // TODO: Hackish...

    public final List<String> types() {
        return NSArrayUtil.asList(types_());
    }

    public abstract NSArray pasteboardItems();

    abstract String availableTypeFromArray(NSArray types);

    public final String availableTypeFromArray(String... types) {
        return availableTypeFromArray(NSArrayUtil.toNSArray(types));
    }

    public abstract String stringForType(String dataType);

    public abstract NSData dataForType(String dataType);

    public final static String NSGeneralPboard = NSString.getGlobalString("NSGeneralPboard").toString();
    public final static String NSFontPboard = NSString.getGlobalString("NSFontPboard").toString();
    public final static String NSRulerPboard = NSString.getGlobalString("NSFontPboard").toString();
    public final static String NSFindPboard = NSString.getGlobalString("NSFindPboard").toString();
    public final static String NSDragPboard = NSString.getGlobalString("NSDragPboard").toString();    
}
