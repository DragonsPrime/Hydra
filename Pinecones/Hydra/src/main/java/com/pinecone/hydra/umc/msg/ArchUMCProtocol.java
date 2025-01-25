package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.json.JSONException;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;

/**
 *  Pinecone Ursus For Java UlfMCProtocol [ Wolf Uniform Message Control Protocol ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  UlfUMC Message Struct:
 *  const char* lpszSignature
 *  byteEnum    method
 *  uint32      nExtraHeadLength
 *  uint64      nBodyLength
 *  Atom*       lpjoExtraHead // JSON5 String / JSONObject
 *  Stream      bodyStream
 *  **********************************************************
 *  UlfUMC/1.1 0xFF0xFFFFFFFF0xFFFFFFFFFFFFFFFF{Key:"Val"...}
 *  **********************************************************
 *  UlfUMC/1.1 0xFF0xFFFFFFFF0xFFFFFFFFFFFFFFFF{Key:"Val"...}
 *  MsgBody
 *  **********************************************************
 */
public abstract class ArchUMCProtocol implements UMCProtocol {
    protected int           mnFrameSize    = 4096;

    protected String        mszVersion     = UMCHeadV1.ProtocolVersion;

    protected String        mszSignature   = UMCHeadV1.ProtocolSignature;

    protected UMCHead       mHead          ;

    protected OutputStream  mOutputStream  ;

    protected InputStream   mInputStream   ;

    protected Medium        mMessageSource ;

    public ArchUMCProtocol( Medium messageSource ) {
        this.mMessageSource = messageSource;
        this.mOutputStream  = this.mMessageSource.getOutputStream();
        this.mInputStream   = this.mMessageSource.getInputStream();
        this.applyMessageSource( messageSource );
    }

    @Override
    public UMCProtocol applyMessageSource( Medium medium ) {
        this.mMessageSource = medium;
        UMCHeadV1 head      = new UMCHeadV1( this.mszSignature );
        head.applyExtraHeadCoder( this.getExtraHeadCoder() );
        this.mHead          = head;
        return this;
    }

    @Override
    public Medium getMessageSource() {
        return this.mMessageSource;
    }

    @Override
    public String getVersion(){
        return this.mszVersion;
    }

    @Override
    public String getSignature() {
        return this.mszSignature;
    }

    @Override
    public UMCHead getHead() {
        return this.mHead;
    }

    @Override
    public void setHead( UMCHead head ) {
        this.mHead = head;
        this.mszSignature = head.getSignature();
    }

    @Override
    public void release() {
        this.mMessageSource.release();
        this.mHead.release();

        this.mMessageSource   = null;
        this.mszVersion       = null;
        this.mszSignature     = null;
        this.mHead            = null;
        this.mOutputStream    = null;
        this.mInputStream     = null;
    }

    public ExtraHeadCoder getExtraHeadCoder() {
        return this.mMessageSource.getMessageNode().getExtraHeadCoder();
    }

    protected void flush() throws IOException {
        this.mOutputStream.flush();
    }

    protected void sendMsgHead( UMCHead head ) throws IOException {
        this.sendMsgHead( head, true );
    }

    protected void sendMsgHead( UMCHead umcHead, boolean bFlush ) throws IOException {
        UMCHeadV1 head = (UMCHeadV1) umcHead;
        head.applyExtraHeadCoder( this.getExtraHeadCoder() );
        head.transApplyExHead();

        ByteBuffer byteBuffer = ByteBuffer.allocate( UMCHeadV1.ReadBufferSize + head.getExtraHeadLength() );
        byteBuffer.order( UMCHeadV1.BinByteOrder );

        int nBufLength = head.getSignatureLength();
        byteBuffer.put( head.getSignature().getBytes() );
        //byteBuffer.put( (byte) ' ' );
        //++nBufLength;

        byteBuffer.putInt( head.nExtraHeadLength );
        nBufLength += Integer.BYTES;

        byteBuffer.put( head.extraEncode.getByteValue() );
        nBufLength += Byte.BYTES;



        byteBuffer.putLong( head.nBodyLength );
        nBufLength += Long.BYTES;

        byteBuffer.putLong( head.nKeepAlive );
        nBufLength += Long.BYTES;

        byteBuffer.put( head.method.getByteValue() );
        nBufLength += Byte.BYTES;

        byteBuffer.putShort( head.status.getShortValue() );
        nBufLength += Short.BYTES;



        byteBuffer.putLong( head.controlBits );
        nBufLength += Long.BYTES;

        byteBuffer.putLong( head.identityId );
        nBufLength += Long.BYTES;

        byteBuffer.putLong( head.sessionId );
        nBufLength += Long.BYTES;



        if( head.extraHead == null ) {
            byteBuffer.put( Bytes.Empty );
        }
        else {
            byteBuffer.put( head.extraHead );
        }
        nBufLength += head.getExtraHeadLength();

        this.mOutputStream.write( byteBuffer.array(), 0, nBufLength );
        if( bFlush ) {
            this.mOutputStream.flush();
        }
    }

