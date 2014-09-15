package org.dynjs.parser;

import org.dynjs.parser.js.Token;

public interface CommentVisitor<T> {

    Object visitComment(T context, Token comment, boolean strict);

}
