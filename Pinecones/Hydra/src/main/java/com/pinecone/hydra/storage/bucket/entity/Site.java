package com.pinecone.hydra.storage.bucket.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

import java.time.LocalDateTime;

public interface Site extends Pinenut {
    long getEnumId();
    void setEnumId( long enumId);

    String getSiteName();
    void setSiteName( String siteName );

    LocalDateTime getCreateTime();
    void setCreateTime( LocalDateTime createTime );

    GUID getSiteGuid();
    void setSiteGuid( GUID siteGuid );

    GUID getMountPointGuid();
    void setMountPointGuid( GUID mountPointGuid );
}
