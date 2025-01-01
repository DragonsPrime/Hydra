package com.pinecone.hydra.conduct.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.conduct.entity.GenericTaskNode;
import com.pinecone.hydra.conduct.entity.TaskNode;
import com.pinecone.hydra.conduct.source.TaskNodeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
@IbatisDataAccessObject
public interface TaskNodeMapper extends TaskNodeManipulator {
    @Insert("INSERT INTO `hydra_task_task_node` (`guid`, `name`) VALUES (#{guid},#{name})")
    void insert(TaskNode taskNode);
    @Delete("DELETE FROM `hydra_task_task_node` WHERE `guid`=#{guid}")
    void remove(GUID guid);
    @Select("SELECT `id` AS `enumId`, `guid`, `name` FROM `hydra_task_task_node` WHERE `guid`=#{guid}")
    GenericTaskNode getTaskNode(GUID guid);

    void update(TaskNode taskNode);
    @Select("SELECT `guid` FROM `hydra_task_task_node` WHERE `name`=#{name}")
    List<GUID> getGuidsByName(String name);
}
