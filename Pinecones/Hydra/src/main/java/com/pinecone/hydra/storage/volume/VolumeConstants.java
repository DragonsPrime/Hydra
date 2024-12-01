package com.pinecone.hydra.storage.volume;

public final class VolumeConstants {
    public static final Number TinyFileStripSizing      =       512 * 1024L;  // 512 KB
    public static final Number SmallFileStripSizing     =  4 * 1024 * 1024L;  //   4 MB
    public static final Number MegaFileStripSizing      = 10 * 1024 * 1024L;  //  10 MB
    public static final Number DefaultStripSize         = VolumeConstants.MegaFileStripSizing;
    public static final int    StripResidentCacheAllotRatio     = 2;
    public static final String StorageObjectExtension   = ".storage";
    public static final String SqliteFileExtension      = ".db";
    public static final String PathSeparator            = "/";
}
