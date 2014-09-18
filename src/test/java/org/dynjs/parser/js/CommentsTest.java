package org.dynjs.parser.js;

import org.dynjs.compiler.ParseContext;
import org.dynjs.parser.DefaultCommentVisitor;
import org.dynjs.parser.ast.NewOperatorExpression;
import org.dynjs.parser.ast.ProgramTree;
import org.dynjs.runtime.JSObject;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import static org.dynjs.parser.js.TokenType.*;
import static org.fest.assertions.Assertions.*;

public class CommentsTest {


    public ProgramTree getParseTree(String resourceName) {
        InputStream input = getClass().getResourceAsStream(resourceName);
        ParseContext context = new ParseContext() {
            @Override public JSObject createSyntaxError(String message) {
                return null;
            }
        };
        ASTFactory factory = new ASTFactory();
        JavascriptParser jsParser = new JavascriptParser(context, factory);
        return jsParser.parse(new InputStreamReader(input), resourceName);
    }

    @Test
    public void testBasicCommentParsing() throws IOException{
        ProgramTree tree = getParseTree("/stack.js");
        final int[] comments = new int[1];
        comments[0] = 0;

        DefaultCommentVisitor vis = new DefaultCommentVisitor(){
            @Override public Object visitComment(Object context, Token comment, boolean strict) {
                comments[0]++;
                System.out.println("found comment : " + comment.getText());
                return null;
            }
        };

        tree.accept(null, vis, true);
        assertThat(comments[0]).isEqualTo(7);
    }

    @Test
    public void testCommonCommentParsing() throws IOException{
        ProgramTree tree = getParseTree("/comments.js");
        final int[] comments = new int[1];
        comments[0] = 0;

        DefaultCommentVisitor vis = new DefaultCommentVisitor(){
            private HashSet<Token> visited = new HashSet<Token>();

            @Override public Object visitComment(Object context, Token comment, boolean strict) {
                if(!visited.contains(comment)) {
                    visited.add(comment);
                    comments[0]++;
                    System.out.println("found comment : " + comment.getText());
                }
                return null;
            }

            @Override public Object visit(Object context, NewOperatorExpression expr, boolean strict) {
                super.visit(context, expr, strict);
                return null;
            }
        };

        tree.accept(null, vis, true);
        assertThat(comments[0]).isEqualTo(9);
    }

}
