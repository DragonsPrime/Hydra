package com.pinecone.hydra.umct.husky.compiler;

public interface IfaceCompiler extends IfaceInspector {
    ClassDigest compile ( String className, boolean bAsIface );

    ClassDigest compile ( Class<? > clazz, boolean bAsIface );

    ClassDigest compile ( String className, boolean bAsIface, CompilerEncoder encoder );

    ClassDigest compile ( Class<? > clazz, boolean bAsIface, CompilerEncoder encoder );

    ClassDigest reinterpret ( String className, boolean bAsIface );

    ClassDigest reinterpret ( Class<? > clazz, boolean bAsIface );

    CompilerEncoder getCompilerEncoder();
}
