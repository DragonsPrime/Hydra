package com.pinecone.hydra.umct;

import com.pinecone.framework.util.lang.ClassScope;
import com.pinecone.framework.util.lang.GenericScopeNSProtocolIteratorsFactory;
import com.pinecone.framework.util.lang.NSProtocolIteratorsFactoryAdapter;
import com.pinecone.ulf.util.lang.GenericPreloadClassInspector;
import com.pinecone.ulf.util.lang.HierarchyClassInspector;
import com.pinecone.ulf.util.lang.PooledClassCandidateScanner;

import javassist.ClassPool;

public class GenericMessagramScanner extends PooledClassCandidateScanner implements MessagramScanner {
    protected HierarchyClassInspector mClassInspector     ;

    public GenericMessagramScanner     ( ClassScope searchScope, ClassLoader classLoader, NSProtocolIteratorsFactoryAdapter iteratorsFactory, ClassPool classPool ) {
        super( searchScope, classLoader, iteratorsFactory, classPool );

        this.mClassInspector = new GenericPreloadClassInspector( this.mClassPool );
    }

    public GenericMessagramScanner     ( ClassScope searchScope, ClassLoader classLoader, ClassPool classPool ) {
        this( searchScope, classLoader, new GenericScopeNSProtocolIteratorsFactory( classLoader, searchScope ), classPool );
    }

    public GenericMessagramScanner     ( ClassScope searchScope, ClassLoader classLoader ) {
        this( searchScope, classLoader, new GenericScopeNSProtocolIteratorsFactory( classLoader, searchScope ), ClassPool.getDefault() );
    }
}
