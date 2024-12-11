package com.pinecone.hydra.umct.husky.compiler;

import java.util.List;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public abstract class ArchIfacCompiler extends ArchIfaceInspector implements IfaceCompiler {
    protected CompilerEncoder mCompilerEncoder;

    public ArchIfacCompiler( ClassPool classPool, ClassLoader classLoader, CompilerEncoder encoder ) {
        super( classPool, classLoader );

        this.mCompilerEncoder = encoder;
    }

    public ArchIfacCompiler( ClassPool classPool, ClassLoader classLoader ) {
        this( classPool, classLoader, CompilerEncoder.DefaultMethodArgumentsCompilerEncoder );
    }

    protected MethodDigest compile ( ClassDigest classDigest, CtMethod method, CompilerEncoder encoder ) {
        try{
            Class<? >[] parameters = this.getParameters( method );


            CtClass retType;
            try{
                retType = method.getReturnType();
            }
            catch ( NotFoundException e ) {
                retType = null;
            }


            Class<? > returnType;
            if( retType != null ) {
                returnType = this.reinterpretClass( retType.getName() );
            }
            else {
                returnType = null;
            }

            MethodDigest ret;
            if( encoder != null ) {
                ret = new DynamicMethodPrototype(
                        classDigest, this.getIfaceMethodName( method ), method.getName(), parameters, returnType, encoder, null
                );
            }
            else {
                ret = new GenericMethodDigest(
                        classDigest, this.getIfaceMethodName( method ), method.getName(), parameters, returnType, null
                );
            }

            List<IfaceParamsDigest> ifaceParamsDigests = this.inspectArgIfaceParams( ret, method );
            ret.apply(ifaceParamsDigests);
            return ret;
        }
        catch ( ClassNotFoundException e ) {
            throw new CompileException( e );
        }
    }

    @Override
    public ClassDigest compile ( String className, boolean bAsIface ) {
        return this.compile( className, bAsIface, this.mCompilerEncoder );
    }

    @Override
    public ClassDigest compile ( Class<? > clazz, boolean bAsIface ) {
        return this.compile( clazz.getName(), bAsIface );
    }

    @Override
    public ClassDigest compile( Class<?> clazz, boolean bAsIface, CompilerEncoder encoder ) {
        return this.compile( clazz.getName(), bAsIface, encoder );
    }

    @Override
    public ClassDigest reinterpret( Class<?> clazz, boolean bAsIface ) {
        return this.compile( clazz, bAsIface, null );
    }

    @Override
    public ClassDigest reinterpret( String className, boolean bAsIface ) {
        return this.compile( className, bAsIface, null );
    }

    @Override
    public ClassDigest compile( String className, boolean bAsIface, CompilerEncoder encoder ) {
        try{
            List<CtMethod > ifaceMethods = this.inspect( className, bAsIface );
            if ( ifaceMethods.isEmpty() ) {
                return null;
            }

            ClassDigest classDigest = new GenericClassDigest( className );
            for ( CtMethod ctMethod : ifaceMethods ) {
                MethodDigest methodDigest = this.compile( classDigest, ctMethod, encoder );
                classDigest.addMethod( methodDigest );
            }

            return classDigest;
        }
        catch ( NotFoundException e ) {
            throw new CompileException( e );
        }
    }

    @Override
    public CompilerEncoder getCompilerEncoder() {
        return this.mCompilerEncoder;
    }
}
