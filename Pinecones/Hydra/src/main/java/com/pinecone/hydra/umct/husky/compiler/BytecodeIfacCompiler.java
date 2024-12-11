package com.pinecone.hydra.umct.husky.compiler;

import java.util.ArrayList;
import java.util.List;

import com.pinecone.hydra.umct.mapping.MappingDigest;

import javassist.ClassPool;

public class BytecodeIfacCompiler extends ArchIfacCompiler implements InterfacialCompiler {
    public BytecodeIfacCompiler( ClassPool classPool, ClassLoader classLoader, CompilerEncoder encoder ) {
        super( classPool, classLoader, encoder );
    }

    public BytecodeIfacCompiler( ClassPool classPool, ClassLoader classLoader ) {
        super( classPool, classLoader );
    }

    public BytecodeIfacCompiler( ClassPool classPool ) {
        super( classPool, Thread.currentThread().getContextClassLoader() );
    }

    @Override
    public IfaceMappingDigest compile( MappingDigest digest ) {
        return new GenericIfaceMappingDigest( digest, this.mCompilerEncoder );
    }

    @Override
    public List<IfaceMappingDigest> compile( List<MappingDigest> digests ) {
        List<IfaceMappingDigest> result = new ArrayList<>( digests.size() );
        for ( MappingDigest digest : digests ) {
            result.add( this.compile( digest ) );
        }
        return result;
    }
}
