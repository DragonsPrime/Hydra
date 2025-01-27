package com.pinecone.hydra.umct.decipher;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.msg.ExtraEncode;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.wolfmc.UlfInformMessage;

public class JSONHeaderDecipher implements HeaderDecipher {
    protected String mszServicePathKey;

    public JSONHeaderDecipher( String szServicePathKey ) {
        this.mszServicePathKey = szServicePathKey;
    }

    @Override
    public String getServicePath( Object that ) {
        return this.evalString( that, null, this.mszServicePathKey );
    }

    @Override
    public Object eval( Object that, Object descriptor, String key ) {
        return ( (Map) that ).get( key );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object > values( Object that, Object descriptor, Object argTpl ) {
        return ( (Map) that ).values();
    }

    @Override
    public Object[] evals( Object that, Object descriptor, List<String> keys, Object argTpl ) {
        Map map = (Map) that;
        Object[] ret = new Object[ keys.size() ];
        int i = 0;
        for( String k : keys ) {
            ret[ i ] = k;
            ++i;
        }
        return ret;
    }

    @Override
    public UMCMessage assembleReturnMsg( Object that, Object descriptor ) {
        if ( that instanceof UMCMessage ) {
            return (UMCMessage) that;
        }
        if ( that == null ) {
            return new UlfInformMessage( null, ExtraEncode.JSONString );
        }

        JSONObject jo = new JSONMaptron();
        jo.put( "__RESPONSE__", that );
        return new UlfInformMessage( jo ); // TODO, Transfer
    }
}
