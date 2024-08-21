package com.walnut.sparta.mapper;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.GenericServiceDescription;
import com.pinecone.hydra.unit.udsn.ServiceDescriptionManipinate;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface GenericServiceDescriptionManipinate extends ServiceDescriptionManipinate {
    @Insert("INSERT INTO `hydra_service_description` (UUID, `name`, `path`, `type`, `alias`, resource_type, service_type, create_time, update_time) VALUES (#{UUID},#{name},#{path},#{type},#{alias},#{resourceType},#{serviceType},#{createTime},#{updateTime})")
    void saveServiceDescription(GenericServiceDescription serviceDescription);
    @Delete("DELETE FROM `hydra_service_description` WHERE UUID=#{UUID}")
    void deleteServiceDescription(@Param("UUID")GUID UUID);
    void updateServiceDescription(GenericServiceDescription serviceDescription);
    @Select("SELECT `id`, UUID, `name`, `path`, `type`, `alias`, `resource_type` AS resourceType, `service_type` AS serviceType, `create_time` AS createTime, `update_time` AS updateTime FROM `hydra_service_description` WHERE UUID=#{UUID}")
    GenericServiceDescription selectServiceDescription(@Param("UUID")GUID UUID);
}
