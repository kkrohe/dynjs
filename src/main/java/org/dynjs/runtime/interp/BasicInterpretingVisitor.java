package org.dynjs.runtime.interp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.dynjs.exception.ThrowException;
import org.dynjs.parser.CodeVisitor;
import org.dynjs.parser.Statement;
import org.dynjs.parser.ast.AdditiveExpression;
import org.dynjs.parser.ast.ArrayLiteralExpression;
import org.dynjs.parser.ast.AssignmentExpression;
import org.dynjs.parser.ast.BitwiseExpression;
import org.dynjs.parser.ast.BitwiseInversionOperatorExpression;
import org.dynjs.parser.ast.BlockStatement;
import org.dynjs.parser.ast.BooleanLiteralExpression;
import org.dynjs.parser.ast.BracketExpression;
import org.dynjs.parser.ast.BreakStatement;
import org.dynjs.parser.ast.CaseClause;
import org.dynjs.parser.ast.CatchClause;
import org.dynjs.parser.ast.CommaOperator;
import org.dynjs.parser.ast.CompoundAssignmentExpression;
import org.dynjs.parser.ast.ContinueStatement;
import org.dynjs.parser.ast.DefaultCaseClause;
import org.dynjs.parser.ast.DeleteOpExpression;
import org.dynjs.parser.ast.DoWhileStatement;
import org.dynjs.parser.ast.DotExpression;
import org.dynjs.parser.ast.EmptyStatement;
import org.dynjs.parser.ast.EqualityOperatorExpression;
import org.dynjs.parser.ast.Expression;
import org.dynjs.parser.ast.ExpressionStatement;
import org.dynjs.parser.ast.FloatingNumberExpression;
import org.dynjs.parser.ast.ForExprInStatement;
import org.dynjs.parser.ast.ForExprOfStatement;
import org.dynjs.parser.ast.ForExprStatement;
import org.dynjs.parser.ast.ForVarDeclInStatement;
import org.dynjs.parser.ast.ForVarDeclOfStatement;
import org.dynjs.parser.ast.ForVarDeclStatement;
import org.dynjs.parser.ast.FunctionCallExpression;
import org.dynjs.parser.ast.FunctionDeclaration;
import org.dynjs.parser.ast.FunctionExpression;
import org.dynjs.parser.ast.IdentifierReferenceExpression;
import org.dynjs.parser.ast.IfStatement;
import org.dynjs.parser.ast.InOperatorExpression;
import org.dynjs.parser.ast.OfOperatorExpression;
import org.dynjs.parser.ast.InstanceofExpression;
import org.dynjs.parser.ast.IntegerNumberExpression;
import org.dynjs.parser.ast.LogicalExpression;
import org.dynjs.parser.ast.LogicalNotOperatorExpression;
import org.dynjs.parser.ast.MultiplicativeExpression;
import org.dynjs.parser.ast.NamedValue;
import org.dynjs.parser.ast.NewOperatorExpression;
import org.dynjs.parser.ast.NullLiteralExpression;
import org.dynjs.parser.ast.NumberLiteralExpression;
import org.dynjs.parser.ast.ObjectLiteralExpression;
import org.dynjs.parser.ast.PostOpExpression;
import org.dynjs.parser.ast.PreOpExpression;
import org.dynjs.parser.ast.ProgramTree;
import org.dynjs.parser.ast.PropertyAssignment;
import org.dynjs.parser.ast.PropertyGet;
import org.dynjs.parser.ast.PropertySet;
import org.dynjs.parser.ast.RegexpLiteralExpression;
import org.dynjs.parser.ast.RelationalExpression;
import org.dynjs.parser.ast.ReturnStatement;
import org.dynjs.parser.ast.StrictEqualityOperatorExpression;
import org.dynjs.parser.ast.StringLiteralExpression;
import org.dynjs.parser.ast.SwitchStatement;
import org.dynjs.parser.ast.TernaryExpression;
import org.dynjs.parser.ast.ThisExpression;
import org.dynjs.parser.ast.ThrowStatement;
import org.dynjs.parser.ast.TryStatement;
import org.dynjs.parser.ast.TypeOfOpExpression;
import org.dynjs.parser.ast.UnaryMinusExpression;
import org.dynjs.parser.ast.UnaryPlusExpression;
import org.dynjs.parser.ast.VariableDeclaration;
import org.dynjs.parser.ast.VariableStatement;
import org.dynjs.parser.ast.VoidOperatorExpression;
import org.dynjs.parser.ast.WhileStatement;
import org.dynjs.parser.ast.WithStatement;
import org.dynjs.parser.js.Position;
import org.dynjs.runtime.BasicBlock;
import org.dynjs.runtime.BlockManager;
import org.dynjs.runtime.BlockManager.Entry;
import org.dynjs.runtime.Completion;
import org.dynjs.runtime.DynArray;
import org.dynjs.runtime.DynObject;
import org.dynjs.runtime.EnvironmentRecord;
import org.dynjs.runtime.ExecutionContext;
import org.dynjs.runtime.JSFunction;
import org.dynjs.runtime.JSObject;
import org.dynjs.runtime.PropertyDescriptor;
import org.dynjs.runtime.Reference;
import org.dynjs.runtime.Types;
import org.dynjs.runtime.builtins.types.BuiltinArray;
import org.dynjs.runtime.builtins.types.BuiltinNumber;
import org.dynjs.runtime.builtins.types.BuiltinObject;
import org.dynjs.runtime.builtins.types.BuiltinRegExp;

public class BasicInterpretingVisitor implements CodeVisitor<ExecutionContext> {

    private BlockManager blockManager;

    public BasicInterpretingVisitor(BlockManager blockManager) {
        this.blockManager = blockManager;
    }

    @Override
    public Object visit(ExecutionContext context, AdditiveExpression expr, boolean strict) {
        if (expr.getOp().equals("+")) {
            return visitPlus(context, expr, strict);
        } else {
            return visitMinus(context, expr, strict);
        }
    }

