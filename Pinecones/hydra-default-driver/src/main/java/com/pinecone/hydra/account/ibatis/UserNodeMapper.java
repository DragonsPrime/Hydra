package com.pinecone.hydra.account.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.entity.Account;
import com.pinecone.hydra.account.source.UserNodeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@IbatisDataAccessObject
public interface UserNodeMapper extends UserNodeManipulator {
    @Insert("INSERT INTO `hydra_account_user_node` (`guid`, `user_name`, `nick_name`, `kernel_credential`, `credential_guid`, `kernel_group_type`, `create_time`, `update_time`) VALUES (#{guid},#{name},#{nickName},#{kernelCredential},#{credentialGuid},#{kernelGroupType},#{createTime},#{updateTime})")
    void insert(Account account);

    @Delete("DELETE FROM `hydra_account_user_node` WHERE `guid` = #{userGuid}")
    void remove(GUID userGuid);

    @Select("SELECT `id`, `guid`, `user_name` AS name, `nick_name` AS nickName, `kernel_credential` AS kernelCredential, `credential_guid` AS credentialGuid, `kernel_group_type` AS kernelGroupType, `create_time` AS createTime, `update_time` AS updateTime FROM hydra_account_user_node WHERE `guid` = #{userGuid}")
    Account queryUser(GUID userGuid );
    @Select("SELECT `guid` FROM hydra_account_user_node WHERE `user_name` = #{name}")
    List<GUID > getGuidsByName(String name );
    @Select("SELECT `guid` FROM hydra_account_user_node WHERE `user_name` = #{name} AND guid = #{guid}")
    List<GUID > getGuidsByNameID(@Param("name") String name, @Param("guid") GUID guid );
}
