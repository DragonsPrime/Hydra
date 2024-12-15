package com.pinecone.hydra.umct.husky.machinery;

import com.pinecone.framework.util.lang.ScopedPackage;

public interface PMCTMarshal extends PMCTTransformer {
    PMCTMarshal           addScope           ( String szPackageName );

    PMCTMarshal           addScope           ( ScopedPackage scope );

    MultiMappingLoader    getMultiMappingLoader();
}
