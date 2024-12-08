package com.pinecone.hydra.umct.protocol.compiler;

import java.util.List;

import javassist.CtMethod;

public interface IfaceCompiler extends IfaceInspector {
    ClassDigest compile ( String className, boolean bAsIface );

    ClassDigest compile ( Class<? > clazz, boolean bAsIface );

    ClassDigest compile ( String className, boolean bAsIface, CompilerEncoder encoder );

    ClassDigest compile ( Class<? > clazz, boolean bAsIface, CompilerEncoder encoder );

    ClassDigest reinterpret ( String className, boolean bAsIface );

    ClassDigest reinterpret ( Class<? > clazz, boolean bAsIface );

    CompilerEncoder getCompilerEncoder();

    List<ParamsDigest > inspectArgParams(MethodDigest methodDigest, CtMethod method );
}
