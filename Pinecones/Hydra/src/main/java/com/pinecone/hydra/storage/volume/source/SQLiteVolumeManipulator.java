package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface SQLiteVolumeManipulator extends Pinenut {
    void insert( GUID physicsGuid, GUID volumeGuid );

    GUID getPhysicsGuid( GUID volumeGuid );
}
