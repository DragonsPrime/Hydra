package com.pinecone.hydra.storage;

public class TitanStorageNaming implements StorageNaming{
    @Override
    public String naming(String objectName, String crc32) {
        return String.format( "%s_%s.storage", objectName, crc32 ); // TODO! CONST
    }
}
