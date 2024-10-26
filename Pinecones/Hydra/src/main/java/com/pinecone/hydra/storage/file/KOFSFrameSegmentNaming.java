package com.pinecone.hydra.storage.file;

import com.pinecone.framework.util.id.GUID;

public class KOFSFrameSegmentNaming implements FrameSegmentNaming {
    @Override
    public String naming( String fileName,long segId,String crc3 ){
        return String.format( "%s_seg%d_%s.frame", fileName, segId, crc3 );
    }

    @Override
    public String naming(String fileName, GUID frameGuid, int threadId) {
        return String.format( "%s_%s-%d.strip", fileName, frameGuid.toString(), threadId );
    }

}
