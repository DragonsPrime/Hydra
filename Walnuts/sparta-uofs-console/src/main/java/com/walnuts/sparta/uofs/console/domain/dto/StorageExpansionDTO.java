package com.walnuts.sparta.uofs.console.domain.dto;

import com.pinecone.framework.system.prototype.Pinenut;

public class StorageExpansionDTO implements Pinenut {
    public String logicGuid;

    public String childGuid;


    public StorageExpansionDTO() {
    }

    public StorageExpansionDTO(String logicGuid, String childGuid) {
        this.logicGuid = logicGuid;
        this.childGuid = childGuid;
    }


    public String getLogicGuid() {
        return logicGuid;
    }


    public void setLogicGuid(String logicGuid) {
        this.logicGuid = logicGuid;
    }


    public String getChildGuid() {
        return childGuid;
    }


    public void setChildGuid(String childGuid) {
        this.childGuid = childGuid;
    }

    public String toString() {
        return "StorageExpansionDTO{logicGuid = " + logicGuid + ", physicalGuid = " + childGuid + "}";
    }
}
