package org.example.sparteucdnservice.infrastructure.dto;

import com.pinecone.framework.system.prototype.Pinenut;
import org.springframework.web.multipart.MultipartFile;

public class UploadDTO implements Pinenut {

    private String          version;

    private String          filePath;


    public UploadDTO() {
    }

    public UploadDTO(String version, String filePath) {
        this.version = version;
        this.filePath = filePath;
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
