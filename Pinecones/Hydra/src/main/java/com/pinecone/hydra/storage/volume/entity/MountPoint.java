package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public interface MountPoint extends Pinenut {
    long getEnumId();

    GUID getGuid();
    void setGuid(GUID guid);

    LocalDateTime getCreateTime();
    void setCreateTime( LocalDateTime createTime );

    LocalDateTime getUpdateTime();
    void setUpdateTime( LocalDateTime updateTime );

    String getName();
    void setName(String name);

    GUID getVolumeGuid();
    void setVolumeGuid( GUID volumeGuid );

    String getMountPoint();
    void setMountPoint( String mountPoint );
}
