package com.pinecone.hydra.umct.husky.machinery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.pinecone.hydra.umct.MessageHandler;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodDigest;
import com.pinecone.hydra.umct.mapping.ControllerInspector;
import com.pinecone.hydra.umct.mapping.MappingDigest;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;

public class HuskyTransformer implements PMCTTransformer {
    protected InterfacialCompiler          mInterfacialCompiler;

    protected FieldProtobufEncoder         mFieldProtobufEncoder;

    protected FieldProtobufDecoder         mFieldProtobufDecoder;

    protected Map<String, ClassDigest >    mClassDigests;

    protected Map<String, MethodDigest >   mMethodDigests;

    protected ControllerInspector          mControllerInspector;

    protected List<MappingDigest >         mMappingDigests;

    protected Map<String, MessageHandler>  mMessageHandlerMap;

    public HuskyTransformer( InterfacialCompiler compiler, ControllerInspector controllerInspector, FieldProtobufDecoder decoder ) {
        this.mInterfacialCompiler   = compiler;
        this.mControllerInspector   = controllerInspector;
        this.mClassDigests          = new LinkedHashMap<>();
        this.mMethodDigests         = new LinkedHashMap<>();

        this.mFieldProtobufEncoder  = compiler.getCompilerEncoder().getEncoder();
        this.mFieldProtobufDecoder  = decoder;
        this.mMessageHandlerMap     = new HashMap<>();
        this.mMappingDigests        = new ArrayList<>();
    }

    @Override
    public InterfacialCompiler getInterfacialCompiler() {
        return this.mInterfacialCompiler;
    }

    @Override
    public ControllerInspector getControllerInspector() {
        return this.mControllerInspector;
    }

    @Override
    public List<MappingDigest > getMappingDigests() {
        return this.mMappingDigests;
    }

    @Override
    public Map<String, MessageHandler> getMessageHandlerMap() {
        return this.mMessageHandlerMap;
    }

    @Override
    public FieldProtobufEncoder getFieldProtobufEncoder() {
        return this.mFieldProtobufEncoder;
    }

    @Override
    public FieldProtobufDecoder getFieldProtobufDecoder() {
        return this.mFieldProtobufDecoder;
    }

    @Override
    public ClassDigest queryClassDigest( String name ) {
        return this.mClassDigests.get( name );
    }

    @Override
    public MethodDigest queryMethodDigest( String name ) {
        return this.mMethodDigests.get( name );
    }

    @Override
    public void addClassDigest( ClassDigest that ) {
        this.mClassDigests.put( that.getClassName(), that );
        List<MethodDigest> digests = that.getMethodDigests();
        for ( MethodDigest digest : digests ) {
            this.addMethodDigest( digest );
        }
    }

    @Override
    public void addMethodDigest( MethodDigest that ) {
        this.mMethodDigests.put( that.getFullName(), that );
    }

    @Override
    public ClassDigest compile( Class<? > clazz, boolean bAsIface ) {
        ClassDigest neo = this.mInterfacialCompiler.compile( clazz, bAsIface );
        this.addClassDigest( neo );
        return neo;
    }
}
