package com.pinecone.hydra.umct.protocol.compiler;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.pinecone.hydra.umct.mapping.ArchMappingInspector;
import com.pinecone.hydra.umct.mapping.ParamsDigest;
import com.pinecone.hydra.umct.stereotype.Iface;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public abstract class ArchIfaceInspector extends ArchMappingInspector implements IfaceInspector {
    public ArchIfaceInspector( ClassPool classPool, ClassLoader classLoader ) {
        super( classPool, classLoader );
    }

    @Override
    public List<CtMethod> inspect( Class<?> clazz, boolean bAsIface ) throws NotFoundException {
        return this.inspect( clazz.getName(), bAsIface );
    }

    @Override
    public List<CtMethod> inspect( String className, boolean bAsIface ) throws NotFoundException {
        List<CtMethod> ifaceMethods = new ArrayList<>();
        CtClass ctClass = this.mClassPool.get( className );

        boolean classHasIfaceAnnotation = this.hasOwnAnnotation( ctClass, Iface.class );

        for ( CtMethod method : ctClass.getDeclaredMethods() ) {
            if ( Modifier.isPublic( method.getModifiers() ) ) {
                if ( bAsIface || classHasIfaceAnnotation || this.methodHasAnnotation( method, Iface.class ) ) {
                    ifaceMethods.add( method );
                }
            }
        }

        return ifaceMethods;
    }

    @Override
    public String getIfaceMethodName( CtMethod method ) throws ClassNotFoundException {
        String ifaceName = method.getName();

        Object annotation = method.getAnnotation( Iface.class );
        if ( annotation != null ) {
            Iface iface = (Iface) annotation;
            if ( !iface.name().isEmpty() ) {
                ifaceName = iface.name();
            }
        }

        return ifaceName;
    }

    @SuppressWarnings( "unchecked" )
    protected List<IfaceParamsDigest> inspectArgIfaceParams( Object methodDigest, CtMethod method ) {
        return (List<IfaceParamsDigest> ) (List) this.inspectArgParams( methodDigest, method );
    }

    @Override
    protected ParamsDigest newParamsDigest( Object methodDigest, int parameterIndex, String name, String value, String defaultValue, boolean required ) {
        return new GenericIfaceParamsDigest(
                (MethodDigest) methodDigest, parameterIndex, this.annotationKeyNormalize(name), this.annotationKeyNormalize(value), this.annotationKeyNormalize(defaultValue), required
        );
    }
}