package org.dynjs.compiler;

import org.dynjs.Config;
import org.dynjs.runtime.*;

/**
 * @author Bob McWhirter
 */
public interface CompilationContext extends ParseContext {
    JSCompiler getCompiler();
    BlockManager getBlockManager();
    DynamicClassLoader getClassLoader();
    Config getConfig();

    // used by parser

    // only used by function compilation
    LexicalEnvironment getLexicalEnvironment();
    GlobalObject getGlobalObject();
}
