package com.pinecone.hydra.umct.protocol.compiler;

import javassist.ClassPool;

public abstract class ArchIfacCompiler extends ArchIfaceInspector implements IfaceCompiler {
    public ArchIfacCompiler( ClassPool classPool ) {
        super( classPool );
    }
}
