package org.dynjs.parser;

import org.dynjs.parser.ast.*;
import org.dynjs.parser.js.Token;

import java.util.Collection;
import java.util.Stack;

public class DefaultCommentVisitor<T> implements CompleteVisitor<T> {

    public static class VisitState<U> {
        public SyntaxElement node;
        public U context;

        public VisitState(U context, SyntaxElement node) {
            this.node = node;
            this.context = context;
        }
    }

    protected Stack<VisitState<T>> astStack = new Stack<VisitState<T>>();


    protected void visitSyntaxElement(T context, SyntaxElement elem, boolean strict) {
        astStack.push(new VisitState(context, elem));
        Collection<Token> comments = elem.getPosition().getComments();
        if(comments != null) {
            for(Token comment : comments) {
                visitComment(context, comment, strict);
            }
        }
        elem.accept(context, this, strict);
        astStack.pop();
    }

    protected void walkBinaryExpression(T context, AbstractBinaryExpression expr, boolean strict) {
        visitSyntaxElement(context, expr.getLhs(), strict);
        visitSyntaxElement(context, expr.getRhs(), strict);
    }

    protected void walkUnaryExpression(T context, AbstractUnaryOperatorExpression expr, boolean strict) {
        visitSyntaxElement(context, expr.getExpr(), strict);
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
            visitSyntaxElement(context, each, strict);
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
            visitSyntaxElement(context, each, strict);
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
        visitSyntaxElement(context, clause.getExpression(), strict);
        visitSyntaxElement(context, clause.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, DefaultCaseClause clause, boolean strict) {
        visitSyntaxElement(context, clause.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, CatchClause clause, boolean strict) {
        visitSyntaxElement(context, clause.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, CompoundAssignmentExpression expr, boolean strict) {
        visitSyntaxElement(context, expr.getRootExpr(), strict);
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
        visitSyntaxElement(context, statement.getTest(), strict);
        visitSyntaxElement(context, statement.getBlock(), strict);
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
        walkBinaryExpression(context, expr, strict);
        return null;
    }

    @Override
    public Object visit(T context, ExpressionStatement statement, boolean strict) {
        visitSyntaxElement(context, statement.getExpr(), strict);
        return null;
    }

    @Override
    public Object visit(T context, FloatingNumberExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, ForExprInStatement statement, boolean strict) {
        visitSyntaxElement(context, statement.getExpr(), strict);
        visitSyntaxElement(context, statement.getRhs(), strict);
        visitSyntaxElement(context, statement.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, ForExprOfStatement statement, boolean strict) {
        visitSyntaxElement(context, statement.getExpr(), strict);
        visitSyntaxElement(context, statement.getRhs(), strict);
        visitSyntaxElement(context, statement.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, ForExprStatement statement, boolean strict) {
        if (statement.getExpr() != null) {
            visitSyntaxElement(context, statement.getExpr(), strict);
        }

        if (statement.getTest() != null) {
            visitSyntaxElement(context, statement.getTest(), strict);
        }

        if (statement.getIncrement() != null) {
            visitSyntaxElement(context, statement.getIncrement(), strict);
        }

        visitSyntaxElement(context, statement.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, ForVarDeclInStatement statement, boolean strict) {
        visitSyntaxElement(context, statement.getDeclaration(), strict);
        visitSyntaxElement(context, statement.getRhs(), strict);
        return null;
    }

    @Override
    public Object visit(T context, ForVarDeclOfStatement statement, boolean strict) {
        visitSyntaxElement(context, statement.getDeclaration(), strict);
        visitSyntaxElement(context, statement.getRhs(), strict);
        return null;
    }

    @Override
    public Object visit(T context, ForVarDeclStatement statement, boolean strict) {
        if (statement.getDeclarationList() != null) {
            for ( VariableDeclaration each : statement.getDeclarationList() ) {
                visitSyntaxElement(context, each, strict);
            }
        }

        if (statement.getTest() != null) {
            visitSyntaxElement(context, statement.getTest(), strict);
        }

        if (statement.getIncrement() != null) {
            visitSyntaxElement(context, statement.getIncrement(), strict);
        }

        visitSyntaxElement(context, statement.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, FunctionCallExpression expr, boolean strict) {
        visitSyntaxElement(context, expr.getMemberExpression(), strict);

        for (Expression each : expr.getArgumentExpressions()) {
            visitSyntaxElement(context, each, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, FunctionDeclaration statement, boolean strict) {
        visitSyntaxElement(context, statement.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, FunctionExpression expr, boolean strict) {
        visitSyntaxElement(context, expr.getDescriptor().getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, IdentifierReferenceExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, IfStatement statement, boolean strict) {
        visitSyntaxElement(context, statement.getTest(), strict);
        visitSyntaxElement(context, statement.getThenBlock(), strict);
        if (statement.getElseBlock() != null) {
            visitSyntaxElement(context, statement.getElseBlock(), strict);
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
        walkBinaryExpression(context, expr, strict);
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
            visitSyntaxElement(context, each, strict);
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
        visitSyntaxElement(context, namedValue.getExpr(), strict);
        return null;
    }

    @Override
    public Object visit(T context, PropertyGet propertyGet, boolean strict) {
        visitSyntaxElement(context, propertyGet.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, PropertySet propertySet, boolean strict) {
        visitSyntaxElement(context, propertySet.getBlock(), strict);
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
            visitSyntaxElement(context, statement.getExpr(), strict);
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
        visitSyntaxElement(context, statement.getExpr(), strict);
        for (CaseClause each : statement.getCaseClauses()) {
            visitSyntaxElement(context, each, strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, TernaryExpression expr, boolean strict) {
        visitSyntaxElement(context, expr.getTest(), strict);
        visitSyntaxElement(context, expr.getThenExpr(), strict);
        visitSyntaxElement(context, expr.getElseExpr(), strict);
        return null;
    }

    @Override
    public Object visit(T context, ThisExpression expr, boolean strict) {
        // no-op
        return null;
    }

    @Override
    public Object visit(T context, ThrowStatement statement, boolean strict) {
        visitSyntaxElement(context, statement.getExpr(), strict);
        return null;
    }

    @Override
    public Object visit(T context, TryStatement statement, boolean strict) {
        visitSyntaxElement(context, statement.getTryBlock(), strict);
        if (statement.getCatchClause() != null) {
            visitSyntaxElement(context, statement.getCatchClause(), strict);
        }
        if (statement.getFinallyBlock() != null) {
            visitSyntaxElement(context, statement.getFinallyBlock(), strict);
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
            visitSyntaxElement(context, expr.getExpr(), strict);
        }
        return null;
    }

    @Override
    public Object visit(T context, VariableStatement statement, boolean strict) {
        for (VariableDeclaration each : statement.getVariableDeclarations()) {
            visitSyntaxElement(context, each, strict);
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
        visitSyntaxElement(context, statement.getTest(), strict);
        visitSyntaxElement(context, statement.getBlock(), strict);
        return null;
    }

    @Override
    public Object visit(T context, WithStatement statement, boolean strict) {
        visitSyntaxElement(context, statement.getExpr(), strict);
        visitSyntaxElement(context, statement.getBlock(), strict);
        return null;
    }

    @Override
    public Object visitComment(T context, Token comment, boolean strict) {
        return null;
    }
}
