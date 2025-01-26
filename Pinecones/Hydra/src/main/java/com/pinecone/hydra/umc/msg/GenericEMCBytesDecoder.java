package com.pinecone.hydra.umc.msg;

import java.io.IOException;

import com.pinecone.framework.util.json.JSONException;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

public class GenericEMCBytesDecoder implements EMCBytesDecoder {
    protected boolean isQualified ( byte[] buf, String szSignature ) throws IOException {
        if ( buf.length < szSignature.length() ) { // Signature size is minimum.
            throw new StreamTerminateException( "StreamEndException:[EMCBytesDecoder] Stream is ended." );
        }

        byte[] des = szSignature.getBytes();  // UMC | UMC-C | UMC-BP
        return buf[ 4 ] ==  des[ 4 ] && buf[ 5 ] ==  des[ 5 ] && buf[ 6 ] ==  des[ 6 ];
    }

    @Override
    public UMCHead decode( byte[] buf, ExtraHeadCoder extraHeadCoder ) throws IOException {
        if ( this.isQualified( buf, UMCHeadV1.ProtocolSignature ) ) {
            return UMCHeadV1.decode( buf, UMCHeadV1.ProtocolSignature, extraHeadCoder );
        }
        else if ( this.isQualified( buf, UMCCHeadV1.ProtocolSignature ) ) {
            return UMCCHeadV1.decode( buf, UMCCHeadV1.ProtocolSignature, extraHeadCoder );
        }

        return null;
    }

    @Override
    public UMCHead decodeIntegrated( byte[] buf, ExtraHeadCoder extraHeadCoder ) throws IOException {
        UMCHead head = this.decode( buf, extraHeadCoder );

        byte[] headBuf = new byte[ head.getExtraHeadLength() ];
        int headSize = head.sizeof();
        System.arraycopy( buf, headSize, headBuf, 0, head.getExtraHeadLength() );

        if ( buf.length < head.getExtraHeadLength() ) {
            throw new StreamTerminateException("[UMCProtocol] Buffer is not long enough.");
        }

        try {
            Object jo = extraHeadCoder.getDecoder().decode( head, headBuf );
            AbstractUMCHead.setExtraHeadExplicitly( head, jo );
        }
        catch ( JSONException e ) {
            throw new IOException(" [UMCProtocol] Illegal protocol head.");
        }

        return head;
    }
}
