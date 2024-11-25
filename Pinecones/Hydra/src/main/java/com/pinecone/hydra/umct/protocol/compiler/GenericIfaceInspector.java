package com.pinecone.hydra.umct.protocol.compiler;

import javassist.ClassPool;

public class GenericIfaceInspector extends ArchIfaceInspector implements IfaceInspector {
    public GenericIfaceInspector( ClassPool classPool ) {
        super( classPool );
    }
}