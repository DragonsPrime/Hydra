package com.pinecone.hydra.umct.husky.machinery;

import com.pinecone.framework.util.lang.ClassScope;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.name.Name;
import com.pinecone.ulf.util.lang.ArchMultiScopeLoader;
import com.pinecone.ulf.util.lang.GenericPreloadClassInspector;
import com.pinecone.ulf.util.lang.PooledClassCandidateScanner;

import javassist.ClassPool;
import javassist.bytecode.annotation.Annotation;

public class HuskyMappingLoader extends ArchMultiScopeLoader implements MultiMappingLoader {
    protected PMCTContextMachinery mPMCTContextMachinery;

    protected HuskyMappingLoader( ClassScope classScope, ClassLoader classLoader, ClassPool classPool, PMCTContextMachinery machinery ) {
        super( classScope, classLoader, classPool, null, null );

        this.mPMCTContextMachinery = machinery;
        this.mClassScanner         = new PooledClassCandidateScanner( new HuskyMappingScopeSet( this.mClassLoader ), this.mClassLoader, this.mClassPool );
        this.mClassInspector       = new GenericPreloadClassInspector( this.mClassPool );
        this.mClassScanner.addExcludeFilter( new ExcludeHuskyMappingFilters( this.mClassInspector, this.mPMCTContextMachinery) );
    }

    protected HuskyMappingLoader( ClassScope classScope, ClassLoader classLoader, PMCTContextMachinery marshal ) {
        this( classScope, classLoader, ClassPool.getDefault(), marshal );
    }

    public HuskyMappingLoader( DynamicFactory factory, PMCTContextMachinery marshal ) {
        this( factory.getClassScope(), factory.getClassLoader(), marshal );
    }

    @Override
    protected boolean isAnnotationQualified( Annotation that, String szName ) {
        return false;
    }

    @Override
    public Class<? > load( Name simpleName ) throws ClassNotFoundException {
        return (Class<? > )super.load( simpleName );
    }

    // Directly by it`s name.
    @Override
    public Class<? > loadByName( Name simpleName ) throws ClassNotFoundException {
        return (Class<? > )super.loadByName( simpleName );
    }

    // Scanning class`s annotations, methods or others.
    @Override
    public Class<? > loadInClassTrait( Name simpleName ) throws ClassNotFoundException {
        return (Class<? > )super.loadInClassTrait( simpleName );
    }

    @Override
    protected Class<? > loadSingleByFullClassName( String szFullClassName ) {
        try {
            Class<?> clazz = this.mClassLoader.loadClass( szFullClassName );
            if( this.filter( clazz ) ) {
                return null;
            }

        }
        catch ( ClassNotFoundException e ) {
            return null;
        }

        return null;
    }

    @Override
    public MultiMappingLoader updateScope() {
        return (MultiMappingLoader)super.updateScope();
    }

    @Override
    public void clearCache() {
        this.mLoadedClassesPool.clear();
        this.mVisitedClasses.clear();
    }
}
