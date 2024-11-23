package com.walnut.sparta.services.dto;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.summer.multiparts.MultipartFile;

public class updateObjectDto implements Pinenut {
    private MultipartFile       object;
    private String              path;
    private String              volumeGuid;


    public updateObjectDto() {
    }

    public updateObjectDto(MultipartFile object, String path, String volumeGuid) {
        this.object = object;
        this.path = path;
        this.volumeGuid = volumeGuid;
    }

    public MultipartFile getObject() {
        return object;
    }


    public void setObject(MultipartFile object) {
        this.object = object;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getVolumeGuid() {
        return volumeGuid;
    }


    public void setVolumeGuid(String volumeGuid) {
        this.volumeGuid = volumeGuid;
    }

    public String toString() {
        return "updateObjectDto{object = " + object + ", path = " + path + ", volumeGuid = " + volumeGuid + "}";
    }
}
