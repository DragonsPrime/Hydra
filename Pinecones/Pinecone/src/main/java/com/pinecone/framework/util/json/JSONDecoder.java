package com.pinecone.framework.util.json;

import com.pinecone.framework.system.prototype.Pinenut;

import java.util.List;
import java.util.Map;

public interface JSONDecoder extends Pinenut {
    JSONDecoder INNER_JSON_OBJECT_DECODER = new JSONObjectDecoder() {
        @Override
        protected void set( Object self, String key, Object val ) {
            ( (JSONObject) self ).put( key, val );
        }
    };

    JSONDecoder INNER_JSON_ARRAY_DECODER = new JSONArrayDecoder() {
        @Override
        protected void add( Object self, Object parent, Object indexKey, Object val ) {
            ( (JSONArray) self ).add( val );
        }
    };

    JSONDecoder INNER_MAP_DECODER = new JSONObjectDecoder() {
        @Override
        @SuppressWarnings( "unchecked" )
        protected void set( Object self, String key, Object val ) {
            ( (Map<String, Object >) self ).put( key, val );
        }
    };

    JSONDecoder INNER_LIST_DECODER = new JSONArrayDecoder() {
        @Override
        @SuppressWarnings( "unchecked" )
        protected void add( Object self, Object parent, Object indexKey, Object val ) {
            ( (List<Object>) self ).add( val );
        }
    };


    void decode( Object self, Object parent, Object indexKey, ArchCursorParser x ) ;
}
