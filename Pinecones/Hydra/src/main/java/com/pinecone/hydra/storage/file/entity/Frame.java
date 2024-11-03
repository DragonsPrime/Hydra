package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;

public interface Frame extends Pinenut {
    long getEnumId();

    void setEnumId(long enumId);

    GUID getFileGuid();
    void setFileGuid(GUID fileGuid);

    GUID getSegGuid();
    void setSegGuid(GUID segGuid);

    long getSegId();
    void setSegId(long segId);

    String getCrc32();
    void setCrc32(String crc32);

    long getSize();
    void setSize(long size);
    void save();
    void remove();

    default LocalFrame evinceLocalFrame(){
        return null;
    }
    default RemoteFrame evinceRemoteFrame(){
        return null;
    }
}
