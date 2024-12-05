package com.pinecone.hydra.umct;

import java.util.List;

import com.pinecone.framework.system.prototype.Pinenut;

public interface MessageController extends Pinenut {
    String getAddressMapping();

    void invoke( Object... args ) throws Exception;

    List<String > getArgumentsKey();

    default boolean isArgsIndexed() {
        return this.getArgumentsKey() == null;
    }
}
