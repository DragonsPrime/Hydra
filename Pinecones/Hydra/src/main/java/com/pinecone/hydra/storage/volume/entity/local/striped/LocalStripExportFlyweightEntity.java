package com.pinecone.hydra.storage.volume.entity.local.striped;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalStripExportFlyweightEntity implements StripExportFlyweightEntity {
    private int                         jobNum;
    private int                         jobCode;
    private StripLockEntity             lockEntity;


    public LocalStripExportFlyweightEntity(){}
    public LocalStripExportFlyweightEntity( int jobNum, int jobCode, StripLockEntity lockEntity ){
        this.jobNum                     = jobNum;
        this.jobCode                    = jobCode;
        this.lockEntity                 = lockEntity;
    }

    public LocalStripExportFlyweightEntity( StripLockEntity lockEntity ){
        this.lockEntity = lockEntity;
    }


    @Override
    public int getJobNum() {
        return this.jobNum;
    }

    @Override
    public void setJobNum(int jobNum) {
        this.jobNum = jobNum;
    }

    @Override
    public int getJobCode() {
        return this.jobCode;
    }

    @Override
    public void setJobCode(int jobCode) {
        this.jobCode = jobCode;
    }

    @Override
    public StripLockEntity getLockEntity() {
        return this.lockEntity;
    }

    @Override
    public void setLockEntity(StripLockEntity lockEntity) {
        this.lockEntity = lockEntity;
    }

}
