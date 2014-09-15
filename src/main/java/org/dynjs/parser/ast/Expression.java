package org.dynjs.parser.ast;

import java.util.List;

import org.dynjs.parser.CodeVisitor;
import org.dynjs.parser.SyntaxElement;
import org.dynjs.parser.js.Position;
import org.dynjs.runtime.ExecutionContext;

public interface Expression extends SyntaxElement {
    String dump(String indent);
    
    int getSizeMetric();
    
    List<FunctionDeclaration> getFunctionDeclarations();

    Object interpret(ExecutionContext context);
}
