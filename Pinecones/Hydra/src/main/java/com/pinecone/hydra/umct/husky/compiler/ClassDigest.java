package com.pinecone.hydra.umct.husky.compiler;

import java.util.List;

import com.pinecone.framework.system.prototype.Pinenut;

public interface ClassDigest extends Pinenut {
    String getClassName();

    void addMethod( MethodDigest methodDigest );

    List<MethodDigest> getMethodDigests();
}
