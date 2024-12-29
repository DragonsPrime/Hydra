package com.pinecone.hydra.umct.husky.machinery;

import com.pinecone.framework.util.lang.ScopedPackage;

public interface PMCTContextMachinery extends PMCTTransformer {
    PMCTContextMachinery addScope           ( String szPackageName );

    PMCTContextMachinery addScope           ( ScopedPackage scope );

    MultiMappingLoader    getMultiMappingLoader();
}
