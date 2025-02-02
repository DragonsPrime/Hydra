package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.Volume;

import java.util.List;

public interface SpannedVolumeManipulator extends LogicVolumeManipulator {
    void insert( SpannedVolume spannedVolume );
    void remove( GUID guid );
    SpannedVolume getSpannedVolume(GUID guid);
    void extendLogicalVolume( GUID logicGuid, GUID physicalGuid );
    List<GUID> listPhysicalVolume(GUID logicGuid );

    List<Volume> queryAllSpannedVolume();

    void updateDefinitionCapacity( GUID guid, long definitionCapacity );
}
