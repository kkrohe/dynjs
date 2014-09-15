package org.dynjs.parser.ast;

import org.dynjs.parser.CodeVisitor;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.Types;
import org.dynjs.runtime.linker.DynJSBootstrapper;

import java.lang.invoke.CallSite;

public class UnaryMinusExpression extends AbstractUnaryOperatorExpression {

    private final CallSite get;

    public UnaryMinusExpression(Expression expr) {
        super(expr, "-");
        this.get = DynJSBootstrapper.factory().createGet( expr.getPosition() );
    }

    public String toString() {
        return "-" + getExpr();
    }

    @Override
    public <T> Object accept(T context, CodeVisitor<T> visitor, boolean strict) {
        return visitor.visit(context, this, strict);
    }

    @Override
    public Object interpret(ExecutionContext context) {
        Object value = getValue(this.get, context, getExpr().interpret(context));
        Number oldValue = Types.toNumber(context, value);
        if (oldValue instanceof Double) {
            if (Double.isNaN(oldValue.doubleValue())) {
                return(Double.NaN);
            } else {
                return(-1 * oldValue.doubleValue());
            }
        } else if (oldValue.longValue() == 0L) {
            return(-0.0);
        } else {
            return(-1 * oldValue.longValue());
        }
    }
}
