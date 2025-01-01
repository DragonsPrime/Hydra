package com.pinecone.hydra.umct.husky.compiler;

import javassist.ClassPool;

public class GenericIfaceInspector extends ArchIfaceInspector implements IfaceInspector {
    public GenericIfaceInspector( ClassPool classPool, ClassLoader classLoader ) {
        super( classPool, classLoader );
    }

    public GenericIfaceInspector( ClassPool classPool ) {
        super( classPool, Thread.currentThread().getContextClassLoader() );
    }
}