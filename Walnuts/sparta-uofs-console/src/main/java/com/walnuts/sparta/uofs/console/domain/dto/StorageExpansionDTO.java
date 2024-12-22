package com.walnuts.sparta.uofs.console.domain.dto;

import com.pinecone.framework.system.prototype.Pinenut;

public class StorageExpansionDTO implements Pinenut {
    public String logicGuid;

    public String physicalGuid;


    public StorageExpansionDTO() {
    }

    public StorageExpansionDTO(String logicGuid, String physicalGuid) {
        this.logicGuid = logicGuid;
        this.physicalGuid = physicalGuid;
    }


    public String getLogicGuid() {
        return logicGuid;
    }


    public void setLogicGuid(String logicGuid) {
        this.logicGuid = logicGuid;
    }


    public String getPhysicalGuid() {
        return physicalGuid;
    }


    public void setPhysicalGuid(String physicalGuid) {
        this.physicalGuid = physicalGuid;
    }

    public String toString() {
        return "StorageExpansionDTO{logicGuid = " + logicGuid + ", physicalGuid = " + physicalGuid + "}";
    }
}
