package com.walnuts.sparta.uofs.console.domain.dto;

import com.pinecone.framework.system.prototype.Pinenut;

public class PhysicalVolumeDTO implements Pinenut {
    private String name;

    private long definitionCapacity;

    private String extConfig;

    private String mountPoint;


    public PhysicalVolumeDTO() {
    }

    public PhysicalVolumeDTO(String name, long definitionCapacity, String extConfig, String mountPoint) {
        this.name = name;
        this.definitionCapacity = definitionCapacity;
        this.extConfig = extConfig;
        this.mountPoint = mountPoint;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public long getDefinitionCapacity() {
        return definitionCapacity;
    }


    public void setDefinitionCapacity(long definitionCapacity) {
        this.definitionCapacity = definitionCapacity;
    }


    public String getExtConfig() {
        return extConfig;
    }


    public void setExtConfig(String extConfig) {
        this.extConfig = extConfig;
    }


    public String getMountPoint() {
        return mountPoint;
    }


    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public String toString() {
        return "PhysicalVolumeDTO{name = " + name + ", definitionCapacity = " + definitionCapacity + ", extConfig = " + extConfig + ", mountPoint = " + mountPoint + "}";
    }
}