    public Object visitPlus(ExecutionContext context1, AdditiveExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = Types.toPrimitive(context,
                getValue(context, expr.getLhs().accept(context, this, strict)));
        Object rhs = Types.toPrimitive(context,
                getValue(context, expr.getRhs().accept(context, this, strict)));

        if (lhs instanceof String || rhs instanceof String) {
            return(Types.toString(context, lhs) + Types.toString(context, rhs));
            
        }

        Number lhsNum = Types.toNumber(context, lhs);
        Number rhsNum = Types.toNumber(context, rhs);

        if (Double.isNaN(lhsNum.doubleValue()) || Double.isNaN(rhsNum.doubleValue())) {
            return(Double.NaN);
            
        }

        if (lhsNum instanceof Double || rhsNum instanceof Double) {
            if (lhsNum.doubleValue() == 0.0 && rhsNum.doubleValue() == 0.0) {
                if (Double.compare(lhsNum.doubleValue(), 0.0) < 0 && Double.compare(rhsNum.doubleValue(), 0.0) < 0) {
                    return(-0.0);
                    
                } else {
                    return(0.0);
                    
                }
            }
            return(lhsNum.doubleValue() + rhsNum.doubleValue());
            
        }

        return(lhsNum.longValue() + rhsNum.longValue());
    }

    public Object visitMinus(ExecutionContext context1, AdditiveExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Number lhs = Types.toNumber(context, getValue(context,
                expr.getLhs().accept(context, this, strict)));
        Number rhs = Types.toNumber(context,
                getValue(context, expr.getRhs().accept(context, this, strict)));

        if (Double.isNaN(lhs.doubleValue()) || Double.isNaN(rhs.doubleValue())) {
            return(Double.NaN);
            
        }

        if (lhs instanceof Double || rhs instanceof Double) {
            if (lhs.doubleValue() == 0.0 && rhs.doubleValue() == 0.0) {
                if (Double.compare(lhs.doubleValue(), 0.0) < 0 && Double.compare(rhs.doubleValue(), 0.0) < 0) {
                    return(+0.0);
                    
                }

            }
            return(lhs.doubleValue() - rhs.doubleValue());
            
        }

        return(lhs.longValue() - rhs.longValue());
    }

    @Override
    public Object visit(ExecutionContext context1, BitwiseExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = getValue(context, expr.getLhs().accept(context, this, strict));

        Long lhsNum = null;

        if (expr.getOp().equals(">>>")) {
            lhsNum = Types.toUint32(context, lhs);
        } else {
            lhsNum = Types.toInt32(context, lhs);
        }

        Object value = expr.getRhs().accept(context, this, strict);

        if (expr.getOp().equals("<<")) {
            // 11.7.1
            Long rhsNum = Types.toUint32(context, getValue(context, value));
            int shiftCount = rhsNum.intValue() & 0x1F;
            return((int) (lhsNum.longValue() << shiftCount));
        } else if (expr.getOp().equals(">>")) {
            // 11.7.2
            Long rhsNum = Types.toUint32(context, getValue(context, value));
            int shiftCount = rhsNum.intValue() & 0x1F;
            return((int) (lhsNum.longValue() >> shiftCount));
        } else if (expr.getOp().equals(">>>")) {
            // 11.7.3
            Long rhsNum = Types.toUint32(context, getValue(context, value));
            int shiftCount = rhsNum.intValue() & 0x1F;
            return(lhsNum.longValue() >>> shiftCount);
        } else if (expr.getOp().equals("&")) {
            Long rhsNum = Types.toInt32(context, getValue(context, value));
            return(lhsNum.longValue() & rhsNum.longValue());
        } else if (expr.getOp().equals("|")) {
            Long rhsNum = Types.toInt32(context, getValue(context, value));
            return(lhsNum.longValue() | rhsNum.longValue());
        } else if (expr.getOp().equals("^")) {
            Long rhsNum = Types.toInt32(context, getValue(context, value));
            return(lhsNum.longValue() ^ rhsNum.longValue());
        }

        return null; // not reached
    }

    @Override
    public Object visit(ExecutionContext context1, ArrayLiteralExpression expr, boolean strict) {
        ExecutionContext context = context1;
        DynArray array = BuiltinArray.newArray(context);

        int i = 0;
        for (Expression each : expr.getExprs()) {
            Object value = null;
            if (each != null) {
                value = getValue(context, each.accept(context, this, strict));
                array.defineOwnProperty(context, "" + i, PropertyDescriptor.newPropertyDescriptorForObjectInitializer(value), false);
            }
            ++i;
        }
        array.put(context, "length", (long) i, true);

        return(array);
    }

    @Override
    public Object visit(ExecutionContext context1, AssignmentExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = expr.getLhs().accept(context, this, strict);
        if (!(lhs instanceof Reference)) {
            throw new ThrowException(context, context.createReferenceError(expr.getLhs() + " is not a reference"));
        }

        Reference lhsRef = (Reference) lhs;
        Object rhs = getValue(context, expr.getRhs().accept(context, this, strict));

        lhsRef.putValue(context, rhs);
        return(rhs);
    }

    @Override
    public Object visit(ExecutionContext context1, BitwiseInversionOperatorExpression expr, boolean strict) {
        ExecutionContext context = context1;
        return(~Types.toInt32(context, getValue(context, expr.getExpr().accept(context, this, strict))));
    }

    @Override
    public Object visit(ExecutionContext context1, BlockStatement statement, boolean strict) {
        ExecutionContext context = context1;
        List<Statement> content = statement.getBlockContent();

        Object completionValue = Types.UNDEFINED;

        for (Statement each : content) {
            Position position = each.getPosition();
            if (position != null) {
                context.setLineNumber(position.getLine());
            }


            Completion completion = (Completion) each.accept(context, this, strict);
            if (completion.type == Completion.Type.NORMAL) {
                completionValue = completion.value;
                continue;
            }
            if (completion.type == Completion.Type.CONTINUE) {
                return(completion);
                
            }
            if (completion.type == Completion.Type.RETURN) {
                return(completion);
                
            }
            if (completion.type == Completion.Type.BREAK) {
                completion.value = completionValue;
                if (completion.target != null && statement.getLabels().contains(completion.target)) {
                    return(Completion.createNormal(completionValue));
                } else {
                    return(completion);
                }
                
            }
        }

        return(Completion.createNormal(completionValue));
    }

    @Override
    public Object visit(ExecutionContext context1, BooleanLiteralExpression expr, boolean strict) {
        return(expr.getValue());
    }

    @Override
    public Object visit(ExecutionContext context, BreakStatement statement, boolean strict) {
        return(Completion.createBreak(statement.getTarget()));
    }

