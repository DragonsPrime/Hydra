package com.pinecone.hydra.policy.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.policy.entity.Policy;
import com.pinecone.hydra.storage.policy.source.PolicyFileMappingManipulator;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PolicyFileMappingMapper extends PolicyFileMappingManipulator {
    @Insert("INSERT INTO `hydra_uofs_file_policy_mapping` (`file_path`, `policy_guid`) VALUES (#{filePath}, #{policyGuid})")
    void insert(@Param("policyGuid" ) GUID policyGuid, @Param("filePath") String filePath);

    @Delete("DELETE FROM hydra_uofs_file_policy_mapping WHERE policy_guid = #{policyGuid} AND file_path = #{filePath}")
    void remove(@Param("policyGuid") GUID policyGuid, @Param("filePath") String filePath);

    @Select("SELECT policy_guid FROM hydra_uofs_file_policy_mapping WHERE file_path = #{filePath}")
    List<GUID> queryPolicy(@Param("filePath") String filePath );
}
