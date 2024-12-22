package com.pinecone.hydra.bucket.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.bucket.entity.GenericSite;
import com.pinecone.hydra.storage.bucket.entity.Site;
import com.pinecone.hydra.storage.bucket.source.SiteManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@IbatisDataAccessObject
public interface SiteMapping extends SiteManipulator {
    @Insert("INSERT INTO `hydra_ucdn_sites` (`site_name`, `create_time`, `site_guid`, `mount_point_guid`) VALUES (#{siteName}, #{createTime}, #{siteGuid}, #{mountPointGuid})")
    void insert(Site site );

    @Delete("DELETE FROM `hydra_ucdn_sites` WHERE `site_guid` = #{siteGuid}")
    void remove( GUID siteGuid );

    @Delete("DELETE FROM `hydra_ucdn_sites` WHERE `site_name` = #{siteName}")
    void removeByName( String siteName );

    @Select("SELECT `id`, `site_name` AS siteName, `create_time` AS createTime, `site_guid` AS siteGuid, `mount_point_guid` AS mountPointGuid FROM `hydra_ucdn_sites` WHERE `site_guid` = #{siteGuid}")
    GenericSite querySite(GUID siteGuid );

    @Select("SELECT `id`, `site_name` AS siteName, `create_time` AS createTime, `site_guid` AS siteGuid, `mount_point_guid` AS mountPointGuid FROM `hydra_ucdn_sites` WHERE site_name = #{siteName}")
    GenericSite querySiteByName( String siteName );

    default List<Site> listSite(){
        List<GenericSite> genericSites = this.listSite0();
        return new ArrayList<>(genericSites);
    }

    @Select("SELECT `id`, `site_name` AS siteName, `create_time` AS createTime, `site_guid` AS siteGuid, `mount_point_guid` AS mountPointGuid FROM `hydra_ucdn_sites`")
    List<GenericSite> listSite0();
}
