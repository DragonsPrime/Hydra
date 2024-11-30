package com.pinecone.hydra.umct.protocol.compiler;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.system.prototype.Pinenut;

public interface MethodDigest extends Pinenut {

    ClassDigest getClassDigest();

    String getName();

    String getRawName();

    DataStructureEntity getArgumentTemplate();

    Class<?> getReturnType();

}
