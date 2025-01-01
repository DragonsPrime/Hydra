package com.pinecone.ulf.util.protobuf;

import com.pinecone.framework.system.prototype.Pinenut;

public interface DescriptorNameNormalizer extends Pinenut {
    String normalize( String bad );
}
