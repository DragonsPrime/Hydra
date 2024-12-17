package com.pinecone.hydra.storage.file;

import com.pinecone.framework.system.prototype.Pinenut;

import java.util.zip.CRC32;

public class Verification implements Pinenut {
    private CRC32 crc32;

    private long checksum;

    private int parityCheck;


    public Verification() {
    }

    public Verification(CRC32 crc32, long checksum, int parityCheck) {
        this.crc32 = crc32;
        this.checksum = checksum;
        this.parityCheck = parityCheck;
    }


    public CRC32 getCrc32() {
        return crc32;
    }


    public void setCrc32(CRC32 crc32) {
        this.crc32 = crc32;
    }


    public long getChecksum() {
        return checksum;
    }


    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }


    public int getParityCheck() {
        return parityCheck;
    }


    public void setParityCheck(int parityCheck) {
        this.parityCheck = parityCheck;
    }

    public String toString() {
        return "Verification{crc32 = " + crc32 + ", checksum = " + checksum + ", parityCheck = " + parityCheck + "}";
    }
}
