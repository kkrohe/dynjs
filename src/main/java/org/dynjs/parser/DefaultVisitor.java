package org.dynjs.parser;

import org.dynjs.parser.ast.*;

public class DefaultVisitor<T> implements CodeVisitor<T> {

    protected void walkBinaryExpression(T context, AbstractBinaryExpression expr, boolean strict) {
        expr.getLhs().accept(context, this, strict);
        expr.getRhs().accept(context, this, strict);
    }

    protected void walkUnaryExpression(T context, AbstractUnaryOperatorExpression expr, boolean strict) {
        expr.getExpr().accept(context, this, strict);

    }

    @Override
    public Object visit(T context, AdditiveExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, BitwiseExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, ArrayLiteralExpression expr, boolean strict) {
        for (Expression each : expr.getExprs()) {
            each.accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, AssignmentExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, BitwiseInversionOperatorExpression expr, boolean strict) {
        walkUnaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, BlockStatement statement, boolean strict) {
        for (Statement each : statement.getBlockContent()) {
            each.accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, BooleanLiteralExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, BreakStatement statement, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, CaseClause clause, boolean strict) {
        clause.getExpression().accept(context, this, strict);
        clause.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, DefaultCaseClause clause, boolean strict) {
        clause.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, CatchClause clause, boolean strict) {
        clause.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, CompoundAssignmentExpression expr, boolean strict) {
        expr.getRootExpr().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, ContinueStatement statement, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, DeleteOpExpression expr, boolean strict) {
        walkUnaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, DoWhileStatement statement, boolean strict) {
        statement.getTest().accept(context, this, strict);
        statement.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, EmptyStatement statement, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, EqualityOperatorExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, CommaOperator expr, boolean strict) {
        expr.getLhs().accept( context, this, strict );
        expr.getRhs().accept( context, this, strict );
        return null;
    }

    @Override
    public Object visit(T context, ExpressionStatement statement, boolean strict) {
        statement.getExpr().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, FloatingNumberExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, ForExprInStatement statement, boolean strict) {
        statement.getExpr().accept(context, this, strict);
        statement.getRhs().accept(context, this, strict);
        statement.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, ForExprOfStatement statement, boolean strict) {
        statement.getExpr().accept(context, this, strict);
        statement.getRhs().accept(context, this, strict);
        statement.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, ForExprStatement statement, boolean strict) {
        if (statement.getExpr() != null) {
            statement.getExpr().accept(context, this, strict);
        }

        if (statement.getTest() != null) {
            statement.getTest().accept(context, this, strict);
        }

        if (statement.getIncrement() != null) {
            statement.getIncrement().accept(context, this, strict);
        }

        statement.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, ForVarDeclInStatement statement, boolean strict) {
        statement.getDeclaration().accept(context, this, strict);
        statement.getRhs().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, ForVarDeclOfStatement statement, boolean strict) {
        statement.getDeclaration().accept(context, this, strict);
        statement.getRhs().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, ForVarDeclStatement statement, boolean strict) {
        if (statement.getDeclarationList() != null) {
            for ( VariableDeclaration each : statement.getDeclarationList() ) {
                each.accept(context, this, strict);
            }
        }

        if (statement.getTest() != null) {
            statement.getTest().accept(context, this, strict);
        }

        if (statement.getIncrement() != null) {
            statement.getIncrement().accept(context, this, strict);
        }

        statement.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, FunctionCallExpression expr, boolean strict) {
        expr.getMemberExpression().accept(context, this, strict);

        for (Expression each : expr.getArgumentExpressions()) {
            each.accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, FunctionDeclaration statement, boolean strict) {
        statement.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, FunctionExpression expr, boolean strict) {
        expr.getDescriptor().getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, IdentifierReferenceExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, IfStatement statement, boolean strict) {
        statement.getTest().accept(context, this, strict);
        statement.getThenBlock().accept(context, this, strict);
        if (statement.getElseBlock() != null) {
            statement.getElseBlock().accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, InOperatorExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, OfOperatorExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, InstanceofExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, IntegerNumberExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, LogicalExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, LogicalNotOperatorExpression expr, boolean strict) {
        expr.getExpr().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, DotExpression expr, boolean strict) {
        expr.getLhs().accept( context, this, strict );
        return null;
    }
    
    @Override
    public Object visit(T context, BracketExpression expr, boolean strict) {
        expr.getLhs().accept( context, this, strict );
        expr.getRhs().accept( context, this, strict );
        return null;
    }

    @Override
    public Object visit(T context, MultiplicativeExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, NewOperatorExpression expr, boolean strict) {
        walkUnaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, NullLiteralExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, ObjectLiteralExpression expr, boolean strict) {
        for (PropertyAssignment each : expr.getPropertyAssignments()) {
            each.accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, PostOpExpression expr, boolean strict) {
        walkUnaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, PreOpExpression expr, boolean strict) {
        walkUnaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, NamedValue namedValue, boolean strict) {
        namedValue.getExpr().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, PropertyGet propertyGet, boolean strict) {
        propertyGet.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, PropertySet propertySet, boolean strict) {
        propertySet.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, RegexpLiteralExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, RelationalExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, ReturnStatement statement, boolean strict) {
        if (statement.getExpr() != null) {
            statement.getExpr().accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, StrictEqualityOperatorExpression expr, boolean strict) {
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, StringLiteralExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, SwitchStatement statement, boolean strict) {
        statement.getExpr().accept(context, this, strict);
        for (CaseClause each : statement.getCaseClauses()) {
            each.accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, TernaryExpression expr, boolean strict) {
        expr.getTest().accept(context, this, strict);
        expr.getThenExpr().accept(context, this, strict);
        expr.getElseExpr().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, ThisExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, ThrowStatement statement, boolean strict) {
        statement.getExpr().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, TryStatement statement, boolean strict) {
        statement.getTryBlock().accept(context, this, strict);
        if (statement.getCatchClause() != null) {
            statement.getCatchClause().accept(context, this, strict);
        }
        if (statement.getFinallyBlock() != null) {
            statement.getFinallyBlock().accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, TypeOfOpExpression expr, boolean strict) {
        walkUnaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, UnaryMinusExpression expr, boolean strict) {
        walkUnaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, UnaryPlusExpression expr, boolean strict) {
        walkUnaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, VariableDeclaration expr, boolean strict) {
        if (expr.getExpr() != null) {
            expr.getExpr().accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, VariableStatement statement, boolean strict) {
        for (VariableDeclaration each : statement.getVariableDeclarations()) {
            each.accept(context, this, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, VoidOperatorExpression expr, boolean strict) {
        walkUnaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, WhileStatement statement, boolean strict) {
        statement.getTest().accept(context, this, strict);
        statement.getBlock().accept(context, this, strict);
        return null;
    }

    @Override
    public Object visit(T context, WithStatement statement, boolean strict) {
        statement.getExpr().accept(context, this, strict);
        statement.getBlock().accept(context, this, strict);
        return null;
    }

}
