package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.FileMeta;
import com.pinecone.hydra.storage.file.entity.GenericFileMeta;
import com.pinecone.hydra.storage.file.source.FileMetaManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
@IbatisDataAccessObject
public interface FileMetaMapper extends FileMetaManipulator {
    FileMeta getFileMeta(GUID guid, ElementNode element);
    @Insert("INSERT INTO `hydra_uofs_files_meta` (`guid`) VALUES (#{guid})")
    void insert( FileMeta fileMeta );
    @Delete("DELETE FROM `hydra_uofs_files_meta` WHERE `guid` = #{guid}")
    void remove( GUID guid );
    @Select("SELECT `id` AS emunId, `guid` FROM `hydra_uofs_files_meta` WHERE guid = #{guid}")
    GenericFileMeta getFileMetaByGuid(GUID guid);
}
