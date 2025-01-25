package com.pinecone.hydra.umc.msg;

import java.util.Map;

public abstract class ArchInformCMessage extends ArchUMCMessage {
    private static UMCCHead newUMCCHead( Object exHead ) {
        UMCCHeadV1 head = new UMCCHeadV1();
        head.onlySetExtraHead( exHead );
        return head;
    }

    public ArchInformCMessage( UMCCHead head ) {
        super( head );
    }

    public ArchInformCMessage( Map<String,Object > joExHead ) {
        super( ArchInformCMessage.newUMCCHead( joExHead ) );
    }

    public ArchInformCMessage( Object protoExHead ) {
        super( ArchInformCMessage.newUMCCHead( protoExHead ) );
    }

    @Override
    public long        getMessageLength(){
        return UMCCHeadV1.HeadBlockSize + this.mHead.getExtraHeadLength();
    }

    @Override
    public UMCCHead getHead() {
        return (UMCCHead) super.getHead();
    }
}
