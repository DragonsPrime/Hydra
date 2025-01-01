package com.pinecone.hydra.umct.husky.machinery;

import java.util.List;
import java.util.Map;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umct.MessageHandler;
import com.pinecone.hydra.umct.husky.compiler.ClassDigest;
import com.pinecone.hydra.umct.husky.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.husky.compiler.MethodDigest;
import com.pinecone.hydra.umct.mapping.ControllerInspector;
import com.pinecone.hydra.umct.mapping.MappingDigest;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;

public interface PMCTTransformer extends Pinenut {
    InterfacialCompiler getInterfacialCompiler();

    ControllerInspector getControllerInspector();

    Map<String, MessageHandler> getMessageHandlerMap();

    List<MappingDigest > getMappingDigests();

    default void addAll( List<MappingDigest > digests ){
        this.getMappingDigests().addAll( digests );
    }

    default FieldProtobufEncoder getFieldProtobufEncoder() {
        return this.getInterfacialCompiler().getCompilerEncoder().getEncoder();
    }

    FieldProtobufDecoder getFieldProtobufDecoder();

    ClassDigest queryClassDigest( String name );

    MethodDigest queryMethodDigest( String name );

    void addClassDigest( ClassDigest that );

    void addMethodDigest( MethodDigest that );

    ClassDigest compile( Class<? > clazz, boolean bAsIface );
}
