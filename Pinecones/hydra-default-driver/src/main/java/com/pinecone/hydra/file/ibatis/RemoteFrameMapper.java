package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.GenericRemoteFrame;
import com.pinecone.hydra.storage.file.entity.RemoteFrame;
import com.pinecone.hydra.storage.file.source.RemoteFrameManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
@IbatisDataAccessObject
public interface RemoteFrameMapper extends RemoteFrameManipulator {
    RemoteFrame getRemoteFrame(GUID guid, ElementNode element);
    @Insert("INSERT INTO `hydra_uofs_files_cluster_mapping` (`file_guid`, `seg_guid`, `device_guid`, `seg_id`, `crc32`, `size`) VALUES (#{fileGuid},#{segGuid},#{deviceGuid},#{segId},#{crc32},#{size})")
    void insert( RemoteFrame remoteFrame );
    @Delete("DELETE FROM `hydra_uofs_files_cluster_mapping` WHERE `seg_guid` = #{guid}")
    void remove( GUID guid );
    @Select("SELECT `id` AS enumID, `file_guid` AS fileGuid, `seg_guid` AS segGuid, `device_guid` AS deviceGuid, `seg_id` AS segId, `crc32`, `size` FROM `hydra_uofs_files_cluster_mapping` WHERE `seg_guid` = #{guid}")
    RemoteFrame getRemoteFrameByGuid(GUID guid);
    @Select("SELECT `id`, `file_guid` AS fileGuid, `seg_guid` AS segGuid, `device_guid` AS deviceGuid, `seg_id` AS segId, `crc32`, `size` FROM `hydra_uofs_files_cluster_mapping` WHERE `file_guid` = #{guid}")
    List<GenericRemoteFrame> getRemoteFrameByFileGuid0(GUID guid );
    default List< RemoteFrame > getRemoteFrameByFileGuid(GUID guid ){
        List<RemoteFrame> remoteFrames = new ArrayList<>();
        List<GenericRemoteFrame> frames = this.getRemoteFrameByFileGuid0(guid);
        for (RemoteFrame frame : frames){
            frame.setRemoteFrameManipulator(this);
            remoteFrames.add(frame);
        }
        return remoteFrames;
    };
    @Select("SELECT `id` AS emunId, `file_guid` AS fileGuid, `seg_guid` AS segGuid, `device_guid` AS deviceGuid, `seg_id` AS segId, `crc32`, `size` FROM `hydra_uofs_files_cluster_mapping` WHERE `file_guid` = #{guid} ORDER BY `seg_id` DESC LIMIT 1")
    RemoteFrame getLastFrame( GUID guid );
}
