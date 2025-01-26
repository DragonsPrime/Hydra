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
        UMCHeadV1.EncodePair encodePair = UMCHeadV1.encode( umcHead, this.getExtraHeadCoder() );

        this.mOutputStream.write( encodePair.byteBuffer.array(), 0, encodePair.bufLength );
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
        return UMCHeadV1.decode( buf, szSignature, extraHeadCoder );
    }
}
