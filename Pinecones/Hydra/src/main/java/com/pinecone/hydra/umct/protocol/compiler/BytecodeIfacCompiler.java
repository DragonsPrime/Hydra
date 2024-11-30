package com.pinecone.hydra.umct.protocol.compiler;

import javassist.ClassPool;

public class BytecodeIfacCompiler extends ArchIfacCompiler implements InterfacialCompiler {
    public BytecodeIfacCompiler( ClassPool classPool, ClassLoader classLoader, CompilerEncoder encoder ) {
        super( classPool, classLoader, encoder );
    }

    public BytecodeIfacCompiler( ClassPool classPool, ClassLoader classLoader ) {
        super( classPool, classLoader );
    }

    public BytecodeIfacCompiler( ClassPool classPool ) {
        super( classPool, Thread.currentThread().getContextClassLoader() );
    }
}
