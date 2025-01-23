package com.pinecone.hydra.umq;

import java.nio.ByteOrder;
import java.util.Map;

import com.pinecone.framework.system.prototype.ObjectiveBean;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.unit.LinkedTreeMap;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.msg.AbstractUMCHead;
import com.pinecone.hydra.umc.msg.ExtraEncode;
import com.pinecone.hydra.umc.msg.Status;
import com.pinecone.hydra.umc.msg.UMCHead;
import com.pinecone.hydra.umc.msg.UMCHeadV1;
import com.pinecone.hydra.umc.msg.UMCMethod;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

public class UMCHeadMQ extends AbstractUMCHead implements UMCHead {
    public static final String     ProtocolVersion   = "1.1";
    public static final String     ProtocolSignature = "UMC/" + UMCHeadV1.ProtocolVersion;
    public static final int        StructBlockSize   = Byte.BYTES + Integer.BYTES + Long.BYTES + Long.BYTES + Short.BYTES + Byte.BYTES + Long.BYTES + Long.BYTES + Long.BYTES;
    public static final int        HeadBlockSize     = UMCHeadV1.ProtocolSignature.length() + UMCHeadV1.StructBlockSize;
    public static final ByteOrder  BinByteOrder      = ByteOrder.LITTLE_ENDIAN ;// Using x86, C/C++

    protected String                 szSignature                                ;
    protected UMCMethod              method                                     ; // sizeof( UMCMethod/byte ) = 1
    protected int                    nExtraHeadLength  = 2                      ; // sizeof( int32 ) = 4
    protected long                   nBodyLength       = 0                      ; // sizeof( int64 ) = 8
    protected long                   nKeepAlive        = -1                     ; // sizeof( int64 ) = 8, [-1 for forever, 0 for off, others for millis]
    protected Status                 status            = Status.OK              ; // sizeof( Status/Short ) = 2
    protected ExtraEncode            extraEncode       = ExtraEncode.Undefined  ; // sizeof( ExtraEncode/byte ) = 1
    protected long                   controlBits                                ; // sizeof( int64 ) = 8, Custom control bytes.
    protected long                   sessionId         = 0                      ; // sizeof( int64 ) = 8
    protected long                   identityId        = 0                      ; // sizeof( int64 ) = 8, Client / Node ID
    protected byte[]                 extraHead         = {}                     ;
    protected Object                 dyExtraHead                                ;

    protected ExtraHeadCoder         extraHeadCoder                             ;





    @Override
    protected void setSignature            ( String signature       ) {
        this.szSignature = signature;
    }

    @Override
    protected void setBodyLength           ( long length            ) {
        this.nBodyLength = length;
    }

    @Override
    public void setKeepAlive     ( long nKeepAlive        ) {
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
    public void setSessionId     ( long sessionId         ) {
        this.sessionId = sessionId;
    }

    @Override
    public void setIdentityId    ( long identityId        ) {
        this.identityId = identityId;
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
        if( !( this.dyExtraHead instanceof Map ) ) {
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
    public String toJSONString() {
        Map<String, Object > joExtraHead = this.getMapExtraHead();
        String szExtraHead;
        if( joExtraHead == null ) {
            szExtraHead = "[object Object]";
        }
        else {
            szExtraHead = JSON.stringify( this.getMapExtraHead() );
        }
        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "Signature"      , this.getSignature()                                             ),
                new KeyValue<>( "Method"         , this.getMethod()                                                ),
                new KeyValue<>( "ExtraHeadLength", this.getExtraHeadLength()                                       ),
                new KeyValue<>( "BodyLength"     , this.getBodyLength()                                            ),
                new KeyValue<>( "KeepAlive"      , this.getKeepAlive()                                             ),
                new KeyValue<>( "Status"         , this.getStatus().getName()                                      ),
                new KeyValue<>( "ExtraEncode"    , this.getExtraEncode().getName()                                 ),
                new KeyValue<>( "ControlBits"    , "0x" + Long.toUnsignedString( this.getControlBits(),16 )  ),
                new KeyValue<>( "SessionId"      , this.getSessionId()                                             ),
                new KeyValue<>( "IdentityId"     , this.getIdentityId()                                            ),
                new KeyValue<>( "ExtraHead"      , szExtraHead                                                     ),
        } );
    }
}
