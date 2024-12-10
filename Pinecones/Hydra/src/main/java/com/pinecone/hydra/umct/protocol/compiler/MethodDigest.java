package com.pinecone.hydra.umct.protocol.compiler;

import java.util.List;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.system.prototype.Pinenut;

public interface MethodDigest extends Pinenut {

    ClassDigest getClassDigest();

    String getName();

    String getFullName();

    String getRawName();

    DataStructureEntity getArgumentTemplate();

    Class<?> getReturnType();

    List<IfaceParamsDigest> getParamsDigests();

    void apply( List<IfaceParamsDigest> ifaceParamsDigests);

    List<String> getArgumentsKey();
}
