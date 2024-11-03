package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.FolderMeta;
import com.pinecone.hydra.storage.file.entity.GenericFolderMeta;
import com.pinecone.hydra.storage.file.source.FolderMetaManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
@IbatisDataAccessObject
public interface FolderMetaMapper extends FolderMetaManipulator {
    FolderMeta getFolderMeta(GUID guid, ElementNode element);
    @Insert("INSERT INTO `hydra_uofs_folder_meta` (`guid`) VALUES (#{guid})")
    void insert( FolderMeta folderMeta );
    @Delete("DELETE FROM `hydra_uofs_folder_meta` WHERE `guid` = #{guid}")
    void remove( GUID guid );
    @Select("SELECT `id` AS enumId, `guid` FROM `hydra_uofs_folder_meta` WHERE `guid` = #{guid}")
    GenericFolderMeta getFolderMetaByGuid(GUID guid);
}
