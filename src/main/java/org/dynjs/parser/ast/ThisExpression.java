package org.dynjs.parser.ast;

import org.dynjs.parser.CodeVisitor;
import org.dynjs.parser.js.Position;
import org.dynjs.runtime.ExecutionContext;

/**
 * The <code>this</code> keyword evaluates to the value of the ThisBinding of
 * the current execution context.
 * 
 * see 11.1.1
 * @author Douglas Campos
 * @author Bob McWhirter
 * 
 */
public class ThisExpression extends BaseExpression implements IllegalFunctionMemberExpression {

    public ThisExpression(Position position) {
        super(position);
    }

    public String toString() {
        return "this";
    }
    
    public int getSizeMetric() {
        return 3;
    }

    @Override
    public <T> Object accept(T context, CodeVisitor<T> visitor, boolean strict) {
        return visitor.visit(context, this, strict);
    }

    public Object interpret(ExecutionContext context) {
        return context.getThisBinding();
    }
}
