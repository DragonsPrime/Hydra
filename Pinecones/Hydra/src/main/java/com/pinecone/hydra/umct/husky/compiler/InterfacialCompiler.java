package com.pinecone.hydra.umct.husky.compiler;

import java.util.List;

import com.pinecone.hydra.umct.mapping.MappingDigest;

public interface InterfacialCompiler extends IfaceCompiler {
    IfaceMappingDigest compile( MappingDigest digest );

    List<IfaceMappingDigest > compile( List<MappingDigest> digests );
}
