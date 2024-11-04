package com.pinecone.hydra.umc.wolfmc;

import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import com.pinecone.hydra.umc.msg.ArchUMCProtocol;
import com.pinecone.hydra.umc.msg.UMCHead;

import java.util.List;


public class GenericUMCByteMessageDecoder extends ByteToMessageDecoder {
    private ByteBuf        cumulation;
    private ExtraHeadCoder extraHeadCoder;
    private long           byteSum;
    private long           bodyBytes;

    public GenericUMCByteMessageDecoder(ExtraHeadCoder extraHeadCoder) {
        this.extraHeadCoder = extraHeadCoder;
        this.byteSum   = -1;
        this.bodyBytes = 0;
    }

    @Override
    protected void decode( ChannelHandlerContext ctx, ByteBuf in, List<Object> out ) throws Exception {
        this.cumulation = in;

        if ( this.byteSum == -1 ) {
            int nBufSize = ArchUMCProtocol.basicHeadLength( UMCHead.ProtocolSignature );

            // Waiting for more data to arrive, and that will be enough to decode the header.
            if ( in.readableBytes() < nBufSize ) {
                return;
            }

            byte[] buf = new byte[ nBufSize ];
            in.readBytes(buf);

            UMCHead head = ArchUMCProtocol.onlyReadMsgBasicHead( buf, UMCHead.ProtocolSignature, this.extraHeadCoder );
            this.bodyBytes = head.getBodyLength();
            this.byteSum = nBufSize + head.getExtraHeadLength() + this.bodyBytes;

            if ( this.byteSum < 0 ) {
                throw new IllegalArgumentException( "Invalid byteSum calculation: " + this.byteSum );
            }
        }

        in.resetReaderIndex();

        if ( in.readableBytes() >= this.byteSum ) {
            ByteBuf completeMessage = in.readRetainedSlice((int) this.byteSum);
            ctx.fireChannelRead(completeMessage);

            this.byteSum   = -1;
            this.bodyBytes = 0;
        }

        // Waiting for more data to arrive.
//        else {
//            return;
//        }
    }

    private void resetState() {
        this.byteSum = -1;
        this.bodyBytes = 0;
    }

    @Override
    public void channelInactive( ChannelHandlerContext ctx ) throws Exception {
        super.channelInactive(ctx);
        if ( this.cumulation != null ) {
            this.cumulation.clear();
            this.cumulation.release();
            this.cumulation = null;
        }
        this.resetState();
    }
}