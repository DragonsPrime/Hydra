package com.pinecone.framework.util.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

public final class JSON {
    public static final Object NULL      = new JSON.Null();


    public static Object parse     ( String szJsonString ) {
        return ( new JSONCursorParser( szJsonString ) ).nextValue();
    }

    public static String stringify ( Object that ) {
        return JSON.decode( that, JSONEncoder.BASIC_JSON_ENCODER );
    }

    public static String stringify ( Object that, int nIndentFactor ) {
        return JSON.decode( that, nIndentFactor, JSONEncoder.BASIC_JSON_ENCODER );
    }

    public static String marshal   ( Object that ) {
        return JSON.decode( that, JSONEncoder.BASIC_JSON_MARSHAL );
    }

    public static String marshal   ( Object that, boolean bOnlyMarshalAnnotated ) {
        return JSON.decode( that, new JSONMarshal( bOnlyMarshalAnnotated ) );
    }

    public static <T> T unmarshal ( String szJsonString, Class<T > classType ) {
        ObjectJSONCursorUnmarshal unmarshal = new ObjectJSONCursorUnmarshal( szJsonString, classType );
        return classType.cast( unmarshal.nextValue() ) ;
    }

    public static <T> T unmarshal ( Reader reader, Class<T > classType ) {
        ObjectJSONCursorUnmarshal unmarshal = new ObjectJSONCursorUnmarshal( reader, classType );
        return classType.cast( unmarshal.nextValue() ) ;
    }

    public static String decode    ( Object that, JSONEncoder encoder ) {
        return JSON.decode( that, 0, encoder );
    }

    public static String decode    ( Object that, int nIndentFactor, JSONEncoder encoder ) {
        StringWriter w = new StringWriter();
        try {
            synchronized( w.getBuffer() ) {
                return encoder.write( that, w, nIndentFactor,0 ).toString();
            }
        }
        catch ( IOException e ){
            return null;
        }
    }


    private static final class Null {
        private Null() {
        }

        @Override
        protected final Object clone() {
            try{
                super.clone();
            }
            catch ( CloneNotSupportedException e ) {
                throw new InternalError(e);
            }
            return this;
        }

        @Override
        public boolean equals( Object that ) {
            if ( that == this || that instanceof Null ) {
                return true;
            }
            return that == null;
        }

        @Override
        public String toString() {
            return this.toJSONString();
        }

        public String toJSONString() {
            return "null";
        }
    }
}
