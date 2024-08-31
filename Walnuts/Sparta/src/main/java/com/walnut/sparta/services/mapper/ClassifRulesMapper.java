package com.walnut.sparta.services.mapper;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.tree.GenericClassificationRules;
import com.pinecone.hydra.service.tree.source.ClassifRulesManipulator;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface ClassifRulesMapper extends ClassifRulesManipulator {
    @Insert("INSERT INTO `hydra_service_classif_rules` (`guid`, `scope`, `name`, `description`) VALUES (#{guid},#{scope},#{name},#{description})")
    void insert(GenericClassificationRules classificationRules);
    @Delete("DELETE FROM `hydra_service_classif_rules` WHERE `guid`=#{guid}")
    void remove(@Param("guid")GUID guid);
    @Select("SELECT `id`, `guid`, `scope`, `name`, `description` FROM `hydra_service_classif_rules` WHERE `guid`=#{guid}")
    GenericClassificationRules getClassifRules(@Param("guid")GUID guid);
    void update(GenericClassificationRules classificationRules);
}
