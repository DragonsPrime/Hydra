package com.pinecone.hydra.conduct.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.conduct.entity.GenericTaskCommonData;
import com.pinecone.hydra.conduct.entity.TaskCommonData;
import com.pinecone.hydra.conduct.source.TaskCommonDataManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
@Mapper
@IbatisDataAccessObject
public interface TaskCommonDataMapper extends TaskCommonDataManipulator {
    @Insert("INSERT INTO `hydra_task_common_data` (`guid`, `create_time`, `update_time`) VALUES (#{guid},#{createTime},#{updateTime})")
    void insert(TaskCommonData taskCommonData);
    @Delete("DELETE FROM `hydra_task_common_data` WHERE `guid`=#{guid}")
    void remove(GUID guid);
    @Select("SELECT `id` AS `enumId`, `guid`, `create_time` AS createTime, `update_time` AS updateTime FROM `hydra_task_common_data` WHERE guid=#{guid}")
    GenericTaskCommonData getTaskCommonData(GUID guid);

    void update(TaskCommonData taskCommonData);
}
