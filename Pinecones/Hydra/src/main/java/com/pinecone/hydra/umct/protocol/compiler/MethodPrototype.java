package com.pinecone.hydra.umct.protocol.compiler;

import com.google.protobuf.Descriptors;
import com.pinecone.hydra.umct.protocol.function.ArgumentRequest;

public interface MethodPrototype extends MethodDigest {
    Descriptors.Descriptor getArgumentsDescriptor();

    Descriptors.Descriptor getReturnDescriptor();

    ArgumentRequest conformRequest();

    ArgumentRequest conformRequest( Object[] args );
}