    @Override
    public Object visit(ExecutionContext context, CaseClause clause, boolean strict) {
        // not used, handled by switch-statement
        return null;
    }

    @Override
    public Object visit(ExecutionContext context, DefaultCaseClause clause, boolean strict) {
        // not used, handled by switch-statement
        return null;
    }

    @Override
    public Object visit(ExecutionContext context, CatchClause clause, boolean strict) {
        // not used, handled by try-statement
        return null;
    }

    @Override
    public Object visit(ExecutionContext context1, CompoundAssignmentExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object r = expr.getRootExpr().accept(context, this, strict);
        Object lref = expr.getRootExpr().getLhs().accept(context, this, strict);

        if (lref instanceof Reference) {
            if (((Reference) lref).isStrictReference()) {
                if (((Reference) lref).getBase() instanceof EnvironmentRecord) {
                    if (((Reference) lref).getReferencedName().equals("arguments") || ((Reference) lref).getReferencedName().equals("eval")) {
                        throw new ThrowException(context, context.createSyntaxError("invalid assignment: " + ((Reference) lref).getReferencedName()));
                    }
                }
            }

            ((Reference) lref).putValue(context, r);
            return(r);
            
        }

        throw new ThrowException(context, context.createReferenceError("cannot assign to non-reference"));
    }

    @Override
    public Object visit(ExecutionContext context, ContinueStatement statement, boolean strict) {
        return(Completion.createContinue(statement.getTarget()));
    }

    @Override
    public Object visit(ExecutionContext context1, DeleteOpExpression expr, boolean strict) {
        ExecutionContext context = context1;

        Object result = expr.getExpr().accept(context, this, strict);
        if (!(result instanceof Reference)) {
            return(true);
            
        }

        Reference ref = (Reference) result;
        if (ref.isUnresolvableReference()) {
            if (strict) {
                throw new ThrowException(context, context.createSyntaxError("cannot delete unresolvable reference"));
            } else {
                return(true);
                
            }
        }

        if (ref.isPropertyReference()) {
            return(Types.toObject(context, ref.getBase()).delete(context, ref.getReferencedName(), ref.isStrictReference()));
            
        }

        if (ref.isStrictReference()) {
            throw new ThrowException(context, context.createSyntaxError("cannot delete from environment record binding"));
        }

        EnvironmentRecord bindings = (EnvironmentRecord) ref.getBase();

        return(bindings.deleteBinding(context, ref.getReferencedName()));
    }

    @Override
    public Object visit(ExecutionContext context1, DoWhileStatement statement, boolean strict) {
        ExecutionContext context = context1;
        Expression testExpr = statement.getTest();
        Statement block = statement.getBlock();

        Object v = null;

        while (true) {
            Completion completion = invokeCompiledBlockStatement(context, "DoWhile", block);
            if (completion.value != null) {
                v = completion.value;
            }
            if (completion.type == Completion.Type.CONTINUE) {
                if (completion.target == null) {
                    // nothing
                } else if (!statement.getLabels().contains(completion.target)) {
                    return(completion);
                    
                }
            } else if (completion.type == Completion.Type.BREAK) {
                if (completion.target == null) {
                    break;
                } else if (!statement.getLabels().contains(completion.target)) {
                    return(completion);
                    
                } else {
                    break;
                }
            } else if (completion.type == Completion.Type.RETURN) {
                return(Completion.createReturn(v));
                
            }


            Boolean testResult = Types.toBoolean(getValue(context, testExpr.accept(context, this, strict)));
            if (!testResult) {
                break;
            }
        }

        return(Completion.createNormal(v));
    }

    @Override
    public Object visit(ExecutionContext context, EmptyStatement statement, boolean strict) {
        return(Completion.createNormal());
    }

    @Override
    public Object visit(ExecutionContext context1, EqualityOperatorExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = getValue(context, expr.getLhs().accept(context, this, strict));
        Object rhs = getValue(context, expr.getRhs().accept(context, this, strict));

        if (expr.getOp().equals("==")) {
            return(Types.compareEquality(context, lhs, rhs));
        } else {
            return(!Types.compareEquality(context, lhs, rhs));
        }
    }

    @Override
    public Object visit(ExecutionContext context1, CommaOperator expr, boolean strict) {
        ExecutionContext context = context1;
        getValue(context, expr.getLhs().accept(context, this, strict));
        return(getValue(context, expr.getRhs().accept(context, this, strict)));
        // leave RHS on the stack
    }

    @Override
    public Object visit(ExecutionContext context1, ExpressionStatement statement, boolean strict) {
        ExecutionContext context = context1;
        Expression expr = statement.getExpr();
        if (expr instanceof FunctionDeclaration) {
            return(Completion.createNormal());
        } else {
            return(Completion.createNormal(getValue(context, expr.accept(context, this, strict))));
        }
    }

    @Override
    public Object visit(ExecutionContext context1, FloatingNumberExpression expr, boolean strict) {
        return(expr.getValue());
    }

    @Override
    public Object visit(ExecutionContext context1, ForExprInStatement statement, boolean strict) {
        ExecutionContext context = context1;
        Object exprRef = statement.getRhs().accept(context, this, strict);
        Object exprValue = getValue(context, exprRef);

        if (exprValue == Types.NULL || exprValue == Types.UNDEFINED) {
            return(Completion.createNormal());
            
        }

        JSObject obj = Types.toObject(context, exprValue);

        Object v = null;

        List<String> names = obj.getAllEnumerablePropertyNames().toList();

        for (String each : names) {

            Object lhsRef = statement.getExpr().accept(context, this, strict);

            if (lhsRef instanceof Reference) {
                ((Reference) lhsRef).putValue(context, each);
            }


            Completion completion = (Completion) statement.getBlock().accept(context, this, strict);
            //Completion completion = invokeCompiledBlockStatement(context, "ForIn", statement.getBlock());

            if (completion.value != null) {
                v = completion.value;
            }

            if (completion.type == Completion.Type.BREAK) {
                if (completion.target == null || statement.getLabels().contains(completion.target)) {
                    return(Completion.createNormal(v));
                } else {
                    return(completion);
                }
                
            }

            if (completion.type == Completion.Type.RETURN || completion.type == Completion.Type.BREAK) {
                return(completion);
                
            }
        }

        return(Completion.createNormal(v));
    }

