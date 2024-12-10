package com.pinecone.hydra.umct.mapping;

import java.lang.reflect.Method;
import java.util.List;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umct.protocol.compiler.IfaceParamsDigest;

public interface MappingDigest extends Pinenut {
    String getAddress();

    DataStructureEntity getArgumentTemplate();

    Class<?> getClassType();

    Method getMappedMethod();

    Class<?> getReturnType();

    List<ParamsDigest> getParamsDigests();

    void apply( List<ParamsDigest> ifaceParamsDigests);

    List<String> getArgumentsKey();
}
