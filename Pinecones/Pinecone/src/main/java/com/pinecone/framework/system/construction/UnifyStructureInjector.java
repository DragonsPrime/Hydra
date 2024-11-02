package com.pinecone.framework.system.construction;

import com.pinecone.framework.system.functions.Executable;
import com.pinecone.framework.unit.Units;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.hometype.JSONGet;
import com.pinecone.framework.util.json.hometype.MapStructure;
import com.pinecone.framework.util.json.hometype.ObjectInjector;

import java.beans.JavaBean;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class UnifyStructureInjector extends ObjectInjector {
    protected InstanceDispenser mInstanceDispenser;

    public UnifyStructureInjector( Class type, InstanceDispenser instanceDispenser ) {
        super( type );
        this.mInstanceDispenser = instanceDispenser;
    }

    public UnifyStructureInjector( Class type ) {
        this( type, null );
    }


    public ObjectTraits getObjectTraits( Field field ) {
        String szKey = null;

        ObjectBasicTraits traits = new ObjectBasicTraits();
        Annotation[] annotations = field.getAnnotations();
        for ( Annotation a : annotations ) {
            if( a instanceof JSONGet ) {
                szKey = ( (JSONGet) a ).value();
                traits.setMappedKey( szKey );
                traits.setTargetAnnotation( a );
                break;
            }
            else if( a instanceof MapStructure ) {
                szKey = ( (MapStructure) a ).value();
                traits.setMappedKey( szKey );
                traits.setTargetAnnotation( a );
                break;
            }
            else if( a instanceof Structure ) {
                Structure structure = (Structure) a;
                traits.fromStructure( structure );
                szKey = structure.mappedName();
                if( szKey.isEmpty() ) {
                    szKey = structure.name();
                }
                traits.setMappedKey( szKey );
                traits.setTargetAnnotation( a );
                break;
            }
            else if( a instanceof JavaBean ) {
                JavaBean javaBean = (JavaBean) a;
                szKey = javaBean.defaultProperty();
                traits.setMappedKey( szKey );
                traits.setBean( true );
                traits.setTargetAnnotation( a );
                break;
            }
        }

        if( szKey == null ) {
            return null;
        }
        traits.setAffiliatedType( field.getType() );
        return traits;
    }


    protected Object getFromMapStructure    ( Object mapLiked, String key ) {
        return Units.getFromMapStructure( mapLiked, key, true, true );
    }

    protected Object injectMapLinked     ( Object mapLiked, Class<?> type, Object instance ) {
        Field[] fields = type.getDeclaredFields();
        for ( Field field : fields ) {
            ReflectionUtils.makeAccessible( field );
            try {
                ObjectTraits traits = this.getObjectTraits( field );
                String szMappedKey;
                if( traits == null ) {
                    continue;
                }
                else if( traits.getMappedKey().isEmpty() ) {
                    szMappedKey = field.getName();
                    traits.setMappedKey( szMappedKey );
                }
                else {
                    szMappedKey = traits.getMappedKey();
                }

                if( traits.getName().isEmpty() ) {
                    traits.setName( field.getName() );
                }


                Object val = this.getFromMapStructure( mapLiked, this.getFieldName( szMappedKey ) );
                if( val == null ){
                    val = this.getFromMapStructure( mapLiked, szMappedKey );
                }
                if( val == null && szMappedKey.contains( "." ) ){
                    val = this.getValueFromMapRecursively( mapLiked, szMappedKey );
                }

                try {
                    Object j;
                    Class<? > insType   = traits.getDeclaredType();
                    Class<? > fieldType = field.getType();
                    Object ann          = traits.getTargetAnnotation();
                    if( ann instanceof Structure ) {
                        if( insType == Object.class || insType == null ) {
                            j = this.inject( val, fieldType );
                        }
                        else {
                            j = this.instancingUnitWithSpecificType( traits, val, field );
                        }
                    }
                    else {
                        j = this.inject( val, fieldType );
                    }

                    if( j == null ) {
                        j = this.instancingAndInject( traits, val, field );
                    }
                    field.set( instance, j );
                }
                catch ( IllegalArgumentException e ){
                    //e.printStackTrace();
                    field = null;
                }
            }
            catch ( IllegalAccessException e ){
                throw new IllegalStateException(e); // This should never be happened.
            }
        }

        return instance;
    }

    protected void ensureRegistered( ObjectTraits traits, Class<? > insType ) {
        if( !this.mInstanceDispenser.hasRegistered( insType ) ) {
            Object ann = traits.getTargetAnnotation();
            if( ann instanceof Structure ) {
                this.mInstanceDispenser.registerByImplicitFirstFound( insType, (Structure)ann );
            }
            else {
                this.mInstanceDispenser.registerByImplicitFirstFound( insType );
            }
        }
    }


    protected Object instancingAndInject( ObjectTraits traits, Object val, Field field ) {
        Class<? > insType = traits.getDeclaredType();
        if( this.mInstanceDispenser != null ) {
            if( insType == null || (insType == Object.class && field.getType() != Object.class) ) {
                insType = field.getType();
            }

            this.ensureRegistered( traits, insType );
            Object neoMember = this.mInstanceDispenser.allotInstance( insType );
            try{
                this.inject( val, insType, neoMember );
            }
            catch ( Exception e ) {
                throw new IllegalArgumentException( e );
            }
            return neoMember;
        }

        return null;
    }

    protected Object instancingUnitWithSpecificType( ObjectTraits traits, Object val, Field field ) {
        if( this.mInstanceDispenser == null || val == null ) {
            return null;
        }

        Class<? > fieldType = field.getType();
        Class<? > insType   = traits.getDeclaredType();
        this.ensureRegistered( traits, insType );
        if( fieldType.isAssignableFrom( val.getClass() ) ) {
            if( val instanceof Map ) {
                Map<Object, Object> cm = Units.newInstance( val.getClass() );
                for( Object v : ((Map) val).entrySet() ) {
                    Map.Entry kv = (Map.Entry) v;
                    Object neo = this.mInstanceDispenser.allotInstance( insType );
                    neo = this.injectMapLinked( kv.getValue(), insType, neo );
                    cm.put( kv.getKey(), neo );
                }
                return cm;
            }
            else if( val.getClass().isArray() ) {
                Object[] vals = new Object[ Array.getLength( val ) ];
                for ( int i = 0; i < vals.length; ++i ) {
                    Object neo = this.mInstanceDispenser.allotInstance( insType );
                    neo = this.injectMapLinked( Array.get( val, i ), insType, neo );
                    vals[i] = neo;
                }
                return vals;
            }
            else if( val instanceof Collection ) {
                Collection<Object> ib = Units.newInstance( val.getClass() );
                for( Object o : (Collection) val ) {
                    Object neo = this.mInstanceDispenser.allotInstance( insType );
                    neo = this.injectMapLinked( o, insType, neo );
                    ib.add( neo );
                }
                return ib;
            }
        }
        return val;
    }

    @Override
    public    Object inject      ( Map that, Class<?> type, Object instance ) {
        return this.injectMapLinked( that, type, instance );
    }

    protected Object getValueFromMapRecursively( Object mapLiked, String key ) {
        String[] keys = key.split("\\.|\\/");
        Object value = mapLiked;
        for ( String k : keys ) {
            value = this.getFromMapStructure( value, k );
        }
        return value;
    }

    @Override
    public Object inject              ( Object that, Class<?> type, Object instance ) throws Exception {
        if ( ObjectInjector.trialHomogeneity( that ) ){
            return that;
        }
        else if( type == Object.class ){
            return that;
        }
        else if( that instanceof Executable){
            return this.inject( (Executable) that );
        }
        else if ( that instanceof Collection){
            return this.inject( (Collection) that, type, instance );
        }
        else if ( that instanceof Map ){
            return this.inject( (Map) that, type, instance );
        }
        else {
            return this.injectMapLinked( that, type, instance );
        }
    }
}
