package com.pinecone.hydra.storage;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.ulf.util.id.GUIDs;

public final class StorageConstants {
    public static final String StorageVersionSignature  = "Titan";
    public static final GUID             LocalhostGUID  = GUIDs.GUID72( "0000000-000000-0000-00" );
    public static final String  DefaultVolumePath       = "09f8d9e-0002fd-0006-2c";
}
