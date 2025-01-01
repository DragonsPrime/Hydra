package com.pinecone.framework.util.json.homotype;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Set;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.GenericJSONEncoder;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONEncoder;

public class GenericStructJSONEncoder implements StructJSONEncoder {
    public GenericStructJSONEncoder() {

    }

    @Override
    public String valueJsonify( Object val ) {
        return JSON.stringify( val );
    }

    @Override
    public void valueJsonify( Object val, Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException {
        JSONEncoder.BASIC_JSON_ENCODER.write( val, writer, nIndentFactor, nIndentBlankNum );
    }

    @Override
    public String encode( Object struct, Set<String > exceptedKeys, boolean bAllFields ) {
        Class klass = struct.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Field[] fields;
        if( bAllFields ) {
            fields = klass.getDeclaredFields();
        }
        else {
            fields = includeSuperClass ? klass.getFields() : klass.getDeclaredFields();
        }

        StringBuilder sb = new StringBuilder( "{" );
        for( int i = 0; i < fields.length; ++i ) {
            try {
                Field field = fields[i];
                field.setAccessible( true );
                String key = field.getName();
                if( !StringUtils.isEmpty( key ) ) {
                    if( exceptedKeys != null && exceptedKeys.contains( key ) ) {
                        continue;
                    }

                    Object val;
                    try {
                        val = field.get( struct );
                        sb.append( '\"' ).append( key ).append( "\":" );
                    }
                    catch ( IllegalAccessException | IllegalArgumentException e ) {
                        continue;
                    }

                    sb.append( this.valueJsonify( val ) );
                    sb.append( ',' );
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
                // Do nothing.
            }
        }

        if( sb.charAt( sb.length() - 1 ) == ',' ) {
            sb.deleteCharAt( sb.length() - 1 );
        }

        return sb.append( '}' ).toString();
    }

    @Override
    public String encode( Object struct ) {
        return this.encode( struct, (Set<String >) null );
    }

    @Override
    public void encode( Object struct, Writer writer, int nIndentFactor ) throws IOException {
        this.encode0( struct, writer, nIndentFactor, 0 );
    }

    protected void encode0( Object struct, Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException {
        Class klass = struct.getClass();
        boolean includeSuperClass = klass.getClassLoader() != null;
        Field[] fields = includeSuperClass ? klass.getFields() : klass.getDeclaredFields();

        writer.write( "{" );
        boolean isFirst = true;

        for( int i = 0; i < fields.length; ++i ) {
            try {
                Field field = fields[i];
                field.setAccessible( true );
                String key = field.getName();
                if( !StringUtils.isEmpty( key ) ) {
                    int nNewIndent = nIndentBlankNum + nIndentFactor;
                    if ( !isFirst ) {
                        writer.write( "," );
                    }

                    if ( nNewIndent > 0 ) {
                        writer.write('\n');
                    }
                    GenericJSONEncoder.indentBlank( writer, nNewIndent );


                    Object val;
                    try {
                        val = field.get( struct );
                        writer.write( "\"" + key + "\":" );
                    }
                    catch ( IllegalAccessException | IllegalArgumentException e ) {
                        continue;
                    }

                    this.valueJsonify( val, writer, nIndentFactor, nNewIndent );
                    isFirst = false;

                    GenericJSONEncoder.indentBlank( writer, nIndentBlankNum );
                }
            }
            catch ( Exception e ) {
                e.printStackTrace();
                // Do nothing.
            }
        }

        if ( nIndentFactor > 0 ) {
            writer.write( '\n' );
        }
        writer.write( "}" );
    }

}