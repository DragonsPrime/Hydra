package com.pinecone.hydra.umct.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JSONHeaderEvaluator implements HeaderEvaluator {
    @Override
    public Object eval( Object that, String key ) {
        return ( (Map) that ).get( key );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Object > values( Object that ) {
        return ( (Map) that ).values();
    }

    @Override
    public Object[] evals( Object that, List<String> keys ) {
        Map map = (Map) that;
        Object[] ret = new Object[ keys.size() ];
        int i = 0;
        for( String k : keys ) {
            ret[ i ] = k;
            ++i;
        }
        return ret;
    }
}
