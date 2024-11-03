package com.pinecone.framework.util.json;

import com.pinecone.framework.unit.Dictium;
import com.pinecone.framework.unit.Units;

public interface JSONDictium extends Dictium<Object > {

    Object opt( Object key );

    boolean optBoolean( Object key );

    double optDouble( Object key );

    int optInt( Object key );

    JSONArray optJSONArray( Object key );

    JSONObject optJSONObject( Object key );

    long optLong( Object key );

    String optString( Object key );

    byte[] optBytes( Object key );


    JSONObject toJSONObject();

    JSONArray toJSONArray();

    default JSONArray affirmArray   ( Object key ) {
        Object o = this.opt(key);
        if (o instanceof JSONArray) {
            return (JSONArray) o;
        }
        JSONArray jNew = new JSONArraytron();
        this.insert( key, jNew );
        return jNew;
    }

    default JSONObject affirmObject ( Object key ) {
        Object o = this.opt(key);
        if (o instanceof JSONObject) {
            return (JSONObject) o;
        }
        JSONObject jNew = new JSONMaptron();
        this.insert( key, jNew );
        return jNew;
    }

    default Object affirm           ( Object key ) {
        if ( this.containsKey(key) ) {
            return this.opt(key);
        }

        Object o = JSON.NULL;
        this.insert( key, o );
        return o;
    }


    /**
     * query
     * 202406029
     * @param evalKey Object simple-eval key, fmt: key1.key2.key3...keyN (T->.T)
     * @return null for nothing, object for the value which just be queried.
     */
    default Object query( String evalKey ) {
        return Units.getValueFromMapStructureRecursively( this, evalKey );
    }

    default String queryString( String evalKey, String defaultValue ) {
        Object object = this.query( evalKey );
        return JSON.NULL.equals(object) ? defaultValue : object.toString();
    }

    default String queryString( String evalKey ) {
        return this.queryString( evalKey, "" );
    }

    default JSONObject queryJSONObject( String evalKey ) {
        Object o = this.query( evalKey );
        return o instanceof JSONObject ? (JSONObject)o : null;
    }

    default JSONArray queryJSONArray( String evalKey ) {
        Object o = this.query( evalKey );
        return o instanceof JSONArray ? (JSONArray)o : null;
    }
}
