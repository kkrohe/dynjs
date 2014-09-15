package org.dynjs.parser.js;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Token implements Position {
    
    private TokenType type;
    private String text;
    private String fileName;
    private int lineNumber;
    private int columnNumber;
    private boolean escapedString;
    private boolean escapedOctalString;
    private boolean continuedLine;
    private List<Token> comments;

    public Token(TokenType type, String text, String fileName, int lineNumber, int columnNumber) {
        this.type = type;
        this.text = text;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }
    
    public void setEscapedString(boolean escapedString) {
        this.escapedString = escapedString;
    }
    
    public boolean isEscapedString() {
        return this.escapedString;
    }
    
    public void setEscapedOctalString(boolean escapedOctalString) {
        this.escapedOctalString = escapedOctalString;
    }
    
    public boolean isEscapedOctalString() {
        return this.escapedOctalString;
    }
    
    public void setContinuedLine(boolean continuedLine) {
        this.continuedLine = continuedLine;
    }
    
    public boolean isContinuedLine() {
        return this.continuedLine;
    }
    
    public TokenType getType() {
        return this.type;
    }
    
    public String getText() {
        if ( this.type.isUnprintable() ) {
            return this.type.getDescription();
        }
        return this.text;
    }
    
    public boolean isSkippable() {
        return this.type.isSkippable();
    }

    @Override
    public Collection<Token> getComments() {
        return comments;
    }

    @Override
    public void addComments(Collection<Token> comments) {
        if(this.comments == null) {
            this.comments = new ArrayList<Token>();
        }
        this.comments.addAll(comments);
    }

    @Override
    public void addComments(Token... comments) {
        if(this.comments == null) {
            this.comments = new ArrayList<Token>();
        }
        Collections.addAll(this.comments, comments);
    }

    public String getFileName() {
        return this.fileName;
    }
    
    public int getLine() {
        return this.lineNumber;
    }
    
    public int getColumn() {
        return this.columnNumber;
    }
    
    public String toString() {
        return "[Token: type=" + this.type + "; text=" + this.text + "; file=" + this.fileName + "; line=" + this.lineNumber + "; col=" + this.columnNumber + "]";
    }

}
