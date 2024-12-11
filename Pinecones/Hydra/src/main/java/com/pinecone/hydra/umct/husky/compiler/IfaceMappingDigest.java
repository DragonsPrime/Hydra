package com.pinecone.hydra.umct.husky.compiler;

import com.google.protobuf.Descriptors;
import com.pinecone.hydra.umct.mapping.MappingDigest;

public interface IfaceMappingDigest extends MappingDigest {
    Descriptors.Descriptor getArgumentsDescriptor();

    Descriptors.Descriptor getReturnDescriptor();
}
