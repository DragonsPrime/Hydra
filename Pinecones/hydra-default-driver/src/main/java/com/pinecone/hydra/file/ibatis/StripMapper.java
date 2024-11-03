package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.FileMeta;
import com.pinecone.hydra.storage.file.entity.GenericStrip;
import com.pinecone.hydra.storage.file.entity.Strip;
import com.pinecone.hydra.storage.file.source.StripManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
@IbatisDataAccessObject
public interface StripMapper extends StripManipulator {
    @Insert("INSERT INTO `hydra_uofs_local_strip` (`strip_guid` , `seg_guid`, `create_time`, `update_time`, `source_name`, `size`, `file_start_offset`, `definition_size`) VALUES (#{stripGuid},#{segGuid},#{createTime},#{updateTime},#{sourceName},#{size},#{fileStartOffset},#{definitionSize})")
    void insert( Strip strip );
    @Delete("DELETE FROM `hydra_uofs_local_strip` WHERE `strip_guid` = #{guid}")
    void remove( GUID guid );
    @Select("SELECT `id` AS enumId, `strip_guid` AS stripGuid, `seg_guid` AS segGuid, `create_time` AS createTime, `update_time` AS updateTime, `source_name` AS sourseName, `size`, `file_start_offset` AS fileStartOffset, `definition_size` AS definitionSize FROM `hydra_uofs_local_strip` WHERE `strip_guid` = #{guid}")
    GenericStrip getStrip(GUID guid);
}
