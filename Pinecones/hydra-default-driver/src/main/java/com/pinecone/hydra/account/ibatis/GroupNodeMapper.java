package com.pinecone.hydra.account.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.entity.Group;
import com.pinecone.hydra.account.entity.Account;
import com.pinecone.hydra.account.source.GroupNodeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
@IbatisDataAccessObject
public interface GroupNodeMapper extends GroupNodeManipulator {
    @Insert("INSERT INTO `hydra_account_group_node` (`default_privilege_policy_guid`, `guid`, `name`) VALUES (#{defaultPrivilegePolicyGuid},#{guid},#{name})")
    void insert(Group group);

    @Delete("DELETE FROM `hydra_account_group_node` WHERE `guid` = #{groupGuid}")
    void remove(GUID groupGuid);

    @Select("SELECT `id`, `default_privilege_policy_guid`, `guid`, `name` FROM `hydra_account_group_node` WHERE `guid` = #{groupGuid}")
    Account queryGroup(GUID groupGuid );
}
