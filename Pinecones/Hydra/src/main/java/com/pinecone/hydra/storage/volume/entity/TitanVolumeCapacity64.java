package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.source.VolumeCapacityManipulator;

public class TitanVolumeCapacity64 implements VolumeCapacity64 {
    private GUID                        volumeGuid;
    private long                        definitionCapacity;
    private long                        usedSize;
    private long                        quotaCapacity;
    private VolumeManager               volumeManager;
    private VolumeCapacityManipulator   volumeCapacityManipulator;

    public TitanVolumeCapacity64(VolumeManager volumeManager, VolumeCapacityManipulator volumeCapacityManipulator ){
        this.volumeManager = volumeManager;
        this.volumeCapacityManipulator = volumeCapacityManipulator;
    }

    public TitanVolumeCapacity64( GUID volumeGuid, long definitionCapacity, long usedSize, long quotaCapacity ){
        this.volumeGuid = volumeGuid;
        this.definitionCapacity = definitionCapacity;
        this.usedSize = usedSize;
        this.quotaCapacity = quotaCapacity;
    }


    @Override
    public Long getDefinitionCapacity() {
        return this.definitionCapacity;
    }

    @Override
    public void setDefinitionCapacity( Number definitionCapacity ) {
        this.definitionCapacity = definitionCapacity.longValue();
    }

    @Override
    public Long getUsedSize() {
        return this.usedSize;
    }

    @Override
    public GUID getVolumeGuid() {
        return this.volumeGuid;
    }

    @Override
    public void setVolumeGuid( GUID volumeGuid ) {
        this.volumeGuid = volumeGuid;
    }

    @Override
    public void setUsedSize( Number usedSize ) {
        this.usedSize = usedSize.longValue();
    }

    @Override
    public Long getQuotaCapacity() {
        return this.quotaCapacity;
    }

    @Override
    public void setQuotaCapacity( Number quotaCapacity ) {
        this.quotaCapacity = quotaCapacity.longValue();
    }
    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
