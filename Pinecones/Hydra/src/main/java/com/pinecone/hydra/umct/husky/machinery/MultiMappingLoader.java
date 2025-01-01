package com.pinecone.hydra.umct.husky.machinery;

import java.util.List;

import com.pinecone.framework.util.lang.MultiClassScopeLoader;
import com.pinecone.framework.util.name.Name;
import com.pinecone.ulf.util.lang.MultiTraitClassLoader;

public interface MultiMappingLoader extends MultiClassScopeLoader, MultiTraitClassLoader {
    @Override
    Class<?> load( Name simpleName ) throws ClassNotFoundException ;

    // Directly by it`s name.
    @Override
    Class<?> loadByName( Name simpleName ) throws ClassNotFoundException ;

    // Scanning class`s annotations, methods or others.
    @Override
    Class<?> loadInClassTrait( Name simpleName ) throws ClassNotFoundException ;

    @Override
    MultiMappingLoader updateScope();

    @Override
    List<Class<? > > loads( Name name ) ;

    @Override
    List<Class<? > > loadsByName( Name simpleName );

    @Override
    List<Class<? > > loadsInClassTrait( Name simpleName ) ;
}
