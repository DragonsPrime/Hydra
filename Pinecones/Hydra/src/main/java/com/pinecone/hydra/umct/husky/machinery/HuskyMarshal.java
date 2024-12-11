package com.pinecone.hydra.umct.husky.machinery;

import java.util.Map;
import java.util.Set;

import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.unit.LinkedTreeSet;
import com.pinecone.framework.util.lang.ClassScanner;
import com.pinecone.framework.util.lang.ClassScope;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.mapping.ControllerInspector;
import com.pinecone.ulf.util.lang.HierarchyClassInspector;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;

import javassist.ClassPool;
import javassist.CtClass;

/**
 *  Pinecone Ursus For Java Hydra Ulfar, Husky Marshal
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  Husky King
 *  Husky Transformer | Husky Marshal
 *  *****************************************************************************************
 */
public class HuskyMarshal extends HuskyTransformer implements PMCTMarshal {
    public HuskyMarshal( InterfacialCompiler compiler, ControllerInspector controllerInspector, FieldProtobufDecoder decoder ) {
        super( compiler, controllerInspector, decoder );
    }
}
