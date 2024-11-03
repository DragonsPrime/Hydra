package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.SymbolicMeta;
import com.pinecone.hydra.storage.file.source.SymbolicMetaManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@IbatisDataAccessObject
public interface SymbolicMetaMapper extends SymbolicMetaManipulator {
    //SymbolicMeta getSymbolicMeta(GUID guid, ElementNode element);
    void insert( SymbolicMeta symbolicMeta );
    void remove( GUID guid );
    SymbolicMeta getSymbolicMetaByGuid(GUID guid);
}
