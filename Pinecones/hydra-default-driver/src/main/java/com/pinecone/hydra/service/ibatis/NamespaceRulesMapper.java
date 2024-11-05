package com.pinecone.hydra.service.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.GenericNamespaceRules;
import com.pinecone.hydra.service.kom.source.NamespaceRulesManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
@IbatisDataAccessObject
public interface NamespaceRulesMapper extends NamespaceRulesManipulator {
    @Insert("INSERT INTO `hydra_service_namespace_rules` (`guid`, `scope`, `name`, `description`) VALUES (#{guid},#{scope},#{name},#{description})")
    void insert(GenericNamespaceRules classificationRules);
    @Delete("DELETE FROM `hydra_service_namespace_rules` WHERE `guid`=#{guid}")
    void remove(@Param("guid")GUID guid);
    @Select("SELECT `id` AS `enumId`, `guid`, `scope`, `name`, `description` FROM `hydra_service_namespace_rules` WHERE `guid`=#{guid}")
    GenericNamespaceRules getNamespaceRules(@Param("guid")GUID guid);
    default void update(GenericNamespaceRules classificationRules){
        if ( classificationRules.getName() != null ){
            this.updateName( classificationRules.getName(), classificationRules.getGuid() );
        }
        if ( classificationRules.getScope() != null ){
            this.updateScope( classificationRules.getScope(), classificationRules.getGuid() );
        }
        if( classificationRules.getDescription() != null ){
            this.updateDescription( classificationRules.getDescription(), classificationRules.getGuid() );
        }
    }
    @Update("UPDATE `hydra_service_namespace_rules` SET `scope` = #{scope} WHERE `guid` = #{guid}")
    void updateScope( @Param("scope") String scope, GUID guid );
    @Update("UPDATE `hydra_service_namespace_rules` SET `name` = #{name} WHERE `guid` = #{guid}")
    void updateName( @Param("name") String name, GUID guid );
    @Update("UPDATE `hydra_service_namespace_rules` SET `description` = #{description} WHERE `guid` = #{guid}")
    void updateDescription( @Param("description") String description, GUID guid );
}
