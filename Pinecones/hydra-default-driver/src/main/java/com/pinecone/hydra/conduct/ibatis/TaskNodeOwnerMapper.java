package com.pinecone.hydra.conduct.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.source.TireOwnerManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
@IbatisDataAccessObject
public interface TaskNodeOwnerMapper extends TireOwnerManipulator {
    void insert(GUID subordinateGuid, GUID ownerGuid);

    void remove(GUID subordinateGuid,GUID ownerGuid);

    void removeBySubordinate(GUID subordinateGuid);

    void removeByOwner(GUID OwnerGuid);

    GUID getOwner(GUID subordinateGuid);

    List<GUID> getSubordinates(GUID guid);
}
