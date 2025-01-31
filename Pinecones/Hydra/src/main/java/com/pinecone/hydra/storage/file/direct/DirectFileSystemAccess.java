package com.pinecone.hydra.storage.file.direct;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.ExternalSymbolic;

public interface DirectFileSystemAccess extends Pinenut {
    void remove(String path);

    ElementNode queryElement(String path);

    void insertExternalSymbolic(ExternalSymbolic externalSymbolic);


}
