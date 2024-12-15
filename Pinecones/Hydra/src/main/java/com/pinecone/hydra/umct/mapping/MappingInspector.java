package com.pinecone.hydra.umct.mapping;

import java.util.List;

import com.pinecone.ulf.util.lang.HierarchyClassInspector;

import javassist.CtMethod;

public interface MappingInspector extends HierarchyClassInspector {
    List<ParamsDigest> inspectArgParams( Object methodDigest, CtMethod method );

    ClassLoader getClassLoader();
}
