package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.Volume;

import java.util.List;

public interface SimpleVolumeManipulator extends LogicVolumeManipulator {
    void insert( SimpleVolume simpleVolume );

    void remove( GUID guid );

    SimpleVolume getSimpleVolume(GUID guid);

    void extendLogicalVolume( GUID logicGuid, GUID physicalGuid );

    List<GUID> listPhysicalVolume(GUID logicGuid );

    List<Volume> queryAllSimpleVolumes();
}
