package com.pinecone.hydra.umb;

import java.io.IOException;

import com.pinecone.hydra.umc.msg.GenericEMCBytesDecoder;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCHeadV1;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

public class UMBBytesDecoder extends GenericEMCBytesDecoder {
    @Override
    public UMCHead decode( byte[] buf, ExtraHeadCoder extraHeadCoder ) throws IOException {
        UMCHead head = super.decode( buf, extraHeadCoder );
        if ( head != null ) {
            return head;
        }

        if ( this.isQualified( buf, UMBPHeadV1.ProtocolSignature ) ) {
            return UMBPHeadV1.decode( buf, UMBPHeadV1.ProtocolSignature, extraHeadCoder );
        }

        return null;
    }
}
