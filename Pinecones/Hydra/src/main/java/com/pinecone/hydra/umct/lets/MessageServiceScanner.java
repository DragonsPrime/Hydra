package com.pinecone.hydra.umct.lets;

import com.pinecone.framework.util.lang.ClassScope;
import com.pinecone.framework.util.lang.GenericScopeNSProtocolIteratorsFactory;
import com.pinecone.framework.util.lang.NSProtocolIteratorsFactoryAdapter;
import com.pinecone.ulf.util.lang.GenericPreloadClassInspector;
import com.pinecone.ulf.util.lang.HierarchyClassInspector;
import com.pinecone.ulf.util.lang.PooledClassCandidateScanner;
import com.pinecone.ulf.util.lang.SimpleAnnotationExcludeFilter;
import javassist.ClassPool;

public class MessageServiceScanner extends PooledClassCandidateScanner implements MessageletScanner {
    protected HierarchyClassInspector mClassInspector     ;

    public MessageServiceScanner     ( ClassScope searchScope, ClassLoader classLoader, NSProtocolIteratorsFactoryAdapter iteratorsFactory, ClassPool classPool ) {
        super( searchScope, classLoader, iteratorsFactory, classPool );

        this.mClassInspector = new GenericPreloadClassInspector( this.mClassPool );
        this.addExcludeFilter( new SimpleAnnotationExcludeFilter( this.mClassInspector, MsgService.class ) );
    }

    public MessageServiceScanner     ( ClassScope searchScope, ClassLoader classLoader, ClassPool classPool ) {
        this( searchScope, classLoader, new GenericScopeNSProtocolIteratorsFactory( classLoader, searchScope ), classPool );
    }

    public MessageServiceScanner     ( ClassScope searchScope, ClassLoader classLoader ) {
        this( searchScope, classLoader, new GenericScopeNSProtocolIteratorsFactory( classLoader, searchScope ), ClassPool.getDefault() );
    }
}
