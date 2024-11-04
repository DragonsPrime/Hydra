package com.pinecone.hydra.umc.msg;

import java.util.Map;

public abstract class ArchPutMessage extends ArchUMCMessage {
    public ArchPutMessage( UMCHead head ) {
        super( head );
    }

    public ArchPutMessage( Map<String,Object > joExHead , long controlBits ) {
        super( joExHead, UMCMethod.PUT, controlBits );
    }

    public ArchPutMessage( Object protoExHead, long controlBits ) {
        super( protoExHead, UMCMethod.PUT, controlBits );
    }

    public ArchPutMessage( Map<String,Object > joExHead ) {
        super( joExHead, UMCMethod.PUT );
    }

    public ArchPutMessage( Object protoExHead ) {
        super( protoExHead, UMCMethod.PUT );
    }

    @Override
    public long        getMessageLength(){
        return UMCHead.HeadBlockSize + this.mHead.getExtraHeadLength();
    }
}
