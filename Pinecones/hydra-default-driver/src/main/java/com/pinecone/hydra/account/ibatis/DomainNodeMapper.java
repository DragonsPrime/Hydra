package com.pinecone.hydra.account.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.account.entity.Domain;
import com.pinecone.hydra.account.entity.Account;
import com.pinecone.hydra.account.source.DomainNodeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
@IbatisDataAccessObject
public interface DomainNodeMapper extends DomainNodeManipulator {

    @Insert("INSERT INTO `hydra_account_domain_node` (`domain_name`, `domin_guid`, `name`) VALUES (#{domainName}, #{domainGuid}, #{name})")
    void insert(Domain domain);

    @Delete("DELETE FROM `hydra_account_domain_node` WHERE `domin_guid` = #{domainGuid}")
    void remove(GUID domainGuid);

    @Select("SELECT `id`, `domain_name`, `domin_guid`, `name` FROM `hydra_account_domain_node` WHERE `domin_guid` = #{domainGuid}")
    Account queryDomain(GUID domainGuid );
}
