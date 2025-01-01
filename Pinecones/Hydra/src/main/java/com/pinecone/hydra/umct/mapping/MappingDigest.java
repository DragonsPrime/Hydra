package com.pinecone.hydra.umct.mapping;

import java.lang.reflect.Method;
import java.util.List;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umc.msg.UMCMethod;

public interface MappingDigest extends Pinenut {
    String[] getAddresses();

    default boolean isAnonymousAddress() {
        return this.getAddresses().length == 0;
    }

    UMCMethod[] getInterceptMethods();

    DataStructureEntity getArgumentTemplate();

    Class<?> getClassType();

    Method getMappedMethod();

    Class<?> getReturnType();

    List<ParamsDigest> getParamsDigests();

    void apply( List<ParamsDigest> ifaceParamsDigests);

    List<String> getArgumentsKey();
}
