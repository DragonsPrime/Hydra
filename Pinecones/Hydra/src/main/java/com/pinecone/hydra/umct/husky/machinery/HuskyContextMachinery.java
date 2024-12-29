package com.pinecone.hydra.umct.husky.machinery;

import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.lang.GenericDynamicFactory;
import com.pinecone.framework.util.lang.ScopedPackage;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.mapping.ControllerInspector;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;


/**
 *  Pinecone Ursus For Java Hydra Ulfar, Husky Machinery
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  *****************************************************************************************
 *  Husky Transformer | Husky Machinery
 *  *****************************************************************************************
 */
public class HuskyContextMachinery extends HuskyTransformer implements PMCTContextMachinery {
    protected DynamicFactory       mIfaceFactory;
    protected MultiMappingLoader   mMultiMappingLoader;

    public HuskyContextMachinery( InterfacialCompiler compiler, ControllerInspector controllerInspector, FieldProtobufDecoder decoder ) {
        super( compiler, controllerInspector, decoder );

        this.mIfaceFactory       = new GenericDynamicFactory( controllerInspector.getClassLoader() );
        this.mMultiMappingLoader = new HuskyMappingLoader( this.mIfaceFactory, this );
    }

    @Override
    public MultiMappingLoader getMultiMappingLoader() {
        return this.mMultiMappingLoader;
    }

    @Override
    public PMCTContextMachinery addScope ( String szPackageName ) {
        this.mIfaceFactory.getClassScope().addScope( szPackageName );
        return this;
    }

    @Override
    public PMCTContextMachinery addScope ( ScopedPackage scope ) {
        this.mIfaceFactory.getClassScope().addScope( scope );
        return this;
    }
}
