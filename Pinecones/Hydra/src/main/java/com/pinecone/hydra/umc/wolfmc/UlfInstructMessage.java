package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.ArchUMCMessage;
import com.pinecone.hydra.umc.msg.ExtraEncode;
import com.pinecone.hydra.umc.msg.InformMessage;
import com.pinecone.hydra.umc.msg.UMCMethod;

public class UlfInstructMessage extends ArchUMCMessage implements InformMessage {
    public UlfInstructMessage( UMCMethod method, long controlBits ) {
        super( (Object) null, ExtraEncode.Iussum, method, controlBits );
    }

    public UlfInstructMessage( long controlBits ) {
        this( UMCMethod.UNDEFINED, controlBits );
    }
}
