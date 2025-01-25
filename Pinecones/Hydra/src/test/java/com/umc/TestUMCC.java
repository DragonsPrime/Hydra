package com.umc;

import java.nio.ByteBuffer;

import com.pinecone.Pinecone;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.umc.msg.UMCCHead;
import com.pinecone.hydra.umc.msg.UMCCHeadV1;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.msg.extra.GenericExtraHeadCoder;

public class TestUMCC {
    public static void testUMCC() throws Exception {
        UMCCHeadV1 head = new UMCCHeadV1();

        ExtraHeadCoder coder = new GenericExtraHeadCoder();


        head.setBodyLength( 136 );
        head.setControlBits( 512 );
        head.applyExtraHeadCoder( coder );
        head.setExtraHead( new JSONMaptron( "{k:123, k1: abcdefg}" ) );

        UMCCHeadV1.EncodePair pair = UMCCHeadV1.encode( head, coder );
        ByteBuffer buffer = pair.byteBuffer;


        Debug.redf( head, pair.bufLength );


        UMCCHead dec = UMCCHeadV1.decode( buffer.array(), head.getSignature(), coder );
        Debug.bluef( dec );
    }

    public static void main( String[] args ) throws Exception {
        //String szJson = FileUtils.readAll("J:/120KWordsPhonetics.json5");
        Pinecone.init( (Object...cfg )->{

            TestUMCC.testUMCC();


            return 0;
        }, (Object[]) args );
    }
}
