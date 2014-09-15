package org.dynjs.parser.js;

import java.util.Collection;

public interface Position {

    Collection<Token> getComments();
    void addComments(Collection<Token> comments);
    void addComments(Token... comments);

    String getFileName();
    int getLine();
    int getColumn();

}
