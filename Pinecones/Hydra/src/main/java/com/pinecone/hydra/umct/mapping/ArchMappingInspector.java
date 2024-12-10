package com.pinecone.hydra.umct.mapping;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.pinecone.hydra.umct.bind.ArgParam;
import com.pinecone.ulf.util.lang.GenericPreloadClassInspector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

public abstract class ArchMappingInspector extends GenericPreloadClassInspector implements MappingInspector {
    protected ClassLoader     mClassLoader;

    public ArchMappingInspector( ClassPool classPool, ClassLoader classLoader ) {
        super( classPool );

        this.mClassLoader = classLoader;
    }

    @Override
    public List<ParamsDigest> inspectArgParams( Object methodDigest, CtMethod method ) {
        List<ParamsDigest> argParams = null;

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

                        argParams.add( this.newParamsDigest(
                                methodDigest, i, this.annotationKeyNormalize(name), this.annotationKeyNormalize(value), this.annotationKeyNormalize(defVal), required )
                        );
                    }
                }
            }
        }

        return argParams;
    }

    protected ParamsDigest newParamsDigest( Object methodDigest, int parameterIndex, String name, String value, String defaultValue, boolean required ) {
        return new GenericParamsDigest(
                parameterIndex, this.annotationKeyNormalize(name), this.annotationKeyNormalize(value), this.annotationKeyNormalize(defaultValue), required
        );
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

    protected <T > T getAnnotation( CtClass ctClass, Class<T > annotationClass ) {
        try {
            Object rawAnnotation = ctClass.getAnnotation( annotationClass );
            return annotationClass.cast( rawAnnotation );
        }
        catch ( ClassNotFoundException e ) {
            return null;
        }
    }

    protected <T> T getAnnotation( CtMethod ctMethod, Class<T> annotationClass ) {
        try {
            Object rawAnnotation = ctMethod.getAnnotation( annotationClass );
            return annotationClass.cast( rawAnnotation );
        }
        catch ( ClassNotFoundException e ) {
            return null;
        }
    }

    protected Class<? >[] getParameters ( CtMethod method ) throws ClassNotFoundException {
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

        return parameters;
    }

}
