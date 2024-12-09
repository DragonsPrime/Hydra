package com.pinecone.hydra.umct.protocol.compiler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.pinecone.hydra.umct.bind.ArgParam;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

public abstract class ArchIfacCompiler extends ArchIfaceInspector implements IfaceCompiler {
    protected ClassLoader     mClassLoader;
    protected CompilerEncoder mCompilerEncoder;

    public ArchIfacCompiler( ClassPool classPool, ClassLoader classLoader, CompilerEncoder encoder ) {
        super( classPool );

        this.mClassLoader     = classLoader;
        this.mCompilerEncoder = encoder;
    }

    public ArchIfacCompiler( ClassPool classPool, ClassLoader classLoader ) {
        this( classPool, classLoader, CompilerEncoder.DefaultMethodArgumentsCompilerEncoder );
    }

    protected String annotationKeyNormalize( String bad ) {
        if ( bad != null ) {
            bad = bad.trim();
            if ( bad.startsWith( "\"" ) ) {
                return bad.replace( "\"", "" );
            }
        }
        return bad;
    }

    @Override
    public List<ParamsDigest > inspectArgParams( MethodDigest methodDigest, CtMethod method ) {
        List<ParamsDigest > argParams = null;

        ParameterAnnotationsAttribute paramAnnotationsAttr = ( ParameterAnnotationsAttribute) method.getMethodInfo().
                getAttribute(ParameterAnnotationsAttribute.visibleTag );

        if ( paramAnnotationsAttr != null ) {
            Annotation[][] parameterAnnotations = paramAnnotationsAttr.getAnnotations();
            if ( parameterAnnotations.length > 0 ) {
                argParams = new ArrayList<>();
            }

            for ( int i = 0; i < parameterAnnotations.length; ++i ) {
                for ( Annotation annotation : parameterAnnotations[ i ] ) {
                    if ( ArgParam.class.getName().equals(annotation.getTypeName()) ) {
                        String name   = annotation.getMemberValue("name") != null ? annotation.getMemberValue("name").toString() : null;
                        String value  = annotation.getMemberValue("value") != null ? annotation.getMemberValue("value").toString() : null;
                        String defVal = annotation.getMemberValue("defaultValue") != null ? annotation.getMemberValue("defaultValue").toString() : null;

                        boolean required =
                                annotation.getMemberValue("required") == null ||
                                        Boolean.parseBoolean(annotation.getMemberValue("required").toString());

                        argParams.add( new GenericParamsDigest(
                                methodDigest, i, this.annotationKeyNormalize(name), this.annotationKeyNormalize(value), this.annotationKeyNormalize(defVal), required )
                        );
                    }
                }
            }
        }

        return argParams;
    }

    protected MethodDigest compile ( ClassDigest classDigest, CtMethod method, CompilerEncoder encoder ) {
        try{
            CtClass[] pars;
            try{
                pars = method.getParameterTypes();
            }
            catch ( NotFoundException e ) {
                pars = null;
            }

            Class<? >[] parameters;
            if( pars != null ) {
                parameters = new Class<?>[ pars.length ];

                for ( int i = 0; i < pars.length; ++i ) {
                    CtClass par = pars[ i ];

                    String parName = par.getName();
                    Class<? > pc = this.reinterpretClass( parName );
                    parameters[ i ] = pc;
                }
            }
            else {
                parameters = null;
            }

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

            List<ParamsDigest > paramsDigests = this.inspectArgParams( ret, method );
            ret.apply( paramsDigests );
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
            ClassDigest classDigest = new GenericClassDigest( className );

            List<CtMethod > ifaceMethods = this.inspect( className, bAsIface );
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

    protected Class<?> reinterpretClass( String className ) throws ClassNotFoundException {
        switch (className) {
            case "boolean": {
                return boolean.class;
            }
            case "byte": {
                return byte.class;
            }
            case "char": {
                return char.class;
            }
            case "short": {
                return short.class;
            }
            case "int": {
                return int.class;
            }
            case "long": {
                return long.class;
            }
            case "float": {
                return float.class;
            }
            case "double": {
                return double.class;
            }
            case "void": {
                return void.class;
            }
            default:
                if ( className.endsWith( "[]" ) ) {
                    String elementTypeName = className.substring( 0, className.length() - 2 );
                    Class<?> elementType = this.reinterpretClass( elementTypeName );
                    return Array.newInstance( elementType, 0 ).getClass();
                }
                return this.mClassLoader.loadClass(className);
        }
    }
}
