package com.pinecone.framework.util.json;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;

public class CustomizableJSONCursorParser extends ArchCursorParser {
    protected Class<? extends JSONObject > mJSONObjectClass;
    protected Class<? extends JSONArray >  mJSONArrayClass;

    public CustomizableJSONCursorParser( Reader reader, Class<? extends JSONObject > jObjectClass, Class<? extends JSONArray > jArrayClass ) {
        super( reader );

        this.mJSONObjectClass = jObjectClass;
        this.mJSONArrayClass  = jArrayClass;
    }

    public CustomizableJSONCursorParser( InputStream inputStream, Class<? extends JSONObject> jObjectClass, Class<? extends JSONArray > jArrayClass ) throws JSONParseException {
        super( inputStream );

        this.mJSONObjectClass = jObjectClass;
        this.mJSONArrayClass  = jArrayClass;
    }

    public CustomizableJSONCursorParser( String s, Class<? extends JSONObject > jObjectClass, Class<? extends JSONArray > jArrayClass ) {
        super( s );

        this.mJSONObjectClass = jObjectClass;
        this.mJSONArrayClass  = jArrayClass;
    }

    @Override
    protected JSONArray newJSONArray( Object indexKey, ArchCursorParser parser, Object parent ) {
        try {
            return this.mJSONArrayClass.getDeclaredConstructor().newInstance();
        }
        catch ( InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e ) {
            return null;
        }
    }

    @Override
    protected JSONObject newJSONObject( Object indexKey, ArchCursorParser parser, Object parent ) {
        try {
            return this.mJSONObjectClass.getDeclaredConstructor().newInstance();
        }
        catch ( InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e ) {
            return null;
        }
    }
}

