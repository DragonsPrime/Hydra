package com.pinecone.hydra.umct.husky.machinery;

import java.util.Set;

import com.pinecone.framework.unit.LinkedTreeSet;
import com.pinecone.framework.util.lang.ArchClassScopeSet;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.lang.ScopedPackage;

public class HuskyMappingScopeSet extends ArchClassScopeSet {
    public HuskyMappingScopeSet( Set<ScopedPackage> scope, ClassLoader classLoader ) {
        super( scope, classLoader );
    }

    public HuskyMappingScopeSet( ClassLoader classLoader ) {
        super( new LinkedTreeSet<>(), classLoader );
    }

    public HuskyMappingScopeSet( DynamicFactory factory ) {
        super( new LinkedTreeSet<>(), factory.getClassLoader() );
    }
}
