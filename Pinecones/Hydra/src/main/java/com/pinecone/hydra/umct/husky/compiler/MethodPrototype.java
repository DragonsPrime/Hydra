package com.pinecone.hydra.umct.husky.compiler;

import com.google.protobuf.Descriptors;
import com.pinecone.hydra.umct.husky.function.ArgumentRequest;

public interface MethodPrototype extends MethodDigest {
    Descriptors.Descriptor getArgumentsDescriptor();

    Descriptors.Descriptor getReturnDescriptor();

    ArgumentRequest conformRequest();

    ArgumentRequest conformRequest( Object[] args );
}
