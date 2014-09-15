package org.dynjs.parser.ast;

import org.dynjs.parser.CodeVisitor;
import org.dynjs.parser.Statement;
import org.dynjs.parser.js.Position;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;

public class PropertyGet extends PropertyAccessor {

    public PropertyGet(Position position, String name, Statement block) {
        super(position, name, block);
    }

    @Override
    public <T> Object accept(T context, CodeVisitor<T> visitor, boolean strict) {
        return visitor.visit(context, this, strict);
    }

    @Override
    public Object interpret(ExecutionContext context) {
        JSFunction compiledFn = ((ExecutionContext) context).getCompiler().compileFunction((ExecutionContext) context,
                null,
                new String[]{},
                getBlock(),
                context.isStrict());
        return(compiledFn);
    }

}
