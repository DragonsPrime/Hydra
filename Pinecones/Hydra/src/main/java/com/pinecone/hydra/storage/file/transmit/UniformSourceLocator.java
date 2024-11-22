package com.pinecone.hydra.storage.file.transmit;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;

public class UniformSourceLocator implements Pinenut {
    private String volumeGuid;

    public UniformSourceLocator() {
    }

    public UniformSourceLocator(String volumeGuid, String sourceName) {
        this.volumeGuid = volumeGuid;
    }

    public String getVolumeGuid() {
        return volumeGuid;
    }


    public void setVolumeGuid(String volumeGuid) {
        this.volumeGuid = volumeGuid;
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