    @Override
    public Object visit(ExecutionContext context1, ForExprOfStatement statement, boolean strict) {
        ExecutionContext context = context1;
        Object exprRef = statement.getRhs().accept(context, this, strict);
        Object exprValue = getValue(context, exprRef);

        if (exprValue == Types.NULL || exprValue == Types.UNDEFINED) {
            return(Completion.createNormal());
            
        }

        JSObject obj = Types.toObject(context, exprValue);

        Object v = null;

        List<String> names = obj.getAllEnumerablePropertyNames().toList();

        for (String each : names) {
            Object lhsRef = statement.getExpr().accept(context, this, strict);

            if (lhsRef instanceof Reference) {
                Reference propertyRef = context.createPropertyReference(obj, each);
                ((Reference) lhsRef).putValue(context, propertyRef.getValue(context));
            }


            Completion completion = (Completion) statement.getBlock().accept(context, this, strict);
            //Completion completion = invokeCompiledBlockStatement(context, "ForOf", statement.getBlock());

            if (completion.value != null) {
                v = completion.value;
            }

            if (completion.type == Completion.Type.BREAK) {
                if (completion.target == null || statement.getLabels().contains(completion.target)) {
                    return(Completion.createNormal(v));
                } else {
                    return(completion);
                }
                
            }

            if (completion.type == Completion.Type.RETURN || completion.type == Completion.Type.BREAK) {
                return(completion);
                
            }
        }

        return(Completion.createNormal(v));
    }

    @Override
    public Object visit(ExecutionContext context1, ForExprStatement statement, boolean strict) {
        ExecutionContext context = context1;
        if (statement.getExpr() != null) {
            statement.getExpr().accept(context, this, strict);
        }

        Expression test = statement.getTest();
        Expression incr = statement.getIncrement();
        Statement body = statement.getBlock();

        Object v = null;

        while (true) {
            if (test != null) {

                if (!Types.toBoolean(getValue(context, test.accept(context, this, strict)))) {
                    break;
                }
            }

            Completion completion = (Completion) body.accept(context, this, strict);
            //Completion completion = invokeCompiledBlockStatement(context, "ForExpr", body);

            if (completion.value != null && completion.value != Types.UNDEFINED) {
                v = completion.value;
            }

            if (completion.type == Completion.Type.BREAK) {
                if (completion.target == null || statement.getLabels().contains(completion.target)) {
                    return(Completion.createNormal(v));
                } else {
                    completion.value = v;
                    return(completion);
                }
                
            }
            if (completion.type == Completion.Type.RETURN) {
                return(completion);
                
            }
            if (completion.type == Completion.Type.CONTINUE) {
                if (completion.target != null && !statement.getLabels().contains(completion.target)) {
                    return(completion);
                    
                }
            }

            if (incr != null) {
                getValue(context, incr.accept(context, this, strict));
            }
        }

        return(Completion.createNormal(v));
    }

    @Override
    public Object visit(ExecutionContext context1, ForVarDeclInStatement statement, boolean strict) {
        ExecutionContext context = context1;
        String varName = (String) statement.getDeclaration().accept(context, this, strict);
        Object exprRef = statement.getRhs().accept(context, this, strict);
        Object exprValue = getValue(context, exprRef);

        if (exprValue == Types.NULL || exprValue == Types.UNDEFINED) {
            return(Completion.createNormal());
            
        }

        JSObject obj = Types.toObject(context, exprValue);

        Object v = null;

        List<String> names = obj.getAllEnumerablePropertyNames().toList();

        for (String each : names) {
            Reference varRef = context.resolve(varName);

            varRef.putValue(context, each);

            Completion completion = (Completion) statement.getBlock().accept(context, this, strict);
            //Completion completion = invokeCompiledBlockStatement(context, "ForVarDeclsIn", statement.getBlock());

            if (completion.value != null) {
                v = completion.value;
            }

            if (completion.type == Completion.Type.BREAK) {
                if (completion.target == null || statement.getLabels().contains(completion.target)) {
                    return(Completion.createNormal(v));
                } else {
                    return(completion);
                }
                
            }

            if (completion.type == Completion.Type.RETURN || completion.type == Completion.Type.BREAK) {
                return(completion);
                
            }
        }

        return(Completion.createNormal(v));

    }

    @Override
    public Object visit(ExecutionContext context1, ForVarDeclOfStatement statement, boolean strict) {
        ExecutionContext context = context1;
        String varName = (String) statement.getDeclaration().accept(context, this, strict);
        Object exprRef = statement.getRhs().accept(context, this, strict);
        Object exprValue = getValue(context, exprRef);

        if (exprValue == Types.NULL || exprValue == Types.UNDEFINED) {
            return(Completion.createNormal());
            
        }

        JSObject obj = Types.toObject(context, exprValue);

        Object v = null;

        List<String> names = obj.getAllEnumerablePropertyNames().toList();

        for (String each : names) {
            Reference varRef = context.resolve(varName);
            Reference propertyRef = context.createPropertyReference(obj, each);

            varRef.putValue(context, propertyRef.getValue(context));

            Completion completion = (Completion) statement.getBlock().accept(context, this, strict);
            //Completion completion = invokeCompiledBlockStatement(context, "ForVarDeclsOf", statement.getBlock());

            if (completion.value != null) {
                v = completion.value;
            }

            if (completion.type == Completion.Type.BREAK) {
                if (completion.target == null || statement.getLabels().contains(completion.target)) {
                    return(Completion.createNormal(v));
                } else {
                    return(completion);
                }
                
            }

            if (completion.type == Completion.Type.RETURN || completion.type == Completion.Type.BREAK) {
                return(completion);
                
            }
        }

        return(Completion.createNormal(v));

    }