    protected UMCHead readMsgHead() throws IOException {
        int nBufSize = ArchUMCProtocol.basicHeadLength( this.mszSignature );
        byte[] buf = new byte[ nBufSize ];

        if ( this.mInputStream.read( buf ) < nBufSize ) {
            throw new StreamTerminateException("StreamEndException:[UMCProtocol] Stream is ended.");
        }

        UMCHeadV1 head = (UMCHeadV1)ArchUMCProtocol.onlyReadMsgBasicHead( buf, this.mszSignature, this.getExtraHeadCoder() );

        byte[] headBuf = new byte[ head.nExtraHeadLength ];
        if ( this.mInputStream.read( headBuf ) < head.nExtraHeadLength ) {
            throw new StreamTerminateException("[UMCProtocol] Stream is ended.");
        }

        try {
            Object jo = this.getExtraHeadCoder().getDecoder().decode( head, headBuf );
            head.setExtraHead( jo );
        }
        catch ( JSONException e ) {
            throw new IOException(" [UMCProtocol] Illegal protocol head.");
        }

        return head;
    }

    public static int basicHeadLength( String szSignature ) {
        return szSignature.length() + UMCHeadV1.StructBlockSize;
    }

    public static UMCHead onlyReadMsgBasicHead( byte[] buf, String szSignature, ExtraHeadCoder extraHeadCoder ) throws IOException {
        int nBufSize = ArchUMCProtocol.basicHeadLength( szSignature );

        if ( buf.length < nBufSize ) {
            throw new StreamTerminateException( "StreamEndException:[UMCProtocol] Stream is ended." );
        }

        int nReadAt = szSignature.length();
        if ( !Arrays.equals( buf, 0, szSignature.length(), szSignature.getBytes(), 0, szSignature.length() )  ) {
            throw new IOException( "[UMCProtocol] Illegal protocol signature." );
        }

        UMCHeadV1 head = new UMCHeadV1();
        head.applyExtraHeadCoder( extraHeadCoder );
        //nReadAt++; // For ' '


        head.nExtraHeadLength  = ByteBuffer.wrap( buf, nReadAt, Integer.BYTES ).order( UMCHeadV1.BinByteOrder ).getInt();
        nReadAt += Integer.BYTES;

        head.extraEncode       = ExtraEncode.asValue( ByteBuffer.wrap( buf, nReadAt, Byte.BYTES ).order( UMCHeadV1.BinByteOrder ).get() );
        nReadAt += Byte.BYTES;



        head.nBodyLength       = ByteBuffer.wrap( buf, nReadAt, Long.BYTES ).order( UMCHeadV1.BinByteOrder ).getLong();
        nReadAt += Long.BYTES;

        head.nKeepAlive       = ByteBuffer.wrap( buf, nReadAt, Long.BYTES ).order( UMCHeadV1.BinByteOrder ).getLong();
        nReadAt += Long.BYTES;

        head.method            = UMCMethod.values()[ buf[nReadAt] ];
        nReadAt += Byte.BYTES;

        head.status            = Status.asValue( ByteBuffer.wrap( buf, nReadAt, Short.BYTES ).order( UMCHeadV1.BinByteOrder ).getShort() );
        nReadAt += Short.BYTES;

        head.controlBits      = ByteBuffer.wrap( buf, nReadAt, Long.BYTES ).order( UMCHeadV1.BinByteOrder ).getLong();
        nReadAt += Long.BYTES;

        head.identityId       = ByteBuffer.wrap( buf, nReadAt, Long.BYTES ).order( UMCHeadV1.BinByteOrder ).getLong();
        nReadAt += Long.BYTES;

        head.sessionId        = ByteBuffer.wrap( buf, nReadAt, Long.BYTES ).order( UMCHeadV1.BinByteOrder ).getLong();
        nReadAt += Long.BYTES;

        return head;
    }
}
