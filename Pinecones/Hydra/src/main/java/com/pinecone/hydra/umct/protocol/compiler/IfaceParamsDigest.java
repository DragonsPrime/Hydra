package com.pinecone.hydra.umct.protocol.compiler;

import com.pinecone.hydra.umct.mapping.ParamsDigest;

public interface IfaceParamsDigest extends ParamsDigest {
    MethodDigest getMethodDigest();

    default ClassDigest getClassDigest() {
        return this.getMethodDigest().getClassDigest();
    }
}
