package com.pinecone.hydra.umct.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PrototypeEvaluator implements HeaderEvaluator {
    @Override
    public Object eval( Object that, String key ) {
        return ( (Map) that ).get( key );
    }

    @Override
    public Collection<Object> values( Object that ) {
        return null;
    }

    @Override
    public Object[] evals( Object that, List<String> keys ) {
        return new Object[0];
    }
}
