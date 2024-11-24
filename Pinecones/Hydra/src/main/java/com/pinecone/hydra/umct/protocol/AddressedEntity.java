package com.pinecone.hydra.umct.protocol;

import com.pinecone.framework.system.prototype.Pinenut;

public interface AddressedEntity extends Pinenut {
    /**
     * Full Name / Path
     */
    String getInterceptedPath();

    /**
     * Function / Method / Mapping
     */
    String getInterceptorName();

    /**
     * Namespace / Domain / Package
     */
    String getAddressPath();
}
