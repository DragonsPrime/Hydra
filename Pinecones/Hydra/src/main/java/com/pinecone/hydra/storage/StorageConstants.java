package com.pinecone.hydra.storage;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.ulf.util.guid.GUIDs;

public final class StorageConstants {
    public static final String StorageVersionSignature  = "Titan";
    public static final GUID             LocalhostGUID  = GUIDs.GUID72( "0000000-000000-0000-00" );
    public static final String  DefaultVolumePath       = "12146c0-0000ca-0000-8c";
}
