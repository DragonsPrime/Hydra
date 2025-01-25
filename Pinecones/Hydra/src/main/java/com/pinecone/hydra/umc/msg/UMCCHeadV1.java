package com.pinecone.hydra.umc.msg;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import com.pinecone.framework.unit.BitSet64;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.Bytes;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.JSONString;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

/**
 *  Pinecone Ursus For Java UMCC[ Uniform Message Control - Compacted ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  Uniform Message Control Protocol - Base-Mutable - Compacted [UMC-C]
 *  统一消息控制协议-紧凑变基分协议
 *  **********************************************************
 */
public class UMCCHeadV1 extends UMCHeadV1 implements UMCCHead {
    public static final String     ProtocolSignature = "UMC-C/" + UMCHeadV1.ProtocolVersion;

    public static final int        BitmapAt          = 1;

    protected long                 fieldIndexBitmap                           ; // :1 sizeof( int64 ) = 8, Field index-control bitmap.


    public static final HeadField FieldSignature       = new HeadField( "signature"       , 0, ProtocolSignature.length() );
    public static final HeadField FieldExtraHeadLength = new HeadField( "extraHeadLength" , 1, Integer.BYTES );
    public static final HeadField FieldExtraEncode     = new HeadField( "extraEncode"     , 2, Byte.BYTES    );
    public static final HeadField FieldBodyLength      = new HeadField( "bodyLength"      , 3, Long.BYTES    );
    public static final HeadField FieldKeepAlive       = new HeadField( "keepAlive"       , 4, Long.BYTES    );
    public static final HeadField FieldMethod          = new HeadField( "method"          , 5, Byte.BYTES    );
    public static final HeadField FieldStatus          = new HeadField( "status"          , 6, Short.BYTES   );
    public static final HeadField FieldControlBits     = new HeadField( "controlBits"     , 7, Long.BYTES    );
    public static final HeadField FieldIdentityId      = new HeadField( "identityId"      , 8, Long.BYTES    );
    public static final HeadField FieldSessionId       = new HeadField( "sessionId"       , 9, Long.BYTES    );


    static final HeadField[] HeadFieldsMap   = new HeadField[ UMCHeadV1.HeadFieldsSize ];

    static final HeadField[] HeadFieldsIndex = {
            FieldSignature,
            FieldExtraHeadLength,
            FieldExtraEncode,
            FieldBodyLength,
            FieldKeepAlive,
            FieldMethod,
            FieldStatus,
            FieldControlBits,
            FieldIdentityId,
            FieldSessionId
    };

    static {
        System.arraycopy( HeadFieldsIndex, 0, HeadFieldsMap, 0, HeadFieldsSize );
        Arrays.sort( HeadFieldsMap, Comparator.comparing(a -> a.name) );
    }

    public static HeadField searchField( String fieldName ) {
        int low = 0;
        int high = HeadFieldsMap.length - 1;

        while ( low <= high ) {
            int mid = (low + high) >>> 1;
            int cmp = HeadFieldsMap[ mid ].name.compareTo(fieldName);

            if ( cmp == 0 ) {
                return HeadFieldsMap[ mid ];
            }
            else if ( cmp < 0 ) {
                low = mid + 1;
            }
            else {
                high = mid - 1;
            }
        }

        return null;
    }


