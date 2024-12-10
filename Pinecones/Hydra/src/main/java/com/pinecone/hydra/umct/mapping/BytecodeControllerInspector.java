package com.pinecone.hydra.umct.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.pinecone.hydra.umct.AddressMapping;
import com.pinecone.hydra.umct.stereotype.Controller;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class BytecodeControllerInspector extends ArchMappingInspector implements ControllerInspector {
    public BytecodeControllerInspector( ClassPool classPool, ClassLoader classLoader ) {
        super( classPool, classLoader );
    }

    public BytecodeControllerInspector( ClassPool classPool ) {
        this( classPool, Thread.currentThread().getContextClassLoader() );
    }

    @Override
    public List<CtMethod > inspect( String className ) throws NotFoundException {
        List<CtMethod> mappingMethods = new ArrayList<>();
        CtClass ctClass = this.mClassPool.get( className );

        boolean classHasControllerAnnotation = this.hasOwnAnnotation( ctClass, Controller.class );

        if ( classHasControllerAnnotation ) {
            for ( CtMethod method : ctClass.getDeclaredMethods() ) {
                if ( Modifier.isPublic( method.getModifiers() ) ) {
                    if ( this.methodHasAnnotation( method, AddressMapping.class ) ) {
                        mappingMethods.add( method );
                    }
                }
            }
        }

        return mappingMethods;
    }

    @Override
    public List<CtMethod> inspect( Class<?> clazz ) throws NotFoundException {
        return this.inspect( clazz.getName() );
    }

    @Override
    public List<MappingDigest > characterize( String className ) throws NotFoundException {
        try{
            List<MappingDigest > mappingDigests = new ArrayList<>();

            CtClass ctClass = this.mClassPool.get(className);

            if ( !this.hasOwnAnnotation( ctClass, Controller.class ) ) {
                return mappingDigests;
            }

            AddressMapping classMapping = this.getAnnotation( ctClass, AddressMapping.class );
            String[] classLevelMappings;
            if ( classMapping != null )  {
                classLevelMappings = classMapping.value();
            }
            else  {
                classLevelMappings = new String[]{};
            }

            for ( CtMethod method : ctClass.getDeclaredMethods() ) {
                if ( !Modifier.isPublic( method.getModifiers() ) || !this.methodHasAnnotation( method, AddressMapping.class ) ) {
                    continue;
                }

                AddressMapping methodMapping = this.getAnnotation( method, AddressMapping.class );
                String[] methodLevelMappings = methodMapping != null ? methodMapping.value() : new String[]{};
                boolean isRelative = methodMapping != null && methodMapping.relative();

                List<String > fullAddresses = new ArrayList<>();
                for ( String classMappingValue : classLevelMappings ) {
                    for ( String methodMappingValue : methodLevelMappings ) {
                        if ( isRelative ) {
                            fullAddresses.add( classMappingValue + methodMappingValue );
                        }
                        else {
                            fullAddresses.add( methodMappingValue );
                        }
                    }
                }

                if ( fullAddresses.isEmpty() && methodLevelMappings.length == 0 ) {
                    continue;
                }

                List<ParamsDigest> paramsDigests = this.inspectArgParams( null, method );
                Class<? >[] parameters = this.getParameters( method );

                Class<? > auth       = this.reinterpretClass( className );
                Method mappedMethod  = auth.getMethod( method.getName(), parameters );
                MappingDigest digest = new GenericMappingDigest(
                        fullAddresses.isEmpty() ? methodLevelMappings[ 0 ] : fullAddresses.get( 0 ),
                        parameters,
                        this.reinterpretClass( method.getReturnType().getName() ),
                        auth,
                        mappedMethod,
                        paramsDigests
                );

                digest.apply( paramsDigests );
                mappingDigests.add( digest );
            }

            return mappingDigests;
        }
        catch ( ClassNotFoundException | NoSuchMethodException e ) {
            throw new InspectException( e );
        }
    }

    @Override
    public List<MappingDigest > characterize( Class<?> clazz ) throws NotFoundException {
        return this.characterize( clazz.getName() );
    }
}