    @Override
    public Object visit(ExecutionContext context1, ForVarDeclStatement statement, boolean strict) {
        ExecutionContext context = context1;
        List<VariableDeclaration> decls = statement.getDeclarationList();
        for (VariableDeclaration each : decls) {
            each.accept(context, this, strict);
        }

        Expression test = statement.getTest();
        Expression incr = statement.getIncrement();
        Statement body = statement.getBlock();

        Object v = null;

        while (true) {
            if (test != null) {
                if (!Types.toBoolean(getValue(context, test.accept(context, this, strict)))) {
                    break;
                }
            }

            Completion completion = (Completion) body.accept(context, this, strict);
            //Completion completion = invokeCompiledBlockStatement(context, "ForVarDecl", body);

            if (completion.value != null && completion.value != Types.UNDEFINED) {
                v = completion.value;
            }

            if (completion.type == Completion.Type.BREAK) {
                if (completion.target == null || statement.getLabels().contains(completion.target)) {
                    return(Completion.createNormal(v));
                } else {
                    completion.value = v;
                    return(completion);
                }
                
            }
            if (completion.type == Completion.Type.RETURN) {
                return(completion);
                
            }
            if (completion.type == Completion.Type.CONTINUE) {
                if (completion.target != null && !statement.getLabels().contains(completion.target)) {
                    return(completion);
                    
                }
            }

            if (incr != null) {
                getValue(context, incr.accept(context, this, strict));
            }
        }

        return(Completion.createNormal(v));
    }

    @Override
    public Object visit(ExecutionContext context1, FunctionCallExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object ref = expr.getMemberExpression().accept(context, this, strict);
        Object function = getValue(context, ref);
        List<Expression> argExprs = expr.getArgumentExpressions();
        Object[] args = new Object[argExprs.size()];
        int i = 0;

        for (Expression each : argExprs) {

            args[i] = getValue(context, each.accept(context, this, strict));
            ++i;
        }

        if (!(function instanceof JSFunction)) {
            throw new ThrowException(context, context.createTypeError(expr.getMemberExpression() + " is not calllable"));
        }

        Object thisValue = null;

        if (ref instanceof Reference) {
            if (((Reference) ref).isPropertyReference()) {
                thisValue = ((Reference) ref).getBase();
            } else {
                thisValue = ((EnvironmentRecord) ((Reference) ref).getBase()).implicitThisValue();
            }
        }

        return(context.call(ref, (JSFunction) function, thisValue, args));
    }

    @Override
    public Object visit(ExecutionContext context, FunctionDeclaration statement, boolean strict) {
        return(Completion.createNormal());
    }

    @Override
    public Object visit(ExecutionContext context, FunctionExpression expr, boolean strict) {
        JSFunction compiledFn = ((ExecutionContext) context).getCompiler().compileFunction((ExecutionContext) context,
                expr.getDescriptor().getIdentifier(),
                expr.getDescriptor().getFormalParameterNames(),
                expr.getDescriptor().getBlock(),
                expr.getDescriptor().isStrict() || strict);
        return(compiledFn);
    }

    @Override
    public Object visit(ExecutionContext context, IdentifierReferenceExpression expr, boolean strict) {
        return(((ExecutionContext) context).resolve(expr.getIdentifier()));
    }

    @Override
    public Object visit(ExecutionContext context1, IfStatement statement, boolean strict) {
        ExecutionContext context = context1;
        Boolean result = Types.toBoolean(getValue(context,
                statement.getTest().accept(context, this, strict)));

        if (result) {
            return(invokeCompiledBlockStatement(context, "Then", statement.getThenBlock()));
        } else if (statement.getElseBlock() != null) {
            return(invokeCompiledBlockStatement(context, "Else", statement.getElseBlock()));
        } else {
            return(Completion.createNormal());
        }
    }

    @Override
    public Object visit(ExecutionContext context1, InOperatorExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = getValue(context, expr.getLhs().accept(context, this, strict));
        Object rhs = getValue(context, expr.getRhs().accept(context, this, strict));

        if (!(rhs instanceof JSObject)) {
            throw new ThrowException(context, context.createTypeError(expr.getRhs() + " is not an object"));
        }

        return(((JSObject) rhs).hasProperty(context, Types.toString(context, lhs)));
    }

    @Override
    public Object visit(ExecutionContext context1, OfOperatorExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = getValue(context, expr.getLhs().accept(context, this, strict));
        Object rhs = getValue(context, expr.getRhs().accept(context, this, strict));

        if (!(rhs instanceof JSObject)) {
            throw new ThrowException(context, context.createTypeError(expr.getRhs() + " is not an object"));
        }

        return(((JSObject) rhs).hasProperty(context, Types.toString(context, lhs)));
    }

    @Override
    public Object visit(ExecutionContext context1, InstanceofExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = getValue(context, expr.getLhs().accept(context, this, strict));
        Object rhs = getValue(context, expr.getRhs().accept(context, this, strict));

        if (rhs == Types.UNDEFINED) {
            throw new ThrowException(context, context.createTypeError(expr.getRhs() + " is undefined."));
        }
        if (rhs instanceof JSObject) {
            if (!(rhs instanceof JSFunction)) {
                throw new ThrowException(context, context.createTypeError(expr.getRhs() + " is not a function"));
            }
            return(((JSFunction) rhs).hasInstance(context, lhs));
        } else if (rhs instanceof Class) {
            Class clazz = (Class) rhs;
            return(lhs.getClass().getName().equals(clazz.getName()));
        }

        return null; // not reached
    }

    @Override
    public Object visit(ExecutionContext context1, IntegerNumberExpression expr, boolean strict) {
        return(expr.getValue());
    }

    @Override
    public Object visit(ExecutionContext context1, LogicalExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = getValue(context, expr.getLhs().accept(context, this, strict));

        if ((expr.getOp().equals("||") && Types.toBoolean(lhs)) || (expr.getOp().equals("&&") && !Types.toBoolean(lhs))) {
            return(lhs);
        } else {
            return expr.getRhs().accept(context, this, strict);
        }
    }

    @Override
    public Object visit(ExecutionContext context1, LogicalNotOperatorExpression expr, boolean strict) {
        ExecutionContext context = context1;
        return(!Types.toBoolean(getValue(context, expr.getExpr().accept(context, this, strict))));
    }

    @Override
    public Object visit(ExecutionContext context1, DotExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object baseRef = expr.getLhs().accept(context, this, strict);
        Object baseValue = getValue(context, baseRef);

        String propertyName = expr.getIdentifier();

        Types.checkObjectCoercible(context, baseValue, propertyName);

        return(context.createPropertyReference(baseValue, propertyName));
    }

    @Override
    public Object visit(ExecutionContext context1, BracketExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object baseRef = expr.getLhs().accept(context, this, strict);
        Object baseValue = getValue(context, baseRef);
        Object identifier = getValue(context, expr.getRhs().accept(context, this, strict));

        Types.checkObjectCoercible(context, baseValue);

        String propertyName = Types.toString(context, identifier);

        return(context.createPropertyReference(baseValue, propertyName));
    }

