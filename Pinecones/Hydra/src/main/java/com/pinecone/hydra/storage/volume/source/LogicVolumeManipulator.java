package com.pinecone.hydra.storage.volume.source;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.system.ko.dao.GUIDNameManipulator;

import java.util.List;

public interface LogicVolumeManipulator extends GUIDNameManipulator {
    void extendLogicalVolume( GUID logicGuid, GUID physicalGuid );

    List< GUID > listPhysicalVolume( GUID logicGuid );
}
