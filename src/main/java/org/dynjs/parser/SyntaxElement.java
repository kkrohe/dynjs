package org.dynjs.parser;

import org.dynjs.parser.js.Position;

public interface SyntaxElement {

    <T> Object accept(T context, CodeVisitor<T> visitor, boolean strict);
    Position getPosition();

}