    @Override
    public Object visit(ExecutionContext context1, MultiplicativeExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Number lval = Types.toNumber(context, getValue(context, expr.getLhs().accept(context, this, strict)));
        Number rval = Types.toNumber(context, getValue(context, expr.getRhs().accept(context, this, strict)));

        if (Double.isNaN(lval.doubleValue()) || Double.isNaN(rval.doubleValue())) {
            return(Double.NaN);
            
        }

        if (lval instanceof Double || rval instanceof Double) {
            switch (expr.getOp()) {
            case "*":
                return(lval.doubleValue() * rval.doubleValue());
                
            case "/":
                // Divide-by-zero
                if (isZero(rval)) {
                    if (isZero(lval)) {
                        return(Double.NaN);
                        
                    } else if (isSameSign(lval, rval)) {
                        return(Double.POSITIVE_INFINITY);
                        
                    } else {
                        return(Double.NEGATIVE_INFINITY);
                    }

                // Zero-divided-by-something
                } else if (isZero(lval)) {
                    if (isSameSign(lval, rval)) {
                        return(0L);
                    } else {
                        return(-0.0);
                    }
                }

                // Regular math
                double primaryValue = lval.doubleValue() / rval.doubleValue();
                if (isRepresentableByLong(primaryValue)) {
                    return((long) primaryValue);
                } else {
                    return(primaryValue);
                }
                
            case "%":
                if (rval.doubleValue() == 0.0) {
                    return(Double.NaN);
                    
                }
                return(BuiltinNumber.modulo(lval, rval));
                
            }
        } else {
            switch (expr.getOp()) {
            case "*":
                return(lval.longValue() * rval.longValue());
                
            case "/":
                if (rval.longValue() == 0L) {
                    if (lval.longValue() == 0L) {
                        return(Double.NaN);
                        
                    } else if (isSameSign(lval, rval)) {
                        return(Double.POSITIVE_INFINITY);
                        
                    } else {
                        return(Double.NEGATIVE_INFINITY);
                    }
                }

                if (lval.longValue() == 0) {
                    if (Double.compare(rval.doubleValue(), 0.0) > 0) {
                        return(0L);
                        
                    } else {
                        return(-0.0);
                        
                    }
                }
                double primaryResult = lval.doubleValue() / rval.longValue();
                if (primaryResult == (long) primaryResult) {
                    return((long) primaryResult);
                } else {
                    return(primaryResult);
                }
                
            case "%":
                if (rval.longValue() == 0L) {
                    return(Double.NaN);
                    
                }
                
                return(BuiltinNumber.modulo(lval, rval));
            }
        }

        return null; // not reached
    }

    @Override
    public Object visit(ExecutionContext context1, NewOperatorExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object ref = expr.getExpr().accept(context, this, strict);
        Object memberExpr = getValue(context, ref);
        Object[] args = new Object[expr.getArgumentExpressions().size()];

        int i = 0;

        for (Expression each : expr.getArgumentExpressions()) {
            args[i] = getValue(context, each.accept(context, this, strict));
            ++i;
        }

        if (memberExpr instanceof JSFunction) {
            return(context.construct(ref, (JSFunction) memberExpr, args));
        }

        throw new ThrowException(context, context.createTypeError("can only construct using functions"));
    }

    @Override
    public Object visit(ExecutionContext context, NullLiteralExpression expr, boolean strict) {
        return(Types.NULL);
    }

    @Override
    public Object visit(ExecutionContext context1, ObjectLiteralExpression expr, boolean strict) {
        ExecutionContext context = context1;
        DynObject obj = BuiltinObject.newObject(context);

        List<PropertyAssignment> assignments = expr.getPropertyAssignments();

        for (PropertyAssignment each : assignments) {
            Object ref = each.accept(context, this, strict);
            String debugName = each.getName();

            if (ref instanceof Reference) {
                debugName = ((Reference) ref).getReferencedName();
            }
            Object value = getValue(context, ref);
            Object original = obj.getOwnProperty(context, each.getName());
            PropertyDescriptor desc = null;
            if (each instanceof PropertyGet) {
                desc = PropertyDescriptor.newPropertyDescriptorForObjectInitializerGet(original, debugName, (JSFunction) value);
            } else if (each instanceof PropertySet) {
                desc = PropertyDescriptor.newPropertyDescriptorForObjectInitializerSet(original, debugName, (JSFunction) value);
            } else {
                desc = PropertyDescriptor.newPropertyDescriptorForObjectInitializer(debugName, value);
            }
            obj.defineOwnProperty(context, each.getName(), desc, false);
        }

        return(obj);
    }

    @Override
    public Object visit(ExecutionContext context1, PostOpExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = expr.getExpr().accept(context, this, strict);

        if (lhs instanceof Reference) {
            if (((Reference) lhs).isStrictReference()) {
                if (((Reference) lhs).getBase() instanceof EnvironmentRecord) {
                    if (((Reference) lhs).getReferencedName().equals("arguments") || ((Reference) lhs).getReferencedName().equals("eval")) {
                        throw new ThrowException(context, context.createSyntaxError("invalid assignment: " + ((Reference) lhs).getReferencedName()));
                    }
                }
            }

            Number newValue = null;
            Number oldValue = Types.toNumber(context, getValue(context, lhs));

            if (oldValue instanceof Double) {
                switch (expr.getOp()) {
                    case "++":
                        newValue = oldValue.doubleValue() + 1;
                        break;
                    case "--":
                        newValue = oldValue.doubleValue() - 1;
                        break;
                }
            } else {
                switch (expr.getOp()) {
                    case "++":
                        newValue = oldValue.longValue() + 1;
                        break;
                    case "--":
                        newValue = oldValue.longValue() - 1;
                        break;
                }
            }

            ((Reference) lhs).putValue(context, newValue);
            return(oldValue);
        }

        return null; // not reached
    }

