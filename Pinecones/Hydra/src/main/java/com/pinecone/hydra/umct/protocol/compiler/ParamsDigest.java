package com.pinecone.hydra.umct.protocol.compiler;

import com.pinecone.framework.system.prototype.Pinenut;

public interface ParamsDigest extends Pinenut {
    MethodDigest getMethodDigest();

    default ClassDigest getClassDigest() {
        return this.getMethodDigest().getClassDigest();
    }

    int getParameterIndex() ;

    String getName();

    String getValue();

    boolean isRequired();

    String getDefaultValue();
}
