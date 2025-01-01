package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface LineSegmentManipulator extends Pinenut {
    void insert( int idMin, int idMax, GUID volumeGuid );
    GUID getVolumeGuid( int id );
    void delete( int idMin, int idMax );
}
