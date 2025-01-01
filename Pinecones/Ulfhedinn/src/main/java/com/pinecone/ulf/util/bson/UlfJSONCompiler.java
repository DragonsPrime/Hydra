package com.pinecone.ulf.util.bson;

import com.pinecone.framework.util.Bytes;

import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONCompiler;
import com.pinecone.framework.util.json.JSONString;
import com.pinecone.framework.util.json.binary.BsonTraits;
import com.pinecone.framework.util.json.binary.Bsonut;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public class UlfJSONCompiler implements JSONCompiler {
    public UlfJSONCompiler() { }

    protected OutputStream compileUnidentifiedObject ( Object that, OutputStream outputStream ) throws IOException {
        // Ignore them
        return outputStream;
    }

    protected OutputStream compileUnknownAnyObject   ( Object that, OutputStream outputStream ) throws IOException {
        if ( that != null ) {
            try {
                BsonTraits.invokeBsonSerialize( that, outputStream );
            }
            catch ( Exception e ){
                try {
                    outputStream.write( BsonTraits.invokeToBsonBytes( that ) );
                }
                catch ( Exception e1 ){
                    return this.compileUnidentifiedObject( that, outputStream ) ;
                }
            }
        }
        else {
            outputStream.write( DataTypeCode.NULL.getValue() );
        }
        return outputStream;
    }

    @Override
    public OutputStream compile( Map that, OutputStream outputStream ) throws IOException {
        outputStream.write( DataTypeCode.JSONOBJECT.getValue() );
        Map<?, ?> map = (Map<?, ?>) that;
        outputStream.write( Bytes.int64ToBytesLE( map.size() ) );
        for ( Map.Entry<?, ?> entry : map.entrySet() ) {
            this.compile( entry.getKey(), outputStream );
            this.compile( entry.getValue(), outputStream );
        }
        outputStream.write( DataTypeCode.JSONOBJECT_END.getValue() );
        return outputStream;
    }

    @Override
    public OutputStream compile( Collection that, OutputStream outputStream ) throws IOException {
        outputStream.write( DataTypeCode.JSONARRAY.getValue() );
        Collection<?> collection = (Collection<?>) that;
        outputStream.write( Bytes.int64ToBytesLE( collection.size() ) );
        for ( Object item : collection ) {
            this.compile( item, outputStream );
        }
        outputStream.write( DataTypeCode.JSONARRAY_END.getValue() );
        return outputStream;
    }

    @Override
    public OutputStream compile( Object[] those, OutputStream outputStream ) throws IOException {
        outputStream.write( DataTypeCode.JSONARRAY.getValue() );
        int length = those.length;
        outputStream.write( Bytes.int64ToBytesLE( length ) );
        for ( int i = 0; i < length; ++i ) {
            this.compile( those[i], outputStream );
        }
        outputStream.write( DataTypeCode.JSONARRAY_END.getValue() );
        return outputStream;
    }

    @Override
    public OutputStream compile( Object that, OutputStream outputStream ) throws IOException {
        if ( that != null ) {
            if ( that == JSON.NULL ) {
                outputStream.write( DataTypeCode.NULL.getValue() );
            }
            else if ( that instanceof Map ) {
                this.compile( (Map) that, outputStream );
            }
            else if ( that instanceof Collection ) {
                this.compile( (Collection) that, outputStream );
            }
            else if ( that instanceof String ) {
                String str = (String) that;
                outputStream.write( DataTypeCode.STRING.getValue() );
                outputStream.write( Bytes.int32ToBytesLE( str.length() ) );
                outputStream.write( str.getBytes());
            }
            else if ( that.getClass().isArray() ) {
                this.compile( (Object[]) that, outputStream );
            }
            else if ( that instanceof Number ) {
                if ( that instanceof Byte ) {
                    outputStream.write( DataTypeCode.BYTE8.getValue() );
                    outputStream.write( (Byte) that );
                }
                else if ( that instanceof Short ) {
                    outputStream.write( DataTypeCode.INT16.getValue() );
                    outputStream.write( Bytes.int16ToBytesLE((Short) that));
                }
                else if ( that instanceof Integer ) {
                    outputStream.write( DataTypeCode.INT32.getValue() );
                    outputStream.write( Bytes.int32ToBytesLE((Integer) that));
                }
                else if ( that instanceof Long ) {
                    outputStream.write( DataTypeCode.INT64.getValue() );
                    outputStream.write( Bytes.int64ToBytesLE((Long) that));
                }
                else if ( that instanceof Float ) {
                    outputStream.write( DataTypeCode.FLOAT32.getValue() );
                    outputStream.write( Bytes.float32ToBytesLE((Float) that));
                }
                else if ( that instanceof Double ) {
                    outputStream.write( DataTypeCode.FLOAT64.getValue() );
                    outputStream.write( Bytes.float64ToBytesLE((Double) that));
                }
                else if ( that instanceof BigInteger ) {
                    outputStream.write( DataTypeCode.BIG_INTEGER.getValue() );
                    byte[] bigIntBytes = ((BigInteger) that).toByteArray();
                    outputStream.write( Bytes.int32ToBytesLE( bigIntBytes.length ) );
                    outputStream.write( bigIntBytes );
                }
                else if ( that instanceof BigDecimal ) {
                    outputStream.write( DataTypeCode.BIG_DECIMAL.getValue() );
                    BigDecimal bigDecimal = (BigDecimal) that;
                    byte[] bigIntBytes = bigDecimal.unscaledValue().toByteArray();
                    int scale = bigDecimal.scale();
                    outputStream.write( Bytes.int32ToBytesLE( bigIntBytes.length ) );
                    outputStream.write( bigIntBytes);
                    outputStream.write( Bytes.int32ToBytesLE(scale) );
                }
            }
            else if ( that instanceof Boolean ) {
                outputStream.write( DataTypeCode.BOOL.getValue() );
                outputStream.write( (Boolean) that ? 1 : 0 );
            }
            else if ( that instanceof JSONString ) {
                String jsonString = ( (JSONString) that).toJSONString();
                outputStream.write( DataTypeCode.STRING.getValue() );
                outputStream.write( Bytes.int32ToBytesLE( jsonString.length() ) );
                outputStream.write( jsonString.getBytes());
            }
            else if ( that instanceof Bsonut ) {
                (( Bsonut ) that).bsonSerialize( outputStream );
            }
            else {
                this.compileUnknownAnyObject( that, outputStream );
            }
        }
        else {
            outputStream.write( DataTypeCode.NULL.getValue() );
        }
        return outputStream;
    }
}
