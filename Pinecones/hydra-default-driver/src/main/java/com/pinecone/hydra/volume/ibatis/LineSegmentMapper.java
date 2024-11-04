package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.source.LineSegmentManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@IbatisDataAccessObject
public interface LineSegmentMapper extends LineSegmentManipulator {
    @Insert("INSERT INTO `hydra_volume_line_segment` (`id_min`, `id_max`, `volume_guid`) VALUES ( #{idMin}, #{idMax}, #{volumeGuid} )")
    void insert(@Param("idMin") int idMin, @Param("idMax") int idMax, @Param("volumeGuid") GUID volumeGuid );
    @Select("SELECT `volume_guid` FROM `hydra_volume_line_segment` WHERE id > id_min AND id < id_max")
    GUID getVolumeGuid( int id );
    @Delete("DELETE FROM hydra_volume_line_segment WHERE id_min = #{idMin} AND id_max = #{idMax}")
    void delete( @Param("idMin") int idMin, @Param("idMax") int idMax );
}
