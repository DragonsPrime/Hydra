package com.pinecone.hydra.service.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.ServiceFamilyNode;
import com.pinecone.hydra.service.kom.entity.GenericCommonMeta;
import com.pinecone.hydra.service.kom.entity.Namespace;
import com.pinecone.hydra.service.kom.source.CommonDataManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
@IbatisDataAccessObject
public interface ServiceCommonDataMapper extends CommonDataManipulator {
    @Insert("INSERT INTO `hydra_service_node_common_data` (`guid`, `scenario`, primary_impl_lang, extra_information, `level`, `description`) VALUES (#{guid}, #{scenario}, #{primaryImplLang}, #{extraInformation}, #{level}, #{description})")
    void insert( ServiceFamilyNode node );

    @Insert("INSERT INTO `hydra_service_node_common_data` (`guid`, `scenario`, primary_impl_lang, extra_information, `level`, `description`) VALUES (#{metaGuid}, #{scenario}, #{primaryImplLang}, #{extraInformation}, #{level}, #{description})")
    void insertNS( Namespace node );

    @Delete("DELETE FROM `hydra_service_node_common_data` WHERE `guid`=#{guid}")
    void remove( @Param("guid")GUID guid );

    @Select("SELECT `id` AS `enumId`, `guid`, `scenario`, `primary_impl_lang` AS primaryImplLang, `extra_information` AS extraInformation, `level`, `description` FROM `hydra_service_node_common_data` WHERE `guid`=#{guid}")
    GenericCommonMeta getNodeCommonData( @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_node_common_data` SET `scenario` = #{scenario}, `primary_impl_lang` = #{primaryImplLang}, `extra_information` = #{extraInformation}, `level` = #{level}, `description` = #{description}")
    void update( ServiceFamilyNode node );

    @Update("UPDATE `hydra_service_node_common_data` SET `scenario` = #{scenario} WHERE `guid` = #{guid}")
    void updateScenario( @Param("scenario") String scenario, @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_node_common_data` SET `primary_impl_lang` = #{primaryImplLang} WHERE `guid` = #{guid}")
    void updatePrimaryImplLang( @Param("primaryImpLang") String primaryImplLang, @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_node_common_data` SET `extra_information` = #{extraInformation} WHERE `guid` = #{guid}")
    void updateExtraInformation( @Param("extraInformation") String extraInformation, @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_node_common_data` SET `level` = #{level} WHERE `guid` = #{guid}")
    void updateLevel( @Param("level") String level, @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_node_common_data` SET `description` = #{description} WHERE `guid` = #{guid}")
    void updateDescription( @Param("description") String description, @Param("guid") GUID guid );
}
