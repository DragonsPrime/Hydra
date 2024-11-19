package com.pinecone.hydra.storage.file.transmit;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import org.apache.tomcat.util.modeler.ParameterInfo;

public class SourceJson implements Pinenut {
    private String volumeGuid;
    private String sourceName;

    public SourceJson() {
    }

    public SourceJson(String volumeGuid, String sourceName) {
        this.volumeGuid = volumeGuid;
        this.sourceName = sourceName;
    }

    public String getVolumeGuid() {
        return volumeGuid;
    }


    public void setVolumeGuid(String volumeGuid) {
        this.volumeGuid = volumeGuid;
    }


    public String getSourceName() {
        return sourceName;
    }


    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
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