    @Override
    public Object visit(ExecutionContext context1, PreOpExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = expr.getExpr().accept(context, this, strict);

        if (lhs instanceof Reference) {
            if (((Reference) lhs).isStrictReference()) {
                if (((Reference) lhs).getBase() instanceof EnvironmentRecord) {
                    if (((Reference) lhs).getReferencedName().equals("arguments") || ((Reference) lhs).getReferencedName().equals("eval")) {
                        throw new ThrowException(context, context.createSyntaxError("invalid assignment: " + ((Reference) lhs).getReferencedName()));
                    }
                }
            }

            Number newValue = null;
            Number oldValue = Types.toNumber(context, getValue(context, lhs));

            if (oldValue instanceof Double) {
                switch (expr.getOp()) {
                    case "++":
                        newValue = oldValue.doubleValue() + 1;
                        break;
                    case "--":
                        newValue = oldValue.doubleValue() - 1;
                        break;
                }
            } else {
                switch (expr.getOp()) {
                    case "++":
                        newValue = oldValue.longValue() + 1;
                        break;
                    case "--":
                        newValue = oldValue.longValue() - 1;
                        break;
                }
            }

            ((Reference) lhs).putValue(context, newValue);
            return(newValue);
        }

        return null; // not reached
    }

    @Override
    public Object visit(ExecutionContext context, PropertyGet propertyGet, boolean strict) {
        JSFunction compiledFn = ((ExecutionContext) context).getCompiler().compileFunction((ExecutionContext) context,
                null,
                new String[]{},
                propertyGet.getBlock(),
                strict);
        return(compiledFn);
    }

    @Override
    public Object visit(ExecutionContext context, PropertySet propertySet, boolean strict) {
        JSFunction compiledFn = ((ExecutionContext) context).getCompiler().compileFunction((ExecutionContext) context,
                null,
                new String[]{propertySet.getIdentifier()},
                propertySet.getBlock(),
                strict);
        return(compiledFn);
    }

    @Override
    public Object visit(ExecutionContext context, NamedValue namedValue, boolean strict) {
        return namedValue.getExpr().accept(context, this, strict);
    }

    @Override
    public Object visit(ExecutionContext context, RegexpLiteralExpression expr, boolean strict) {
        return(BuiltinRegExp.newRegExp((ExecutionContext) context, expr.getPattern(), expr.getFlags()));
    }

