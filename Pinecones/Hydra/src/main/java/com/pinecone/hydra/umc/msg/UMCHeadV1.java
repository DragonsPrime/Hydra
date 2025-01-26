package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.prototype.ObjectiveBean;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;

public class UMCHeadV1 extends AbstractUMCHead implements UMCHead {
    public static final String     ProtocolVersion   = "1.1";
    public static final String     ProtocolSignature = "UMC/" + UMCHeadV1.ProtocolVersion;
    public static final int        StructBlockSize   = Integer.BYTES + Byte.BYTES + Long.BYTES + Long.BYTES + Byte.BYTES + Short.BYTES + Long.BYTES + Long.BYTES + Long.BYTES;
    public static final int        HeadBlockSize     = UMCHeadV1.ProtocolSignature.length() + UMCHeadV1.StructBlockSize;
    public static final ByteOrder  BinByteOrder      = ByteOrder.LITTLE_ENDIAN ;// Using x86, C/C++
    public static final int        HeadFieldsSize    = 10;
    public static final int        ReadBufferSize    = 64;


    protected String                 szSignature                                ; // :0
    protected int                    nExtraHeadLength  = 2                      ; // :1 sizeof( int32 ) = 4
    protected ExtraEncode            extraEncode       = ExtraEncode.Undefined  ; // :2 sizeof( ExtraEncode/byte ) = 1

    protected long                   nBodyLength       = 0                      ; // :3 sizeof( int64 ) = 8
    protected long                   nKeepAlive        = -1                     ; // :4 sizeof( int64 ) = 8, [-1 for forever, 0 for off, others for millis]
    protected UMCMethod              method                                     ; // :5 sizeof( UMCMethod/byte ) = 1
    protected Status                 status            = Status.OK              ; // :6 sizeof( Status/Short ) = 2
    protected long                   controlBits       = 0                      ; // :7 sizeof( int64 ) = 8, Custom control bytes.
    protected long                   identityId        = 0                      ; // :8 sizeof( int64 ) = 8, Client / Node ID
    protected long                   sessionId         = 0                      ; // :9 sizeof( int64 ) = 8

    protected byte[]                 extraHead         = {}                     ;
    protected Object                 dyExtraHead                                ;
    protected ExtraHeadCoder         extraHeadCoder                             ;


    public UMCHeadV1(  ) {
        this( UMCHeadV1.ProtocolSignature, UMCMethod.INFORM );
    }

    public UMCHeadV1( String szSignature ) {
        this( szSignature, UMCMethod.INFORM );
    }

    public UMCHeadV1( String szSignature, long controlBits ) {
        this( szSignature, UMCMethod.INFORM, controlBits );
    }

    public UMCHeadV1( String szSignature, UMCMethod umcMethod ) {
        this( szSignature, umcMethod, 0 );
    }

    public UMCHeadV1( String szSignature, UMCMethod umcMethod, long controlBits ) {
        this( szSignature, umcMethod, new LinkedTreeMap<>(), controlBits );
    }

    public UMCHeadV1( String szSignature, UMCMethod umcMethod, Object ex, long controlBits ) {
        this.szSignature       = szSignature;
        this.method            = umcMethod;
        this.dyExtraHead       = ex;
        this.controlBits       = controlBits;
    }

    UMCHeadV1( String szSignature, UMCMethod umcMethod, Map<String,Object > joEx, long controlBits ) {
        this( szSignature, umcMethod, (Object) joEx, controlBits );
    }

    UMCHeadV1( String szSignature, UMCMethod umcMethod, Map<String,Object > joEx ) {
        this( szSignature, umcMethod, (Object) joEx, 0 );
    }



    @Override
    public int sizeof() {
        return UMCHeadV1.HeadBlockSize;
    }

    @Override
    public int fieldsSize() {
        return UMCHeadV1.HeadFieldsSize;
    }



    @Override
    protected void setSignature            ( String signature       ) {
        this.szSignature = signature;
    }

    @Override
    protected void setBodyLength           ( long length            ) {
        this.nBodyLength = length;
    }

    @Override
    public void setKeepAlive               ( long nKeepAlive        ) {
        this.nKeepAlive = nKeepAlive;
    }

    @Override
    protected void setMethod               ( UMCMethod umcMethod    ) {
        this.method = umcMethod;
        if ( this.method == UMCMethod.INFORM ) {
            this.nBodyLength = 0;
        }
    }

    @Override
    protected void setExtraEncode          ( ExtraEncode encode     ) {
        this.extraEncode = encode;
    }



    @Override
    public void setControlBits   ( long controlBits       ) {
        this.controlBits = controlBits;
    }

    @Override
    public void setIdentityId    ( long identityId        ) {
        this.identityId = identityId;
    }

    @Override
    public void setSessionId     ( long sessionId         ) {
        this.sessionId = sessionId;
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
        this.status = status;
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
        return this.method;
    }

    @Override
    public int             getExtraHeadLength() {
        return this.nExtraHeadLength;
    }

    @Override
    public long            getBodyLength() {
        return this.nBodyLength;
    }

    @Override
    public long            getKeepAlive() {
        return this.nKeepAlive;
    }

    @Override
    public long            getSessionId() {
        return this.sessionId;
    }

    @Override
    public Status          getStatus() {
        return this.status;
    }

    @Override
    public ExtraEncode     getExtraEncode() {
        return this.extraEncode;
    }

    @Override
    public long            getControlBits() {
        return this.controlBits;
    }

    @Override
    public long            getIdentityId() {
        return this.identityId;
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
    public String toString() {
        return this.toJSONString();
    }


    @Override
    public EncodePair bytesEncode( ExtraHeadCoder extraHeadCoder ) {
        return UMCHeadV1.encode( this, extraHeadCoder );
    }

    public static EncodePair encode( UMCHead umcHead, ExtraHeadCoder extraHeadCoder ) {
        UMCHeadV1 head = (UMCHeadV1) umcHead;
        head.applyExtraHeadCoder( extraHeadCoder );
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

        return new EncodePair( byteBuffer, nBufLength );
    }

    public static UMCHead decode( byte[] buf, String szSignature, ExtraHeadCoder extraHeadCoder ) throws IOException {
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
