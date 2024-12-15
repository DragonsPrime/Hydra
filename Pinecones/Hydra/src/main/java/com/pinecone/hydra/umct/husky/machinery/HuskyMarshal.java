package com.pinecone.hydra.umct.husky.machinery;

import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.lang.GenericDynamicFactory;
import com.pinecone.framework.util.lang.ScopedPackage;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.mapping.ControllerInspector;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;


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
    protected DynamicFactory       mIfaceFactory;
    protected MultiMappingLoader   mMultiMappingLoader;

    public HuskyMarshal( InterfacialCompiler compiler, ControllerInspector controllerInspector, FieldProtobufDecoder decoder ) {
        super( compiler, controllerInspector, decoder );

        this.mIfaceFactory       = new GenericDynamicFactory( controllerInspector.getClassLoader() );
        this.mMultiMappingLoader = new HuskyMappingLoader( this.mIfaceFactory, this );
    }

    @Override
    public MultiMappingLoader getMultiMappingLoader() {
        return this.mMultiMappingLoader;
    }

    @Override
    public PMCTMarshal addScope ( String szPackageName ) {
        this.mIfaceFactory.getClassScope().addScope( szPackageName );
        return this;
    }

    @Override
    public PMCTMarshal addScope ( ScopedPackage scope ) {
        this.mIfaceFactory.getClassScope().addScope( scope );
        return this;
    }
}
