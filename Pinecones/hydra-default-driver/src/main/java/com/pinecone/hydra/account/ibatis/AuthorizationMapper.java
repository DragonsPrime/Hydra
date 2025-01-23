package com.pinecone.hydra.account.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.entity.Authorization;
import com.pinecone.hydra.account.source.AuthorizationManipulator;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface AuthorizationMapper extends AuthorizationManipulator {
    @Insert("INSERT INTO `hydra_account_authorization` (`guid`, `user_name`, `user_guid`, `credential_guid`, `privilege_token`, `privilege_guid`, `create_time`, `update_time`) VALUES (#{guid},#{userName},#{userGuid},#{credentialGuid},#{privilrgrToken},#{privilegeGuid},#{createTime},#{updateTime})")
    void insert(Authorization authorization);

    @Delete("DELETE FROM `hydra_account_authorization` WHERE `guid` = #{authorizationGuid}")
    void remove(GUID authorizationGuid);

    @Select("SELECT `id`, `guid`, `user_name`, `user_guid`, `credential_guid`, `privilege_token`, `privilege_guid`, `create_time`, `update_time` FROM `hydra_account_authorization` WHERE guid = #{authorizationGuid}")
    Authorization queryCredential(GUID authorizationGuid );
}
