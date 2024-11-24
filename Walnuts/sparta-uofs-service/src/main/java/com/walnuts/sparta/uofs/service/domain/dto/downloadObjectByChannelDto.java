package com.walnuts.sparta.uofs.service.domain.dto;

public class downloadObjectByChannelDto {
    private String destDirPath;
    private String targetPath;

    public downloadObjectByChannelDto() {
    }

    public downloadObjectByChannelDto(String destDirPath, String targetPath) {
        this.destDirPath = destDirPath;
        this.targetPath = targetPath;
    }


    public String getDestDirPath() {
        return destDirPath;
    }


    public void setDestDirPath(String destDirPath) {
        this.destDirPath = destDirPath;
    }


    public String getTargetPath() {
        return targetPath;
    }


    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String toString() {
        return "downloadObjectByChannelDto{destDirPath = " + destDirPath + ", targetPath = " + targetPath + "}";
    }
}
