package com.walnuts.sparta.uofs.console.domain.dto;

import com.pinecone.framework.system.prototype.Pinenut;

public class LogicVolumeDTO implements Pinenut {
    private String name;

    private long definitionCapacity;

    private String extConfig;


    public LogicVolumeDTO() {
    }

    public LogicVolumeDTO(String name, long definitionCapacity, String extConfig) {
        this.name = name;
        this.definitionCapacity = definitionCapacity;
        this.extConfig = extConfig;
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

    public String toString() {
        return "SimpleVolumeDTO{name = " + name + ", definitionCapacity = " + definitionCapacity + ", extConfig = " + extConfig + "}";
    }
}
