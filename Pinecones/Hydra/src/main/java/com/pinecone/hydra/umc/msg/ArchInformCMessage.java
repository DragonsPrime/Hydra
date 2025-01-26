package com.pinecone.hydra.umc.msg;

import java.util.Map;

public abstract class ArchInformCMessage extends ArchUMCMessage implements InformMessage {
    public static UMCCHead newUMCCHead( Object exHead ) {
        UMCCHeadV1 head = new UMCCHeadV1();
        head.setExtraHead( exHead );
        return head;
    }

    public static UMCCHead newUMCCHead( Map<String,Object > joExHead ) {
        UMCCHeadV1 head = new UMCCHeadV1();
        head.applyExHead( joExHead );
        return head;
    }

    public ArchInformCMessage( UMCCHead head ) {
        super( head );
    }

    public ArchInformCMessage( Map<String,Object > joExHead ) {
        this( ArchInformCMessage.newUMCCHead( joExHead ) );
    }

    public ArchInformCMessage( Object protoExHead ) {
        this( ArchInformCMessage.newUMCCHead( protoExHead ) );
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
