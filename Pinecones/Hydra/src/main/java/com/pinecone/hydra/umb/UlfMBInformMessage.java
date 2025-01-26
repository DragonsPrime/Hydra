package com.pinecone.hydra.umb;

import com.pinecone.hydra.umc.msg.AbstractUMCHead;
import com.pinecone.hydra.umc.msg.ArchUMCMessage;
import com.pinecone.hydra.umc.msg.ExtraEncode;
import com.pinecone.hydra.umc.msg.InformMessage;
import com.pinecone.hydra.umc.msg.UMCCHeadV1;
import com.pinecone.hydra.umc.msg.UMCHead;

import java.util.Map;

public class UlfMBInformMessage extends ArchUMCMessage implements InformMessage {
    public static UMCHead newUMCHead( Object exHead ) {
        UMBPHeadV1 head = new UMBPHeadV1();
        head.setExtraHead( exHead );
        head.setExtraEncode( ExtraEncode.Prototype );
        return head;
    }

    public static UMCHead newUMCHead( Map<String,Object > joExHead ) {
        UMBPHeadV1 head = new UMBPHeadV1();
        head.applyExHead( joExHead );
        return head;
    }

    public static UMCHead newUMCHead( Object exHead, long controlBits ) {
        UMCCHeadV1 head = UlfMBInformMessage.newUMCHead( controlBits );
        head.setExtraHead( exHead );
        head.setExtraEncode( ExtraEncode.Prototype );
        return head;
    }

    public static UMCHead newUMCHead( Map<String,Object > joExHead, long controlBits ) {
        UMCCHeadV1 head = UlfMBInformMessage.newUMCHead( controlBits );
        head.applyExHead( joExHead );
        return head;
    }

    public static UMCCHeadV1 newUMCHead( long controlBits ) {
        UMCCHeadV1 head = new UMCCHeadV1();
        head.setControlBits( controlBits );
        return head;
    }



    public UlfMBInformMessage( UMCHead head ) {
        super( head );
    }

    public UlfMBInformMessage( Map<String,Object > joExHead ) {
        this( UlfMBInformMessage.newUMCHead( joExHead ) );
    }

    public UlfMBInformMessage( Object protoExHead ) {
        this( UlfMBInformMessage.newUMCHead( protoExHead ) );
    }


    public UlfMBInformMessage( Map<String,Object > joExHead, long controlBits ) {
        this( UlfMBInformMessage.newUMCHead( joExHead, controlBits ) );
    }

    public UlfMBInformMessage( Object protoExHead, long controlBits ) {
        this( UlfMBInformMessage.newUMCHead( protoExHead, controlBits ) );
    }


    public UlfMBInformMessage( long controlBits ) {
        this( UlfMBInformMessage.newUMCHead( controlBits ) );
    }


    @Override
    public long        getMessageLength(){
        if ( this.mHead instanceof UMBPHeadV1 ) {
            return UMBPHeadV1.HeadBlockSize + this.mHead.getExtraHeadLength();
        }

        return UMCCHeadV1.HeadBlockSize + this.mHead.getExtraHeadLength();
    }

    @Override
    public UMCHead getHead() {
        return super.getHead();
    }

}
