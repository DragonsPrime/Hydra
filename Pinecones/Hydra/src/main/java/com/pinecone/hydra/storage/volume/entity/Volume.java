package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public interface Volume extends Pinenut {
    long getEnumId();

    GUID getGuid();
    void setGuid(GUID guid);

    LocalDateTime getCreateTime();
    void setCreateTime( LocalDateTime createTime );

    LocalDateTime getUpdateTime();
    void setUpdateTime( LocalDateTime updateTime );

    String getName();
    void setName(String name);

    String getType();
    void setType( String type );

    String getExtConfig();
    void setExtConfig( String extConfig );

    VolumeCapacity64 getVolumeCapacity();
    void setVolumeCapacity( VolumeCapacity64 volumeCapacity );
}
