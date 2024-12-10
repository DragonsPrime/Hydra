package com.pinecone.hydra.umct.mapping;

import java.util.ArrayList;
import java.util.List;

import com.pinecone.framework.lang.field.DataStructureEntity;
import com.pinecone.framework.util.StringUtils;

public final class MethodDigestUtils {
    public static List<String> getArgumentsKey( List<? extends ParamsDigest> paramsDigests, DataStructureEntity argumentTemplate ) {
        if ( paramsDigests == null || paramsDigests.isEmpty() || paramsDigests.size() != argumentTemplate.size() ) {
            return null;
        }

        List<String> keys = new ArrayList<>( paramsDigests.size() );
        for ( ParamsDigest digest : paramsDigests ) {
            String n = digest.getName();
            if ( StringUtils.isEmpty( n ) ) {
                return null;
            }
            keys.add( n );
        }
        return keys;
    }
}
