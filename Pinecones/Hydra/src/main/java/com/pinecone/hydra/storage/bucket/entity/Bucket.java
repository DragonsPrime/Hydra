package com.pinecone.hydra.storage.bucket.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public interface Bucket extends Pinenut {
    int getEnumId();

    String getBucketName();
    void setBucketName( String bucketName );

    GUID getBucketGuid();
    void setBucketGuid( GUID bucketGuid );

    GUID getUserGuid();
    void setUserGuid( GUID userGuid );

    String getMountPoint();
    void setMountPoint( String mountPoint );

    LocalDateTime  getCreateTime();
    void setCreateTime( LocalDateTime createTime );
}
