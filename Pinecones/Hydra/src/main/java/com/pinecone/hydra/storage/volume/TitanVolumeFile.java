package com.pinecone.hydra.storage.volume;

import com.pinecone.hydra.storage.CheckedFile;

public class TitanVolumeFile implements VolumeFile{
    private String name;
    private Number size;
    private long   checksum;
    private int   parityCheck;

    public TitanVolumeFile( String name, Number size ){
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Number size() {
        return this.size;
    }

    @Override
    public long getChecksum() {
        return this.checksum;
    }

    @Override
    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

    @Override
    public int getParityCheck() {
        return this.parityCheck;
    }

    @Override
    public void setParityCheck(int parityCheck) {
        this.parityCheck = parityCheck;
    }

    @Override
    public VolumeFile fromUniformFile(CheckedFile file) {
        this.name = file.getName();
        this.size = file.size();
        this.parityCheck = file.getParityCheck();
        this.checksum = file.getChecksum();
        return this;
    }
}
