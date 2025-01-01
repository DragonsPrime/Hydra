package com.pinecone.hydra.storage.version;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.version.entity.TitanVersion;

public interface VersionManage extends Pinenut {
    void insert(TitanVersion version);

    void remove(String version, GUID fileGuid);

    GUID queryObjectGuid(String version, GUID fileGuid );
}
