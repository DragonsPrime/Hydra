package com.walnuts.sparta.uofs.console.domain.dto;

import org.springframework.web.multipart.MultipartFile;

public class UpdateObjectByChannelDTO {
    private String          volumeGuid;
    private String          destDirPath;
    private MultipartFile   object;


    public UpdateObjectByChannelDTO() {
    }

    public UpdateObjectByChannelDTO(String volumeGuid, String destDirPath, MultipartFile object) {
        this.volumeGuid = volumeGuid;
        this.destDirPath = destDirPath;
        this.object = object;
    }

    public String getVolumeGuid() {
        return volumeGuid;
    }


    public void setVolumeGuid(String volumeGuid) {
        this.volumeGuid = volumeGuid;
    }


    public String getDestDirPath() {
        return destDirPath;
    }


    public void setDestDirPath(String destDirPath) {
        this.destDirPath = destDirPath;
    }


    public MultipartFile getObject() {
        return object;
    }


    public void setObject(MultipartFile object) {
        this.object = object;
    }

    public String toString() {
        return "updateObjectDto{volumeGuid = " + volumeGuid + ", destDirPath = " + destDirPath + ", object = " + object + "}";
    }
}