    @Override
    public Object visit(ExecutionContext context1, RelationalExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lval = getValue(context, expr.getLhs().accept(context, this, strict));
        Object rval = getValue(context, expr.getRhs().accept(context, this, strict));
        Object r = null;

        switch (expr.getOp()) {
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

    @Override
    public Object visit(ExecutionContext context1, ReturnStatement statement, boolean strict) {
        ExecutionContext context = context1;
        if (statement.getExpr() != null) {
            Object value = statement.getExpr().accept(context, this, strict);
            return(Completion.createReturn(getValue(context, value)));
        } else {
            return(Completion.createReturn(Types.UNDEFINED));
        }
    }

    @Override
    public Object visit(ExecutionContext context1, StrictEqualityOperatorExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object lhs = getValue(context, expr.getLhs().accept(context, this, strict));
        Object rhs = getValue(context, expr.getRhs().accept(context, this, strict));

        Object result = null;
        if (expr.getOp().equals("===")) {
            result = Types.compareStrictEquality(context, lhs, rhs);
        } else {
            result = !Types.compareStrictEquality(context, lhs, rhs);
        }
        return(result);
    }

    @Override
    public Object visit(ExecutionContext context, StringLiteralExpression expr, boolean strict) {
        return(expr.getLiteral());
    }

    @Override
    public Object visit(ExecutionContext context1, SwitchStatement statement, boolean strict) {
        ExecutionContext context = context1;
        Object value = getValue(context, statement.getExpr().accept(context, this, strict));
        Object v = null;

        int numClauses = statement.getCaseClauses().size();

        int startIndex = -1;
        int defaultIndex = -1;

        for (int i = 0; i < numClauses; ++i) {
            CaseClause each = statement.getCaseClauses().get(i);
            if (each instanceof DefaultCaseClause) {
                defaultIndex = i;
                continue;
            }

            Object caseTest = each.getExpression().accept(context, this, strict);
            if (Types.compareStrictEquality(context, value, getValue(context, caseTest))) {
                startIndex = i;
                break;
            }
        }

        if (startIndex < 0 && defaultIndex >= 0) {
            startIndex = defaultIndex;
        }

        if (startIndex >= 0) {
            for (int i = startIndex; i < numClauses; ++i) {
                CaseClause each = statement.getCaseClauses().get(i);
                if (each.getBlock() != null) {
                    Completion completion = (Completion) each.getBlock().accept(context, this, strict);
                    v = completion.value;

                    if (completion.type == Completion.Type.BREAK) {
                        break;
                    } else if (completion.type == Completion.Type.RETURN) {
                        return(completion);
                        
                    }
                }
            }
        }

        return(Completion.createNormal(v));
    }

    @Override
    public Object visit(ExecutionContext context1, TernaryExpression expr, boolean strict) {
        ExecutionContext context = context1;
        if (Types.toBoolean(getValue(context, expr.getTest().accept(context, this, strict)))) {
            return expr.getThenExpr().accept(context, this, strict);
        } else {
            return expr.getElseExpr().accept(context, this, strict);
        }
    }

    @Override
    public Object visit(ExecutionContext context, ThisExpression expr, boolean strict) {
        return(((ExecutionContext) context).getThisBinding());
    }

    @Override
    public Object visit(ExecutionContext context1, ThrowStatement statement, boolean strict) {
        ExecutionContext context = context1;
        Object throwable = getValue(context, statement.getExpr().accept(context, this, strict));
        // if ( throwable instanceof Throwable ) {
        // ((Throwable) throwable).printStackTrace();
        // }
        throw new ThrowException(context, throwable);
    }

    @Override
    public Object visit(ExecutionContext context, TryStatement statement, boolean strict) {
        Completion b = null;
        boolean finallyExecuted = false;
        try {
            b = invokeCompiledBlockStatement(context, "Try", statement.getTryBlock());
        } catch (ThrowException e) {
            if (statement.getCatchClause() != null) {
                // BasicBlock catchBlock = new InterpretedStatement(statement.getCatchClause().getBlock(), strict);
                BasicBlock catchBlock = compiledBlockStatement(context, "Catch", statement.getCatchClause().getBlock());
                try {
                    b = ((ExecutionContext) context).executeCatch(catchBlock, statement.getCatchClause().getIdentifier(), e.getValue());
                } catch (ThrowException e2) {
                    if (statement.getFinallyBlock() != null) {
                        Completion f = invokeCompiledBlockStatement(context, "Finally", statement.getFinallyBlock());
                        if (f.type == Completion.Type.NORMAL) {
                            if (b != null) {
                                return(b);
                            } else {
                                throw e2;
                            }
                        } else {
                            return(f);
                            
                        }
                    } else {
                        throw e2;
                    }
                }
            }

            if (statement.getFinallyBlock() != null) {
                finallyExecuted = true;
                Completion f = invokeCompiledBlockStatement(context, "Finally", statement.getFinallyBlock());
                if (f.type == Completion.Type.NORMAL) {
                    if (b != null) {
                        return(b);
                    } else {
                        throw e;
                    }
                } else {
                    return(f);
                    
                }
            } else {
                if (b != null) {
                    return(b);
                } else {
                    throw e;
                }
            }
        }

        if (!finallyExecuted && statement.getFinallyBlock() != null) {
            Completion f = invokeCompiledBlockStatement(context, "Finally", statement.getFinallyBlock());
            if (f.type == Completion.Type.NORMAL) {
                return(b);
            } else {
                return(f);
            }
        }

        return b;
    }

    @Override
    public Object visit(ExecutionContext context1, TypeOfOpExpression expr, boolean strict) {
        ExecutionContext context = context1;
        return(Types.typeof(context, expr.getExpr().accept(context, this, strict)));
    }

    @Override
    public Object visit(ExecutionContext context1, UnaryMinusExpression expr, boolean strict) {
        ExecutionContext context = context1;
        Object value = getValue(context, expr.getExpr().accept(context, this, strict));
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

    @Override
    public Object visit(ExecutionContext context1, UnaryPlusExpression expr, boolean strict) {
        ExecutionContext context = context1;
        return(Types.toNumber(context, getValue(context, expr.getExpr().accept(context, this, strict))));
    }

    @Override
    public Object visit(ExecutionContext context1, VariableDeclaration expr, boolean strict) {
        ExecutionContext context = context1;
        if (expr.getExpr() != null) {
            Object value = getValue(context, expr.getExpr().accept(context, this, strict));
            Reference var = context.resolve(expr.getIdentifier());
            var.putValue(context, value);
        }
        return(expr.getIdentifier());
    }

    @Override
    public Object visit(ExecutionContext context, VariableStatement statement, boolean strict) {
        for (VariableDeclaration each : statement.getVariableDeclarations()) {
            each.accept(context, this, strict);
        }

        return(Completion.createNormal(Types.UNDEFINED));
    }

    @Override
    public Object visit(ExecutionContext context1, VoidOperatorExpression expr, boolean strict) {
        ExecutionContext context = context1;
        getValue(context, expr.getExpr().accept(context, this, strict));
        return(Types.UNDEFINED);
    }

    @Override
    public Object visit(ExecutionContext context1, WhileStatement statement, boolean strict) {
        ExecutionContext context = context1;
        Expression testExpr = statement.getTest();
        Statement block = statement.getBlock();

        Object v = null;

        while (true) {

            Boolean testResult = Types.toBoolean(getValue(context, testExpr.accept(context, this, strict)));
            if (testResult) {
                // block.accept(context, this, strict);
                // Completion completion = (Completion) pop();
                Completion completion = invokeCompiledBlockStatement(context, "While", block);
                if (completion.value != null) {
                    v = completion.value;
                }
                if (completion.type == Completion.Type.CONTINUE) {
                    if (completion.target == null) {
                        continue;
                    } else if (!statement.getLabels().contains(completion.target)) {
                        return(completion);
                        
                    } else {
                        continue;
                    }
                }
                if (completion.type == Completion.Type.BREAK) {
                    if (completion.target == null) {
                        break;
                    } else if (!statement.getLabels().contains(completion.target)) {
                        return(completion);
                        
                    } else {
                        break;
                    }

                }
                if (completion.type == Completion.Type.RETURN) {
                    return(Completion.createReturn(v));
                    
                }
            } else {
                break;
            }
        }

        return(Completion.createNormal(v));
    }

    @Override
    public Object visit(ExecutionContext context1, WithStatement statement, boolean strict) {
        ExecutionContext context = context1;
        JSObject obj = Types.toObject(context, getValue(context, statement.getExpr().accept(context, this, strict)));
        BasicBlock block = compiledBlockStatement(context, "With", statement.getBlock());
        return(context.executeWith(obj, block));
    }

    protected BasicBlock compiledBlockStatement(Object context, String grist, Statement statement) {
        Entry entry = this.blockManager.retrieve(statement.getStatementNumber());
        if (entry.getCompiled() == null) {
            BasicBlock compiledBlock = ((ExecutionContext) context).getCompiler().compileBasicBlock((ExecutionContext) context, grist, statement, ((ExecutionContext) context).isStrict());
            entry.setCompiled(compiledBlock);
        }
        return entry.getCompiled();
    }

    protected Completion invokeCompiledBlockStatement(Object context, String grist, Statement statement) {
        BasicBlock block = compiledBlockStatement(context, grist, statement);
        return block.call((ExecutionContext) context);
    }

    protected Object getValue(ExecutionContext context, Object obj) {
        return Types.getValue(context, obj);
    }

    private boolean isZero(Number n) {
        return n.doubleValue() == 0.0;
    }

    private boolean isNegativeZero(Number n) {
        return isZero(n) && isNegative(n);
    }

    private boolean isPositiveZero(Number n) {
        return isZero(n) && isPositive(n);
    }

    private boolean isNegative(Number n) {
        return (Double.compare(n.doubleValue(), 0.0) < 0);
    }

    private boolean isPositive(Number n) {
        return (Double.compare(n.doubleValue(), 0.0) >= 0);
    }

    private boolean isSameSign(Number n1, Number n2) {
        return (isPositive(n1) && isPositive(n2)) || (isNegative(n1) && isNegative(n2));
    }

    private boolean isDifferentSign(Number n1, Number n2) {
        return (isPositive(n1) && isNegative(n2)) || (isNegative(n1) && isPositive(n2));
    }

    private boolean isRepresentableByLong(double n) {
        if (isNegativeZero(n)) {
            return false;
        }
        return (n == (long) n);
    }
}
