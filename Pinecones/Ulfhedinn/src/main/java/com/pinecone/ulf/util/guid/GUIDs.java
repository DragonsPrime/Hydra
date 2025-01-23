package com.pinecone.ulf.util.guid;

import com.pinecone.framework.util.id.GuidAllocator;
import com.pinecone.ulf.util.guid.worker.WorkerIdAssigner;

public final class GUIDs {
    public static GUID64 GUID64( String s ) {
        return new GUID64( s );
    }

    public static GUID72 GUID72( String s ) {
        return new GUID72( s );
    }

    public static GUID72 Dummy72() {
        return new GUID72();
    }

    public static GuidAllocator newGuidAllocator(WorkerIdAssigner idAssigner ) {
        if( idAssigner == null ) {
            return new GenericGuidAllocator();
        }
        return new GenericGuidAllocator( idAssigner );
    }

    public static GuidAllocator newGuidAllocator() {
        return GUIDs.newGuidAllocator( null );
    }
}
