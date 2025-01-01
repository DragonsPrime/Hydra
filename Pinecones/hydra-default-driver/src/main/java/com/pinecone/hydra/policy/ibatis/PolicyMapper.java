package com.pinecone.hydra.policy.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.policy.entity.Policy;
import com.pinecone.hydra.storage.policy.source.PolicyManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@IbatisDataAccessObject
public interface PolicyMapper extends PolicyManipulator {
    @Insert("INSERT INTO `hydra_uofs_policy` (`policy_name`, `policy_guid`, `policy_desc`) VALUES (#{policyName}, #{policyGuid}, #{policyDesc})")
    void insert(Policy policy);
    @Delete("DELETE FROM `hydra_uofs_policy` WHERE `policy_guid` = #{policyGuid}")
    void remove(GUID policyGuid);
    @Select("SELECT `id`, `policy_name` AS policyName, `policy_guid` AS policyGuid, `policy_desc` AS policyDesc FROM hydra_uofs_policy")
    Policy queryPolicy( GUID policyGuid );
}