    protected void enableDefaultFields() {
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldSignature.index );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldExtraHeadLength.index );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldExtraEncode.index );
    }

    public UMCCHeadV1(  ) {
        super( UMCCHeadV1.ProtocolSignature, UMCMethod.INFORM );
        this.enableDefaultFields();
    }

    public UMCCHeadV1( String szSignature ) {
        super( szSignature, UMCMethod.INFORM );
        this.enableDefaultFields();
    }

    public UMCCHeadV1( String szSignature, UMCMethod umcMethod ) {
        super( szSignature, umcMethod, 0 );
        this.enableDefaultFields();
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldMethod.index );
    }





    @Override
    public int sizeof() {
        int totalSize = 0;

        for ( int i = 0; i < UMCHeadV1.HeadFieldsSize; ++i ) {
            if ( ( this.fieldIndexBitmap & (1L << i) ) != 0 ) {
                totalSize += HeadFieldsIndex[ i ].sizeof;
            }
        }

        return totalSize;
    }

    @Override
    public int fieldsSize() {
        return BitSet64.existence( this.fieldIndexBitmap );
    }

    @Override
    public long getFieldIndexBitmap() {
        return this.fieldIndexBitmap;
    }

    @Override
    public long evalIndexBitmap() {
        if ( this.nExtraHeadLength > 0 ) {
            this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldExtraHeadLength.index );
            this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldExtraEncode.index );
        }

        if ( this.nBodyLength > 0 ) {
            this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldBodyLength.index );
        }

        if ( this.nKeepAlive != -1 ) {
            this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldKeepAlive.index );
        }

        if ( this.method != null && this.method != UMCMethod.UNDEFINED && this.method != UMCMethod.INFORM ) {
            this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldMethod.index );
        }

        if ( this.status != Status.OK ) {
            this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldStatus.index );
        }

        if ( this.controlBits != 0 ) {
            this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldControlBits.index );
        }

        if ( this.identityId != 0 ) {
            this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldIdentityId.index );
        }

        if ( this.sessionId != 0 ) {
            this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldSessionId.index );
        }

        return this.fieldIndexBitmap;
    }

    @Override
    public void enableField( int at ) {
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, at );
    }

    @Override
    public void disableField( int at ) {
        this.fieldIndexBitmap = BitSet64.clearBit( this.fieldIndexBitmap, at );
    }

    @Override
    public void enableField( String fieldName ) {
        HeadField field = UMCCHeadV1.searchField( fieldName );
        if ( field == null ) {
            throw new IllegalArgumentException( fieldName + " is not existed." );
        }

        this.enableField( field.index );
    }

    @Override
    public void disableField( String fieldName ) {
        HeadField field = UMCCHeadV1.searchField( fieldName );
        if ( field == null ) {
            throw new IllegalArgumentException( fieldName + " is not existed." );
        }

        this.disableField( field.index );
    }







    protected void enableExtraHead() {
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldExtraHeadLength.index );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldExtraEncode.index );
    }

    @Override
    public void setExtraHead               ( JSONObject jo           ) {
        super.setExtraHead( jo );
        this.transApplyExHead();
    }

    @Override
    public void setExtraHead               ( Map<String,Object > jo  ) {
        super.setExtraHead( jo );
        this.transApplyExHead();
    }

    @Override
    public void setExtraHead               ( Object o                ) {
        super.setExtraHead( o );
        this.transApplyExHead();
    }

    @Override
    public void setExtraEncode             ( ExtraEncode encode      ) {
        super.setExtraEncode( encode );
        this.transApplyExHead();
    }

    public UMCCHead applyExHead            ( Map<String, Object > jo ) {
        super.applyExHead( jo );
        this.enableExtraHead();
        return this;
    }

    @Override
    protected void transApplyExHead        (                         ) {
        if ( this.dyExtraHead != null && this.extraHeadCoder == null ) {
            throw new IllegalStateException( "ExtraHeadCoder is null." );
        }

        super.transApplyExHead();
        this.enableExtraHead();
    }

    @Override
    public void applyExtraHeadCoder        ( ExtraHeadCoder coder    ) {
        super.applyExtraHeadCoder( coder );
        this.enableExtraHead();
    }





    @Override
    public void setBodyLength              ( long length            ) {
        super.setBodyLength( length );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldBodyLength.index );
    }

    @Override
    public void setKeepAlive               ( long nKeepAlive        ) {
        super.setKeepAlive( nKeepAlive );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldKeepAlive.index );
    }

    @Override
    protected void setMethod               ( UMCMethod umcMethod    ) {
        super.setMethod( umcMethod );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldMethod.index );
    }

    @Override
    public void setStatus                  ( Status status          ) {
        super.setStatus( status );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldStatus.index );
    }



    @Override
    public void setControlBits   ( long controlBits       ) {
        super.setControlBits( controlBits );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldControlBits.index );
    }

    @Override
    public void setIdentityId    ( long identityId        ) {
        super.setIdentityId( identityId );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldIdentityId.index );
    }

    @Override
    public void setSessionId     ( long sessionId         ) {
        super.setSessionId( sessionId );
        this.fieldIndexBitmap = BitSet64.setBit( this.fieldIndexBitmap, FieldSessionId.index );
    }


    @Override
    public String toJSONString() {
        String szExtraHead = this.jsonifyExtraHead();



        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "Signature"        , this.getSignature()                                             ),

                new KeyValue<>( "FieldIndexBitmap" , JSONString.wrapRaw(
                        BitSet64.toIndexJSONString( this.fieldIndexBitmap ) )
                ),

                new KeyValue<>( "ExtraHeadLength"  , this.getExtraHeadLength()                                       ),
                new KeyValue<>( "ExtraEncode"      , this.getExtraEncode().getName()                                 ),
                new KeyValue<>( "BodyLength"       , this.getBodyLength()                                            ),
                new KeyValue<>( "KeepAlive"        , this.getKeepAlive()                                             ),
                new KeyValue<>( "Method"           , this.getMethod()                                                ),
                new KeyValue<>( "Status"           , this.getStatus().getName()                                      ),
                new KeyValue<>( "ControlBits"      , "0x" + Long.toUnsignedString( this.getControlBits(),16 )  ),
                new KeyValue<>( "IdentityId"       , this.getIdentityId()                                            ),
                new KeyValue<>( "SessionId"        , this.getSessionId()                                             ),
                new KeyValue<>( "ExtraHead"        , szExtraHead                                                     ),
        } );
    }

    public static class HeadField {
        public final String name;

        public final int index;

        public final int sizeof;

        HeadField( String name, int index, int sizeof ) {
            this.name = name;
            this.index = index;
            this.sizeof = sizeof;
        }
    }

    public static class EncodePair {
        public final ByteBuffer byteBuffer;
        public final int        bufLength;

        EncodePair( ByteBuffer byteBuffer, int bufLength ) {
            this.byteBuffer = byteBuffer;
            this.bufLength  = bufLength;
        }
    }

    public static EncodePair encode( UMCCHead umcHead, ExtraHeadCoder extraHeadCoder ) {
        UMCCHeadV1 head = (UMCCHeadV1) umcHead;
        head.applyExtraHeadCoder( extraHeadCoder );
        head.transApplyExHead();

        int extraHeadLength = head.getExtraHeadLength();

        ByteBuffer byteBuffer = ByteBuffer.allocate( UMCHeadV1.ReadBufferSize + extraHeadLength );
        byteBuffer.order( UMCHeadV1.BinByteOrder );

        int nBufLength = head.getSignatureLength();
        byteBuffer.put( head.getSignature().getBytes() );

        byteBuffer.putLong( head.fieldIndexBitmap );
        nBufLength += Long.BYTES;

        for ( int i = BitmapAt; i < HeadFieldsIndex.length; ++i ) {
            if ( ( head.fieldIndexBitmap & (1L << i) ) != 0 ) {
                HeadField field = HeadFieldsIndex[i];
                switch ( field.index ) {
                    case 1: { // nExtraHeadLength
                        byteBuffer.putInt( head.nExtraHeadLength );
                        nBufLength += Integer.BYTES;
                        break;
                    }
                    case 2: { // extraEncode
                        byteBuffer.put( head.extraEncode.getByteValue() );
                        nBufLength += Byte.BYTES;
                        break;
                    }
                    case 3: { // nBodyLength
                        byteBuffer.putLong( head.nBodyLength );
                        nBufLength += Long.BYTES;
                        break;
                    }
                    case 4: { // nKeepAlive
                        byteBuffer.putLong( head.nKeepAlive );
                        nBufLength += Long.BYTES;
                        break;
                    }
                    case 5: { // method
                        byteBuffer.put( head.method.getByteValue() );
                        nBufLength += Byte.BYTES;
                        break;
                    }
                    case 6: { // status
                        byteBuffer.putShort( head.status.getShortValue() );
                        nBufLength += Short.BYTES;
                        break;
                    }
                    case 7: { // controlBits
                        byteBuffer.putLong( head.controlBits );
                        nBufLength += Long.BYTES;
                        break;
                    }
                    case 8: { // identityId
                        byteBuffer.putLong( head.identityId );
                        nBufLength += Long.BYTES;
                        break;
                    }
                    case 9: { // sessionId
                        byteBuffer.putLong( head.sessionId );
                        nBufLength += Long.BYTES;
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }

        if( head.extraHead == null ) {
            byteBuffer.put( Bytes.Empty );
        }
        else {
            byteBuffer.put( head.extraHead );
        }
        nBufLength += head.getExtraHeadLength();

        return new EncodePair( byteBuffer, nBufLength );
    }

    public static UMCCHead decode( byte[] buf, String szSignature, ExtraHeadCoder extraHeadCoder ) throws IOException {
        int nBufSize = szSignature.length() + UMCHeadV1.StructBlockSize;

        if ( buf.length < nBufSize ) {
            throw new StreamTerminateException( "StreamEndException:[UMC-CProtocol] Stream is ended." );
        }

        int nReadAt = szSignature.length();
        if ( !Arrays.equals( buf, 0, szSignature.length(), szSignature.getBytes(), 0, szSignature.length() )  ) {
            throw new IOException( "[UMC-CProtocol] Illegal protocol signature." );
        }

        UMCCHeadV1 head = new UMCCHeadV1();
        head.applyExtraHeadCoder( extraHeadCoder );

        head.fieldIndexBitmap = ByteBuffer.wrap( buf, nReadAt, Long.BYTES ).order( BinByteOrder ).getLong();
        nReadAt += Long.BYTES;

        for ( int i = BitmapAt; i < HeadFieldsIndex.length; ++i ) {
            if ( ( head.fieldIndexBitmap & (1L << i) ) != 0 ) {
                HeadField field = HeadFieldsIndex[ i ];
                switch ( field.index ) {
                    case 1: { // nExtraHeadLength
                        head.nExtraHeadLength  = ByteBuffer.wrap( buf, nReadAt, Integer.BYTES ).order( BinByteOrder ).getInt();
                        nReadAt += Integer.BYTES;
                        break;
                    }
                    case 2: { // extraEncode
                        head.extraEncode       = ExtraEncode.asValue( ByteBuffer.wrap( buf, nReadAt, Byte.BYTES ).order( BinByteOrder ).get() );
                        nReadAt += Byte.BYTES;
                        break;
                    }
                    case 3: { // nBodyLength
                        head.nBodyLength = ByteBuffer.wrap(buf, nReadAt, Long.BYTES).order( BinByteOrder ).getLong();
                        nReadAt += Long.BYTES;
                        break;
                    }
                    case 4: { // nKeepAlive
                        head.nKeepAlive = ByteBuffer.wrap(buf, nReadAt, Long.BYTES).order( BinByteOrder ).getLong();
                        nReadAt += Long.BYTES;
                        break;
                    }
                    case 5: { // method
                        head.method = UMCMethod.values()[buf[nReadAt]];
                        nReadAt += Byte.BYTES;
                        break;
                    }
                    case 6: { // status
                        head.status = Status.asValue(ByteBuffer.wrap(buf, nReadAt, Short.BYTES).order( BinByteOrder ).getShort());
                        nReadAt += Short.BYTES;
                        break;
                    }
                    case 7: { // controlBits
                        head.controlBits = ByteBuffer.wrap(buf, nReadAt, Long.BYTES).order( BinByteOrder ).getLong();
                        nReadAt += Long.BYTES;
                        break;
                    }
                    case 8: { // identityId
                        head.identityId = ByteBuffer.wrap(buf, nReadAt, Long.BYTES).order( BinByteOrder ).getLong();
                        nReadAt += Long.BYTES;
                        break;
                    }
                    case 9: { // sessionId
                        head.sessionId = ByteBuffer.wrap(buf, nReadAt, Long.BYTES).order( BinByteOrder ).getLong();
                        nReadAt += Long.BYTES;
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }

        return head;
    }
}