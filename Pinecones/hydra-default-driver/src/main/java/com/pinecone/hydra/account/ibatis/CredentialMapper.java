package com.pinecone.hydra.account.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.entity.Credential;
import com.pinecone.hydra.account.source.CredentialManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@IbatisDataAccessObject
public interface CredentialMapper extends CredentialManipulator {
    @Insert("INSERT INTO `hydra_account_credential` (`guid`, `name`, `credential`, `create_time`, `update_time`, `type`) VALUES (#{guid},#{name},#{credential},#{createTime},#{updateTime},#{type})")
    void insert(Credential credential);

    @Delete("DELETE FROM hydra_account_credential WHERE guid = #{guid}")
    void remove(GUID credentialGuid);

    @Select("SELECT `id`, `guid`, `name`, `credential`, `create_time`, `update_time`, `type` FROM hydra_account_credential WHERE `guid` = #{guid}")
    Credential queryCredential(GUID credentialGuid );
}
