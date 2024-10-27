package com.pinecone.hydra.storage.file.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.UniformFile;

import java.time.LocalDateTime;
import java.util.TreeMap;

public interface FileNode extends ElementNode, UniformFile {
    LocalDateTime getDeletedTime();
    void setDeletedTime(LocalDateTime deletedTime);

    long getChecksum();
    void setChecksum(long checksum);

    int getParityCheck();
    void setParityCheck(int parityCheck);


    void copyValueTo(GUID destinationGuid );
    void copyTo    (GUID destinationGuid);

    FileMeta getFileMeta();
    void setFileMeta(FileMeta fileMeta);
    GUID getDataAffinityGuid();

    boolean getIsUploadSuccessful();
    void setIsUploadSuccessful( boolean isUploadSuccessful );
    TreeMap<Long, Frame> getFrames();

    @Override
    default FileNode evinceFileNode() {
        return this;
    }

    void removeFrame();

    long getPhysicalSize();
    void setPhysicalSize(long physicalSize);

    long getLogicSize();
    void setLogicSize(long logicSize);

    long getDefinitionSize();
    void setDefinitionSize(long definitionSize);

    String getCrc32Xor();
    void setCrc32Xor(String crc32Xor);

    boolean getIntegrityCheckEnable();
    void setIntegrityCheckEnable(boolean integrityCheckEnable);

    boolean getDisableCluster();
    void setDisableCluster(boolean disableCluster);
    boolean isUploadSuccess();
}
