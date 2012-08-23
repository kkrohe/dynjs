package org.dynjs.runtime.builtins.types;

import org.dynjs.runtime.AbstractNativeFunction;
import org.dynjs.runtime.DynObject;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.GlobalObject;
import org.dynjs.runtime.JSObject;
import org.dynjs.runtime.PrimitiveDynObject;
import org.dynjs.runtime.PropertyDescriptor;
import org.dynjs.runtime.Types;
import org.dynjs.runtime.builtins.types.number.ToFixed;
import org.dynjs.runtime.builtins.types.number.NaN;

public class BuiltinNumber extends AbstractNativeFunction {

    public static final Number POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
    public static final Number NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
    public static final Number NaN = Double.NaN;

    public BuiltinNumber(final GlobalObject globalObject) {
        super(globalObject, true );

        // 15.7.4
        PrimitiveDynObject prototype = new PrimitiveDynObject();
        prototype.setClassName("Number");
        prototype.setPrimitiveValue(0);
        // 15.7.4.1
        prototype.defineOwnProperty(null, "constructor", new PropertyDescriptor() {
            {
                set("Value", BuiltinNumber.this);
            }
        }, false);
        // 15.7.4.2
        prototype.defineOwnProperty(null, "toString", new PropertyDescriptor() {
            {
                set("Value", new AbstractNativeFunction(globalObject) {
                    @Override
                    public Object call(ExecutionContext context, Object self, Object... args) {
                        if (BuiltinNumber.isNumber((DynObject) self)) {
                            return ((PrimitiveDynObject)self).getPrimitiveValue().toString();
                        }
                        return "0";
                    }
                    
                });
            }
        }, false);
        // 15.7.4.3
        prototype.defineOwnProperty(null, "toLocaleString", new PropertyDescriptor() {            
            {
                set("Value", new AbstractNativeFunction(globalObject) {
                    @Override
                    public Object call(ExecutionContext context, Object self, Object... args) {
                        if (BuiltinNumber.isNumber((DynObject) self)) {
                            return ((PrimitiveDynObject)self).getPrimitiveValue().toString();
                        }
                        return "0";
                    }
                });
            }
        }, false);
        // 15.7.4.4
        prototype.defineOwnProperty(null, "valueOf", new PropertyDescriptor() {
            {
                set("Value", new AbstractNativeFunction(globalObject) {
                    @Override
                    public Object call(ExecutionContext context, Object self, Object... args) {
                        if (BuiltinNumber.isNumber((DynObject) self)) {
                            return ((PrimitiveDynObject)self).getPrimitiveValue();
                        }
                        return "TypeError";
                    }
                });
            }
        }, false);
        // 15.7.4.5
        prototype.defineOwnProperty(null, "toFixed", new PropertyDescriptor() {
            {
                set("Value", new ToFixed(globalObject));
            }
        }, false);
        
        setPrototype(prototype);

        final NaN nan = new NaN(globalObject);
        this.defineOwnProperty(null, "NaN", new PropertyDescriptor() {
            {
                set("Value", nan);
            }
        }, false);
        globalObject.defineGlobalProperty("NaN", nan);
        
        final PropertyDescriptor positiveInfinity = PropertyDescriptor.newAccessorPropertyDescriptor(true);
        positiveInfinity.setValue(POSITIVE_INFINITY);
        this.defineOwnProperty(null, "POSITIVE_INFINITY", positiveInfinity, true);

        final PropertyDescriptor negativeInfinity = PropertyDescriptor.newAccessorPropertyDescriptor(true);
        negativeInfinity.setValue(NEGATIVE_INFINITY);
        this.defineOwnProperty(null, "NEGATIVE_INFINITY", negativeInfinity, true);
        
        

    }
    
    public static boolean isNumber(DynObject object) {
        return "Number".equals(object.getClassName());
    }

    @Override
    public Object call(ExecutionContext context, Object self, Object... args) {
        System.err.println( "NUMBER SELF: " + self );
        if (self == Types.UNDEFINED ) {
            // called as a function
            return Types.toNumber(args[0]);
        } else {
            // called as a ctor
            PrimitiveDynObject numberObject = (PrimitiveDynObject) self;
            numberObject.setPrimitiveValue(Types.toNumber(args[0]));
            return numberObject;
        }
    }
    
    @Override
    public JSObject createNewObject() {
        // 15.7.2.1
        PrimitiveDynObject object = new PrimitiveDynObject();
        object.setPrototype(this.getPrototype());
        object.setClassName("Number");
        return object;
    }

}