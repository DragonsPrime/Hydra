package com.pinecone.hydra.storage.version;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.version.entity.Version;

public interface VersionManage extends Pinenut {
    void insert(Version version);

    void remove(String version, GUID fileGuid);

    Version query( String version, GUID fileGuid );
}
