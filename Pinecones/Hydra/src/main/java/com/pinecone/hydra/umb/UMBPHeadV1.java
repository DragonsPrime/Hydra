package com.pinecone.hydra.umb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;

import com.pinecone.framework.system.prototype.ObjectiveBean;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.msg.AbstractUMCHead;
import com.pinecone.hydra.umc.msg.ArchUMCProtocol;
import com.pinecone.hydra.umc.msg.ExtraEncode;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.StreamTerminateException;
import com.pinecone.hydra.umc.msg.UMCCHeadV1;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCHeadV1;
import com.pinecone.hydra.umc.msg.UMCMethod;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

/**
 *  Pinecone Ursus For Java UMB [ Uniform Message Broadcast Control Transmit - Package ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  Uniform Message Control Transmit Protocol - Broadcast Package [UMC-T-BP]
 *  统一消息广播控制传输协议-小包分协议
 *  For: Simplified Message Small-Package [最小压缩邮政小包]
 *  **********************************************************
 *  According to MQ traits, in practice, if a message is received, its status should be 'OK' in principle.
 *  根据MQ特性，实践中，若收到消息状态码原则上就应该是 `OK`
 *  **********************************************************
 */
public class UMBPHeadV1 extends AbstractUMCHead implements UMBHead {
    public static final String     ProtocolVersion   = "1.1";
    public static final String     ProtocolSignature = "UMC-BP/" + UMBPHeadV1.ProtocolVersion;
    public static final int        StructBlockSize   = Integer.BYTES + Byte.BYTES;
    public static final int        HeadBlockSize     = UMBPHeadV1.ProtocolSignature.length() + UMBPHeadV1.StructBlockSize;
    public static final ByteOrder  BinByteOrder      = UMCHeadV1.BinByteOrder;
    public static final int        HeadFieldsSize    = 3;

    protected String                 szSignature                                ; // :0
    protected int                    nExtraHeadLength  = 2                      ; // :1 sizeof( int32 ) = 4
    protected ExtraEncode            extraEncode       = ExtraEncode.Undefined  ; // :2 sizeof( ExtraEncode/byte ) = 1

    protected byte[]                 extraHead         = {}                     ;
    protected Object                 dyExtraHead                                ;
    protected ExtraHeadCoder         extraHeadCoder                             ;


    public UMBPHeadV1(  ) {
        this.szSignature = UMBPHeadV1.ProtocolSignature;
        this.dyExtraHead = new LinkedTreeMap<>();
    }


    @Override
    public int sizeof() {
        return UMBPHeadV1.HeadBlockSize;
    }


    @Override
    public int fieldsSize() {
        return UMBPHeadV1.HeadFieldsSize;
    }



    @Override
    protected void setSignature            ( String signature       ) {
        this.szSignature = signature;
    }

    @Override
    protected void setBodyLength           ( long length            ) {

    }

    @Override
    public void setKeepAlive     ( long nKeepAlive        ) {

    }

    @Override
    protected void setMethod               ( UMCMethod umcMethod    ) {

    }

    @Override
    protected void setExtraEncode          ( ExtraEncode encode     ) {
        this.extraEncode = encode;
    }



    @Override
    public void setControlBits   ( long controlBits       ) {

    }

    @Override
    public void setSessionId     ( long sessionId         ) {

    }

    @Override
    public void setIdentityId    ( long identityId        ) {

    }



    @Override
    protected void setExtraHead            ( JSONObject jo          ) {
        this.dyExtraHead = jo.getMap();
    }

    @Override
    protected void setExtraHead            ( Map<String,Object > jo ) {
        this.dyExtraHead = jo;
    }

    @Override
    protected void setExtraHead            ( Object o               ) {
        this.dyExtraHead = o;
        if( o == null ) {
            this.nExtraHeadLength = 0;
        }
    }

    @Override
    protected void transApplyExHead        (                        ) {
        if ( this.dyExtraHead != null ) {
            this.extraHead         = this.extraHeadCoder.getEncoder().encode( this, this.dyExtraHead );
            this.nExtraHeadLength  = this.extraHead.length;
        }
        else {
            if( this.extraEncode == ExtraEncode.JSONString ) {
                this.extraHead  = "{}".getBytes();
            }
            else if( this.extraEncode == ExtraEncode.Prototype ) {
                this.extraHead         = null;
                this.nExtraHeadLength  = 0;
                return;
            }
            else if( this.extraEncode == ExtraEncode.Iussum ) {
                this.extraHead         = new byte[ 0 ];
                this.nExtraHeadLength  = 0;
                return;
            }
            else {
                this.dyExtraHead = this.extraHeadCoder.newExtraHead();
                this.extraHead   = this.extraHeadCoder.getEncoder().encode( this, this.dyExtraHead );
            }
        }

        this.nExtraHeadLength  = this.extraHead.length;
    }

    @Override
    protected void applyExtraHeadCoder     ( ExtraHeadCoder coder   ) {
        this.extraHeadCoder = coder;

        if( this.extraEncode == ExtraEncode.Undefined ) {
            this.extraEncode = coder.getDefaultEncode();
        }
    }



    @Override
    public void            setStatus ( Status status ) {

    }

    @Override
    public ExtraHeadCoder  getExtraHeadCoder() {
        return this.extraHeadCoder;
    }

