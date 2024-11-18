package com.pinecone.framework.util.json;

import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.homotype.AnnotatedJSONInjector;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class JSONMarshal extends GenericJSONEncoder {
    protected boolean    mbOnlyMarshalAnnotated;

    public JSONMarshal( boolean bOnlyMarshalAnnotated ) {
        super();

        this.mbOnlyMarshalAnnotated = bOnlyMarshalAnnotated;
    }

    public JSONMarshal() {
        this( true );
    }

    @Override
    public Writer write          ( JSONObject that, Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException {
        if ( that != null ) {
            this.write( (Map)that, writer, nIndentFactor, nIndentBlankNum );
        }
        else {
            writer.write( JSONEncoder.JSON_OBJ_NULL_DEFAULT );
        }
        return writer;
    }

    @Override
    public Writer write          ( JSONArray that, Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException {
        if ( that != null ) {
            this.write( (Collection) that, writer, nIndentFactor, nIndentBlankNum );
        }
        else {
            writer.write( JSONEncoder.JSON_OBJ_NULL_DEFAULT );
        }
        return writer;
    }

    @Override
    public Writer writeUnidentifiedObject( Object that, Writer writer, int nIndentFactor, int nIndentBlankNum ) throws IOException {
        ArrayList<Object[] > list = new ArrayList<>();
        Field[] fields = that.getClass().getDeclaredFields();
        for ( Field field : fields ) {
            ReflectionUtils.makeAccessible( field );
            String szKey = AnnotatedJSONInjector.getAnnotatedKey( field );
            if( this.mbOnlyMarshalAnnotated ) {
                if( szKey == null ) {
                    continue;
                }
                else if( szKey.isEmpty() ) {
                    szKey = field.getName();
                }
            }
            else if( szKey == null || szKey.isEmpty() ) {
                szKey = field.getName();
            }

            Object value;
            try{
                value = field.get( that );
            }
            catch ( IllegalAccessException e ){
                value = null;
            }

            list.add( new Object[] { szKey, field, value } );
        }

        if( this.mbOnlyMarshalAnnotated && list.isEmpty() ) {
            super.writeUnidentifiedObject( that, writer, nIndentFactor, nIndentBlankNum );
        }
        else {
            writer.write('{');
            boolean bHasNextElement = false;

            int nNewIndent = nIndentBlankNum + nIndentFactor;

            for( int i = 0; i < list.size(); ++i ) {
                GenericJSONEncoder.beforeJsonElementWrote( writer, nIndentFactor, nNewIndent, bHasNextElement );
                this.writeKeyValue( writer, list.get(i)[0], list.get(i)[2], nIndentFactor, nIndentBlankNum  );
                bHasNextElement = true;
            }

            if ( nIndentFactor > 0 ) {
                writer.write( '\n' );
            }

            GenericJSONEncoder.indentBlank( writer, nIndentBlankNum );

            writer.write( '}' );
        }
        return writer;
    }
}
