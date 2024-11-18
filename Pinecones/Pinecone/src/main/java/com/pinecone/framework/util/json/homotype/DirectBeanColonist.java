package com.pinecone.framework.util.json.homotype;

import java.util.Map;

public class DirectBeanColonist extends ArchBeanColonist {
    public DirectBeanColonist() {
        super();
    }

    @Override
    protected void putValue( Map<String, Object> targetMap, String key, Object result, boolean bRecursive ) {
        targetMap.put( key, result );
    }
}
