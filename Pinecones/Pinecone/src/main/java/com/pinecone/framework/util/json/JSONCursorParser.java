package com.pinecone.framework.util.json;


import java.io.InputStream;
import java.io.Reader;

public class JSONCursorParser extends ArchCursorParser {
    public JSONCursorParser( Reader reader ) {
        super( reader );
    }

    public JSONCursorParser( InputStream inputStream ) throws JSONParseException {
        super( inputStream );
    }

    public JSONCursorParser( String s ) {
        super( s );
    }

    @Override
    protected JSONArray newJSONArray( Object indexKey, ArchCursorParser parser, Object parent, Object[] args ) {
        return new JSONArraytron( parser );
    }

    @Override
    protected JSONObject newJSONObject( Object indexKey, ArchCursorParser parser, Object parent, Object[] args ) {
        return new JSONMaptron( parser );
    }
}
