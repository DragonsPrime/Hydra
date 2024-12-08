package com.pinecone.hydra.umct.protocol.compiler;

import java.util.Set;

import com.google.protobuf.Descriptors;
import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.ulf.util.protobuf.FieldProtobufEncoder;
import com.pinecone.ulf.util.protobuf.Options;

public interface CompilerEncoder extends Pinenut {
    CompilerEncoder DefaultMethodArgumentsCompilerEncoder = new GenericCompilerEncoder( "_Arguments" );

    FieldProtobufEncoder getEncoder();

    Options getOptions();

    String getEntityExtend();

    Set<String > getExceptedKeys();


    Descriptors.Descriptor transform(DataStructureEntity entity );
}
