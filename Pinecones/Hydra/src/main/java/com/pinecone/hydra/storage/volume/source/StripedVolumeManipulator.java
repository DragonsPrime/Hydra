package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.Volume;

import java.util.List;

public interface StripedVolumeManipulator extends LogicVolumeManipulator {
    void insert( StripedVolume stripedVolume );
    void remove( GUID guid );
    StripedVolume getStripedVolume(GUID guid);
    void extendLogicalVolume( GUID logicGuid, GUID physicalGuid );
    List<GUID> listPhysicalVolume(GUID logicGuid );

    List<Volume> queryAllStripedVolume();
}
