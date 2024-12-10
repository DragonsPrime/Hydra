package com.pinecone.hydra.umct.mapping;

import com.pinecone.framework.system.prototype.Pinenut;

public interface ParamsDigest extends Pinenut {
    int getParameterIndex() ;

    String getName();

    String getValue();

    boolean isRequired();

    String getDefaultValue();
}
