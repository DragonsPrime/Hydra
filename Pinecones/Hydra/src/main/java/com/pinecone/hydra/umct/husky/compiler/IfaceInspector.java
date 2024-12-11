package com.pinecone.hydra.umct.husky.compiler;

import java.util.List;

import com.pinecone.framework.system.prototype.Pinenut;

import javassist.CtMethod;
import javassist.NotFoundException;

public interface IfaceInspector extends Pinenut {
    List<CtMethod> inspect( Class<?> clazz, boolean bAsIface ) throws NotFoundException ;

    List<CtMethod> inspect( String className, boolean bAsIface ) throws NotFoundException;

    String getIfaceMethodName( CtMethod method ) throws ClassNotFoundException;
}
