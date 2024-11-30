package com.pinecone.hydra.umct.protocol.compiler;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.pinecone.hydra.umct.stereotype.Iface;
import com.pinecone.ulf.util.lang.GenericPreloadClassInspector;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public abstract class ArchIfaceInspector extends GenericPreloadClassInspector implements IfaceInspector {
    public ArchIfaceInspector( ClassPool classPool ) {
        super( classPool );
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
}