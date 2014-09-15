package org.dynjs.parser.ast;

import org.dynjs.parser.CodeVisitor;
import org.dynjs.parser.js.Position;
import org.dynjs.runtime.ExecutionContext;

public class IntegerNumberExpression extends NumberLiteralExpression {
    private long value;

    public IntegerNumberExpression(Position position, String text, int radix, long value) {
        super(position, text, radix);

        this.value = value;
    }

    @Override
    public <T> Object accept(T context, CodeVisitor<T> visitor, boolean strict) {
        return visitor.visit(context, this, strict);
    }

    @Override
    public Object interpret(ExecutionContext context) {
        return getValue();
    }

    public long getValue() {
        return value;
    }
}
