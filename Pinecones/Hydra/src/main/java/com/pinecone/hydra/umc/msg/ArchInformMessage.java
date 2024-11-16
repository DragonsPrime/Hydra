package com.pinecone.hydra.umc.msg;

import java.util.Map;

public abstract class ArchInformMessage extends ArchUMCMessage {
    public ArchInformMessage( UMCHead head ) {
        super( head );
    }

    public ArchInformMessage( Map<String,Object > joExHead , long controlBits ) {
        super( joExHead, UMCMethod.INFORM, controlBits );
    }

    public ArchInformMessage( Object protoExHead, long controlBits ) {
        super( protoExHead, UMCMethod.INFORM, controlBits );
    }

    public ArchInformMessage( Map<String,Object > joExHead ) {
        super( joExHead, UMCMethod.INFORM );
    }

    public ArchInformMessage( Object protoExHead ) {
        super( protoExHead, UMCMethod.INFORM );
    }

    @Override
    public long        getMessageLength(){
        return UMCHead.HeadBlockSize + this.mHead.getExtraHeadLength();
    }
}
