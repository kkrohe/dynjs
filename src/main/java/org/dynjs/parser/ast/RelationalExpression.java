package org.dynjs.parser.ast;

import org.dynjs.parser.CodeVisitor;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.Types;
import org.dynjs.runtime.linker.DynJSBootstrapper;

import java.lang.invoke.CallSite;

public class RelationalExpression extends AbstractBinaryExpression {

    public RelationalExpression(Expression lhs, Expression rhs, String op) {
        super(lhs, rhs, op);
    }

    @Override
    public <T> Object accept(T context, CodeVisitor<T> visitor, boolean strict) {
        return visitor.visit(context, this, strict);
    }

    @Override
    public Object interpret(ExecutionContext context) {
        Object lval = getValue(this.lhsGet, context, getLhs().interpret(context));
        Object rval = getValue(this.rhsGet, context, getRhs().interpret(context));
        Object r = null;

        switch (getOp()) {
            case "<":
                r = Types.compareRelational(context, lval, rval, true);
                if (r == Types.UNDEFINED) {
                    return(false);
                } else {
                    return(r);
                }

            case ">":
                r = Types.compareRelational(context, rval, lval, false);
                if (r == Types.UNDEFINED) {
                    return(false);
                } else {
                    return(r);
                }

            case "<=":
                r = Types.compareRelational(context, rval, lval, false);
                if (r == Boolean.TRUE || r == Types.UNDEFINED) {
                    return(false);
                } else {
                    return(true);
                }

            case ">=":
                r = Types.compareRelational(context, lval, rval, true);
                if (r == Boolean.TRUE || r == Types.UNDEFINED) {
                    return(false);
                } else {
                    return(true);
                }
        }

        return null; // not reached

    }
}
