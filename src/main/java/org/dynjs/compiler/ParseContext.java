package org.dynjs.compiler;

import org.dynjs.runtime.JSObject;

public interface ParseContext {
    JSObject createSyntaxError(String message);
}