    @Override
    public String          getSignature() {
        return this.szSignature;
    }

    @Override
    public int             getSignatureLength() {
        return this.getSignature().length();
    }

    @Override
    public UMCMethod       getMethod() {
        return UMCMethod.INFORM;
    }

    @Override
    public int             getExtraHeadLength() {
        return this.nExtraHeadLength;
    }

    @Override
    public long            getBodyLength() {
        return 0L;
    }

    @Override
    public long            getKeepAlive() {
        return -1L;
    }

    @Override
    public long            getSessionId() {
        return -1L;
    }

    @Override
    public Status          getStatus() {
        return Status.OK;
    }

    @Override
    public ExtraEncode     getExtraEncode() {
        return this.extraEncode;
    }

    @Override
    public long            getControlBits() {
        return 0;
    }

    @Override
    public long            getIdentityId() {
        return 0;
    }

    @Override
    public byte[]          getExtraHeadBytes() {
        return this.extraHead ;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Map<String, Object > evalMapExtraHead() {
        if( this.dyExtraHead instanceof Map ) {
            return (Map) this.dyExtraHead;
        }
        return ( new ObjectiveBean( this.dyExtraHead ) ).toMap();
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public Map<String, Object > getMapExtraHead() {
        if( this.dyExtraHead instanceof Map ) {
            return (Map) this.dyExtraHead;
        }
        return null;
    }

    @Override
    public Object getExtraHead() {
        return this.dyExtraHead;
    }

    @Override
    public void putExHeaderVal( String key, Object val ) throws IllegalArgumentException {
        if( this.dyExtraHead instanceof Map ) {
            this.getMapExtraHead().put( key, val );
        }
        else {
            ReflectionUtils.beanSet( this.dyExtraHead, key, val );
        }
    }

    @Override
    public Object getExHeaderVal( String key ) {
        if( this.dyExtraHead instanceof Map ) {
            return this.getMapExtraHead().get( key );
        }
        else {
            return ReflectionUtils.beanGet( this.dyExtraHead, key );
        }
    }

    @Override
    protected UMCHead applyExHead( Map<String, Object > jo      ) {
        if( !( this.dyExtraHead instanceof Map ) && this.dyExtraHead != null ) {
            throw new IllegalArgumentException( "Current extra headed is not dynamic." );
        }

        if( this.getMapExtraHead() == null || this.getMapExtraHead().size() == 0 ) {
            this.setExtraHead( jo );
        }
        else {
            if( jo.size() > this.getMapExtraHead().size() ) {
                jo.putAll( this.getMapExtraHead() );
                this.setExtraHead( jo );
            }
            else {
                this.getMapExtraHead().putAll( jo );
            }
        }
        return this;
    }

    public UMCHead receiveSet( Map<String, Object > joExtraHead ) {
        this.dyExtraHead = joExtraHead;
        return this;
    }

    @Override
    public void release() {
        // Help GC
        this.dyExtraHead = null;
    }

    @Override
    public EncodePair bytesEncode( ExtraHeadCoder extraHeadCoder ) {
        return UMBPHeadV1.encode( this, extraHeadCoder );
    }



    public static EncodePair encode( UMCHead umcHead, ExtraHeadCoder extraHeadCoder ) {
        UMBPHeadV1 head = (UMBPHeadV1) umcHead;
        head.applyExtraHeadCoder( extraHeadCoder );
        head.transApplyExHead();

        ByteBuffer byteBuffer = ByteBuffer.allocate( UMCHeadV1.ReadBufferSize + head.getExtraHeadLength() );
        byteBuffer.order( BinByteOrder );

        byteBuffer.put( head.getSignature().getBytes() );

        int nBufLength = head.getSignatureLength();
        byteBuffer.putInt( head.nExtraHeadLength );
        nBufLength += Integer.BYTES;

        byteBuffer.put( head.extraEncode.getByteValue() );
        nBufLength += Byte.BYTES;




        if( head.extraHead == null ) {
            byteBuffer.put( Bytes.Empty );
        }
        else {
            byteBuffer.put( head.extraHead );
        }
        nBufLength += head.getExtraHeadLength();

        return new EncodePair( byteBuffer, nBufLength );
    }

    public static UMCHead decode( byte[] buf, String szSignature, ExtraHeadCoder extraHeadCoder ) throws IOException {
        int nBufSize = szSignature.length() + UMBPHeadV1.StructBlockSize;

        if ( buf.length < nBufSize ) {
            throw new StreamTerminateException( "StreamEndException:[UMBPProtocol] Stream is ended." );
        }

        int nReadAt = szSignature.length();
        if ( !Arrays.equals( buf, 0, szSignature.length(), szSignature.getBytes(), 0, szSignature.length() )  ) {
            throw new IOException( "[UMBPProtocol] Illegal protocol signature." );
        }

        UMBPHeadV1 head = new UMBPHeadV1();
        head.applyExtraHeadCoder( extraHeadCoder );


        head.nExtraHeadLength  = ByteBuffer.wrap( buf, nReadAt, Integer.BYTES ).order( BinByteOrder ).getInt();
        nReadAt += Integer.BYTES;

        head.extraEncode       = ExtraEncode.asValue( ByteBuffer.wrap( buf, nReadAt, Byte.BYTES ).order( BinByteOrder ).get() );
        nReadAt += Byte.BYTES;

        return head;
    }
}
