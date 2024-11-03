package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.GenericLocalFrame;
import com.pinecone.hydra.storage.file.entity.LocalFrame;
import com.pinecone.hydra.storage.file.source.LocalFrameManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
@IbatisDataAccessObject
public interface LocalFrameMapper extends LocalFrameManipulator {
    LocalFrame getLocalFrame(GUID guid, ElementNode element);
    @Insert("INSERT INTO `hydra_uofs_local_cluster_fat` (`file_guid`, `seg_guid`, `seg_id`, `create_time`, `update_time`, `source_name`, `crc32`, `size`) VALUES (#{fileGuid},#{segGuid},#{segId},#{createTime},#{updateTime},#{sourceName},#{crc32},#{size})")
    void insert( LocalFrame localFrame );
    @Delete("DELETE FROM `hydra_uofs_local_cluster_fat` WHERE `seg_guid` = #{guid}")
    void remove( GUID guid );

    default GenericLocalFrame getLocalFrameByGuid(GUID guid){
        GenericLocalFrame localFrame = this.getLocalFrameByGuid0(guid);
        if ( localFrame == null ) return null;
        localFrame.setLocalFrameManipulator( this );
        return localFrame;
    }
    @Select("SELECT `id` AS enumId, `file_guid` AS fileGuid, `seg_guid` AS segGuid, `seg_id` AS segId, `create_time` AS createTime, `update_time` AS updateTime, `source_name` AS sourceName, `crc32`, `size` FROM `hydra_uofs_local_cluster_fat` WHERE `seg_guid` = #{guid}")
    GenericLocalFrame getLocalFrameByGuid0(GUID guid);
    @Select("SELECT `id`, `file_guid` AS fileGuid, `seg_guid` AS segGuid, `seg_id` AS segId, `create_time` AS createTime, `update_time` AS updateTime, `source_name` AS sourceName, `crc32`, `size` FROM `hydra_uofs_local_cluster_fat` WHERE `file_guid` = #{guid}")
    List<GenericLocalFrame> getLocalFrameByFileGuid0(GUID guid );

    default List<LocalFrame> getLocalFrameByFileGuid(GUID guid){
        List< LocalFrame > localFrames = new ArrayList<>();
        List<GenericLocalFrame> frames = this.getLocalFrameByFileGuid0(guid);
        for (LocalFrame frame : frames){
            frame.setLocalFrameManipulator(this);
            localFrames.add( frame );
        }
        return localFrames;
    }
}
