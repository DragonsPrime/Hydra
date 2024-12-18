package org.example.sparteucdnservice.infrastructure.dto;

import com.pinecone.framework.system.prototype.Pinenut;
import org.springframework.web.multipart.MultipartFile;

public class UploadDTO implements Pinenut {
    private MultipartFile   file;

    private String          version;

    private String          filePath;


    public UploadDTO() {
    }

    public UploadDTO(MultipartFile file, String version, String filePath) {
        this.file = file;
        this.version = version;
        this.filePath = filePath;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
