package org.dynjs.parser;

import org.dynjs.parser.ast.*;

public interface CodeVisitor<T> {


    Object visit(T context,AdditiveExpression expr, boolean strict);

    Object visit(T context,BitwiseExpression bitwiseExpression, boolean strict);

    Object visit(T context,ArrayLiteralExpression expr, boolean strict);

    Object visit(T context,AssignmentExpression expr, boolean strict);

    Object visit(T context,BitwiseInversionOperatorExpression expr, boolean strict);

    Object visit(T context,BlockStatement statement, boolean strict);

    Object visit(T context,BooleanLiteralExpression expr, boolean strict);

    Object visit(T context,BreakStatement statement, boolean strict);

    Object visit(T context,CaseClause clause, boolean strict);
    
    Object visit(T context,DefaultCaseClause clause, boolean strict);

    Object visit(T context,CatchClause clause, boolean strict);

    Object visit(T context,CompoundAssignmentExpression expr, boolean strict);

    Object visit(T context,ContinueStatement statement, boolean strict);

    Object visit(T context,DeleteOpExpression expr, boolean strict);

    Object visit(T context,DoWhileStatement statement, boolean strict);

    Object visit(T context,EmptyStatement statement, boolean strict);

    Object visit(T context,EqualityOperatorExpression expr, boolean strict);

    Object visit(T context,CommaOperator expr, boolean strict);

    Object visit(T context,ExpressionStatement statement, boolean strict);

    Object visit(T context,FloatingNumberExpression expr, boolean strict);

    Object visit(T context,ForExprInStatement statement, boolean strict);

    Object visit(T context,ForExprOfStatement statement, boolean strict);

    Object visit(T context,ForExprStatement statement, boolean strict);

    Object visit(T context,ForVarDeclInStatement statement, boolean strict);

    Object visit(T context,ForVarDeclOfStatement statement, boolean strict);

    Object visit(T context,ForVarDeclStatement statement, boolean strict);

    Object visit(T context,FunctionCallExpression expr, boolean strict);

    Object visit(T context,FunctionDeclaration statement, boolean strict);

    Object visit(T context,FunctionExpression expr, boolean strict);

    Object visit(T context,IdentifierReferenceExpression expr, boolean strict);

    Object visit(T context,IfStatement statement, boolean strict);

    Object visit(T context,InOperatorExpression expr, boolean strict);

    Object visit(T context,OfOperatorExpression expr, boolean strict);

    Object visit(T context,InstanceofExpression expr, boolean strict);

    Object visit(T context,IntegerNumberExpression expr, boolean strict);

    Object visit(T context,LogicalExpression expr, boolean strict);

    Object visit(T context,LogicalNotOperatorExpression expr, boolean strict);

    Object visit(T context,DotExpression expr, boolean strict);
    
    Object visit(T context,BracketExpression expr, boolean strict);

    Object visit(T context,MultiplicativeExpression expr, boolean strict);

    Object visit(T context,NewOperatorExpression expr, boolean strict);

    Object visit(T context,NullLiteralExpression expr, boolean strict);

    Object visit(T context,ObjectLiteralExpression expr, boolean strict);

    Object visit(T context,PostOpExpression expr, boolean strict);

    Object visit(T context,PreOpExpression expr, boolean strict);

    Object visit(T context,PropertyGet propertyGet, boolean strict);

    Object visit(T context,PropertySet propertySet, boolean strict);
    
    Object visit(T context,NamedValue namedValue, boolean strict);

    Object visit(T context,RegexpLiteralExpression expr, boolean strict);

    Object visit(T context,RelationalExpression expr, boolean strict);

    Object visit(T context,ReturnStatement statement, boolean strict);

    Object visit(T context,StrictEqualityOperatorExpression expr, boolean strict);

    Object visit(T context,StringLiteralExpression expr, boolean strict);

    Object visit(T context,SwitchStatement statement, boolean strict);

    Object visit(T context,TernaryExpression expr, boolean strict);

    Object visit(T context,ThisExpression expr, boolean strict);

    Object visit(T context,ThrowStatement statement, boolean strict);

    Object visit(T context,TryStatement statement, boolean strict);

    Object visit(T context,TypeOfOpExpression expr, boolean strict);

    Object visit(T context,UnaryMinusExpression expr, boolean strict);

    Object visit(T context,UnaryPlusExpression expr, boolean strict);

    Object visit(T context,VariableDeclaration expr, boolean strict);

    Object visit(T context,VariableStatement statement, boolean strict);

    Object visit(T context,VoidOperatorExpression expr, boolean strict);

    Object visit(T context,WhileStatement statement, boolean strict);

    Object visit(T context,WithStatement statement, boolean strict);


}
