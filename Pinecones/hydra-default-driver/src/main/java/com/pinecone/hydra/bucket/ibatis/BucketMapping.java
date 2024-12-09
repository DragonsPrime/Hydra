package com.pinecone.hydra.bucket.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.bucket.entity.Bucket;
import com.pinecone.hydra.storage.bucket.source.BucketManipulator;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BucketMapping extends BucketManipulator {
    @Insert("INSERT INTO hydra_uos_bucket (`bucket_name`, `create_Time`, `bucket_guid`, `user_guid`, `mount_point`) VALUES (#{bucketName},#{createTime},#{bucketGuid},#{userGuid},#{mountPoint})")
    void insert( Bucket bucket );

    @Delete("DELETE From hydra_uos_bucket WHERE `bucket_guid` = #{bucketGuid}")
    void remove( GUID bucketGuid );

    @Select("SELECT `id`, `bucket_name` AS bucketName, `create_Time` AS createTime, `bucket_guid` AS bucketGuid, `user_guid` AS userGuid, `mount_point` AS mountPoint FROM hydra_uos_bucket WHERE `bucket_guid` = #{bucketGuid}")
    Bucket queryBucketByBucketGuid( GUID bucketGuid );

    @Select("SELECT `id`, `bucket_name` AS bucketName, `create_Time` AS createTime, `bucket_guid` AS bucketGuid, `user_guid` AS userGuid, `mount_point` AS mountPoint FROM hydra_uos_bucket WHERE `user_guid` = #{userGuid} ")
    List<Bucket> queryBucketsByUserGuid(GUID userGuid );
}
