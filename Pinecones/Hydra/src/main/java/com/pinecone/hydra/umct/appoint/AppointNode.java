package com.pinecone.hydra.umct.appoint;

import com.pinecone.hydra.umc.msg.MessageNode;
import com.pinecone.hydra.umct.UMCTNode;
import com.pinecone.hydra.umct.protocol.compiler.ClassDigest;
import com.pinecone.hydra.umct.protocol.compiler.InterfacialCompiler;
import com.pinecone.hydra.umct.protocol.compiler.MethodDigest;
import com.pinecone.ulf.util.protobuf.FieldProtobufDecoder;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;

public interface AppointNode extends UMCTNode {
    MessageNode getMessageNode();

    InterfacialCompiler getInterfacialCompiler();

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
