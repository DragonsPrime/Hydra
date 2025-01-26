package com.pinecone.hydra.umb;

import java.io.IOException;

import com.pinecone.framework.util.UnitHelper;
import com.pinecone.hydra.umc.msg.ArchBytesTransferMessage;
import com.pinecone.hydra.umc.msg.ArchStreamTransferMessage;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;


public class UlfPackageMessageEncoder implements UMCPackageMessageEncoder {
    protected ExtraHeadCoder        mExtraHeadCoder     ;

    public UlfPackageMessageEncoder ( ExtraHeadCoder extraHeadCoder ) {
        this.mExtraHeadCoder = extraHeadCoder;
    }

    @Override
    public byte[] encode( UMCMessage message ) throws IOException {
        if ( message.evinceTransferMessage() != null ) {
            UMCHead.EncodePair pair = message.getHead().bytesEncode( this.mExtraHeadCoder );
            byte[] headBuf = pair.getBytes();

            if ( message instanceof ArchStreamTransferMessage ) {
                ArchStreamTransferMessage transferMessage = (ArchStreamTransferMessage) message;

                byte[] bytes = transferMessage.getBody().readAllBytes();

                return (byte[]) UnitHelper.mergeArr( headBuf, bytes );
            }
            else if ( message instanceof ArchBytesTransferMessage ) {
                ArchBytesTransferMessage transferMessage = (ArchBytesTransferMessage) message;

                byte[] bytes = transferMessage.getBody();

                return (byte[]) UnitHelper.mergeArr( headBuf, bytes );
            }
        }
        else if ( message.evinceInformMessage() != null ) {
            UMCHead.EncodePair pair = message.getHead().bytesEncode( this.mExtraHeadCoder );
            return pair.getBytes();
        }

        throw new IllegalArgumentException( "Type of `UMCMessage` [ " + message.getClass().getSimpleName() + " ] is not supported." );
    }
}
