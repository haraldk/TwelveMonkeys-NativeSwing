/*
 * Copyright 2007, 2008 Duncan McGregor
 * 
 * This file is part of Rococoa, a library to allow Java to talk to Cocoa.
 * 
 * Rococoa is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Rococoa is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Rococoa.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.rococoa.internal;


import com.sun.jna.*;
import org.rococoa.NSObject;

/**
 * A JNA TypeMapper that knows how to convert :
 * <ul>
 *   <li>{@link org.rococoa.NSObject} to and from an integer type with the right size to be an id.</li>
 *   <li>{@link String} to and from an integer type with the right size to be an id.</li>
 * </ul>
 *
 * Note that nativeType is never NativeLong, but the appropriate Java primitive
 * with the right size of NativeLong.
 *
 * TypeMappers are consulted by JNA to know how to convert between Java values
 * and objects and native values.
 *
 * @author duncan
 *
 */
// TODO: Remove when upgrading to a Rococoa version that incorporates this fix
public class RococoaTypeMapper extends DefaultTypeMapper {
    private static final NSObjectTypeConverter<NSObject> nsObjectConverter = new NSObjectTypeConverter<NSObject>(NSObject.class);
    private static final BoolConverter boolConverter = new BoolConverter();
    private static final StringTypeConverter stringConverter = new StringTypeConverter();

    public RococoaTypeMapper() {
        addToNativeConverter(NSObject.class, nsObjectConverter);
        addToNativeConverter(Boolean.class, boolConverter);
        addFromNativeConverter(Boolean.class, boolConverter);
        addTypeConverter(String.class, stringConverter);
        // addToNativeConverter(NSObjectByReference.class, new ObjectByReferenceConverter());
        // not actually used at present because NSObjectInvocationHandler does marshalling
    }

    @SuppressWarnings("unchecked")
    @Override
    public FromNativeConverter getFromNativeConverter(Class javaType) {
        if (NSObject.class.isAssignableFrom(javaType)) {
            // return a new converter that knows the subtype it is going to create
            return new NSObjectTypeConverter((Class<NSObject>)javaType);
        }
        return super.getFromNativeConverter(javaType);
    }

    private static final class BoolConverter implements ToNativeConverter, FromNativeConverter {
        public Object toNative(final Object value, final ToNativeContext context) {
            return value == null ? 0 : (((Boolean) value) ? 1 : 0);
        }

        @Override
        public Object fromNative(Object value, FromNativeContext fromNativeContext) {
            return ((Byte) value).intValue() == 1;
        }

        public Class nativeType() {
            return Byte.class;
        }
    }
}