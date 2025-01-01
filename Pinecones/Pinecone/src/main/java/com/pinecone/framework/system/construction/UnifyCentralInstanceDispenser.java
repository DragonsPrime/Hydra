package com.pinecone.framework.system.construction;

import com.pinecone.framework.system.Nullable;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.lang.GenericDynamicFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UnifyCentralInstanceDispenser implements StructureInstanceDispenser {
    protected final Map<Class<?>, Object >                 mSingletonObjects   = new ConcurrentHashMap<>();
    protected final Map<Class<?>, StructureDefinition >    mObjectDefinitions  = new ConcurrentHashMap<>();
    protected final Map<Class<?>, InstancePool<? > >       mObjectInstancer    = new ConcurrentHashMap<>(); // Pool is immutable.
    protected final DynamicFactory                         mCentralFactory     ;

    public UnifyCentralInstanceDispenser( DynamicFactory factory ) {
        this.mCentralFactory = factory;
    }

    public UnifyCentralInstanceDispenser() {
        this( new GenericDynamicFactory() );
    }

    /**
     * update
     * The pool is immutable.
     * @param type the object`s type
     * @param definition the object`s definition
     * @return null for nonsexist or definition which just inserted.
     */
    @Override
    public StructureDefinition update( Class<?> type, StructureDefinition definition ) {
        if( this.mObjectDefinitions.containsKey( type ) ) {
            return this.mObjectDefinitions.put( type, definition );
        }
        return null;
    }

    @Override
    public InstanceDispenser register( Class<?> type, StructureDefinition definition, InstancePool<? > pool ) {
        if( pool == null ) {
            pool = this.defaultInstancePool( type, definition );
        }
        this.mObjectDefinitions.putIfAbsent( type, definition );
        this.mObjectInstancer.putIfAbsent( type, pool );
        return this;
    }

    @Override
    public InstanceDispenser register( Class<?> type, StructureDefinition definition ) {
        return this.register( type, definition, this.defaultInstancePool( type, definition ) );
    }

    protected StructureDefinition defaultDefinition( Class<?> type, Structure structure ) {
        StructureDefinition definition = new GenericStructureDefinition( structure );
        if( definition.getType() == Object.class && type != Object.class ) {
            definition.setType( type );
        }

        return definition;
    }

    @Override
    public InstanceDispenser register( Class<?> type, Structure structure ) {
        return this.register( type, structure, null );
    }

    @Override
    public InstanceDispenser register( Class<?> type, Structure structure, @Nullable InstancePool<?> pool ) {
        StructureDefinition definition = this.defaultDefinition( type, structure );
        if( pool == null ) {
            pool = this.defaultInstancePool( type, definition );
        }
        return this.register( type, definition, pool );
    }

    @Override
    public InstanceDispenser register( Class<?> type ) {
        return this.register( type, (InstancePool<?>) null );
    }

    protected Structure foundClassDeclaredStructure( Class<?> type ) {
        Annotation[] annotations = type.getAnnotations();
        for( Annotation annotation : annotations ) {
            if( annotation instanceof Structure ) {
                return (Structure)annotation;
            }
        }

        return null;
    }

    @Override
    public InstanceDispenser register( Class<?> type, @Nullable InstancePool<?> pool ) {
        Structure target = this.foundClassDeclaredStructure( type );
        if( target != null ) {
            return this.register( type, target, pool );
        }

        StructureDefinition definition = new GenericStructureDefinition( type );
        return this.register( type, definition, pool );
    }

    protected InstancePool<? > defaultInstancePool( Class<?> type, StructureDefinition definition ) {
        if( definition.getProvide() != void.class && definition.getProvide() != Object.class ) {
            Object o = this.tryInstancingFromProvider( type, definition, null );
            if( o instanceof InstancePool ) {
                return (InstancePool)o;
            }
        }

        if( definition.getCycle() == ReuseCycle.Disposable || definition.getCycle().isSingleton() ) {
            return new GenericDynamicInstancePool<>( this.mCentralFactory, 0, type );
        }
        return new GenericDynamicInstancePool<>( this.mCentralFactory, 4, type );
    }

    @Override
    public InstanceDispenser register( StructureDefinition definition ) {
        return this.register( definition.getType(), definition );
    }

    @Override
    public InstanceDispenser registerByImplicitFirstFound( Class<?> type, @Nullable Structure structure ) {
        return this.registerByImplicitFirstFound( type, structure, null );
    }

    @Override
    public InstanceDispenser registerByImplicitFirstFound( Class<?> type, @Nullable Structure structure, @Nullable InstancePool<?> pool ) {
        Structure target = this.foundClassDeclaredStructure( type );
        if( target == null ) {
            target = structure;
        }

        if( target == null ) {
            StructureDefinition definition = new GenericStructureDefinition( type );
            return this.register( type, definition, pool );
        }
        return this.register( type, target, pool );
    }

    @Override
    public InstanceDispenser registerByImplicitFirstFound( Class<?> type ) {
        return this.registerByImplicitFirstFound( type, null, null );
    }

    @Override
    public boolean  hasRegistered( Class<? > type ) {
        return this.mObjectDefinitions.containsKey( type );
    }

    protected Object invokeInstancingProvider( Class<? > provider, String szMethodName ) {
        Object provide = this.mCentralFactory.optNewInstance( provider, null );
        Method pm;
        try{
            pm = provide.getClass().getMethod( szMethodName );
        }
        catch ( NoSuchMethodException nme ) {
            return null;
        }

        try {
            return ReflectionUtils.tryAccessibleInvoke( pm, provide );
        }
        catch ( InvocationTargetException | IllegalArgumentException e ) {
            return null;
        }
    }

    protected Object tryInstancingFromProvider( Class<?> type, StructureDefinition definition, @Nullable Structure instanceStructure ) {
        if( instanceStructure != null ) {
            Class<? > provider = instanceStructure.provider();
            if( DynamicInstancePool.class.isAssignableFrom( provider ) ) {
                if( instanceStructure.cycle() == ReuseCycle.Disposable || instanceStructure.cycle().isSingleton() ) {
                    return (InstancePool<?>) this.mCentralFactory.optNewInstance( provider, new Object[]{ this.mCentralFactory, 0, type } );
                }
                return (InstancePool<?>) this.mCentralFactory.optNewInstance( provider, new Object[]{ this.mCentralFactory, 4, type } );
            }
            else if( InstancePool.class.isAssignableFrom( provider ) ) {
                return (InstancePool<?>) this.mCentralFactory.optNewInstance( provider, null );
            }
            else if( instanceStructure.type() != void.class && instanceStructure.type() != Object.class && !instanceStructure.providerMethod().isEmpty() ) {
                Object ret = this.invokeInstancingProvider( provider, instanceStructure.providerMethod() );
                if( ret != null ) {
                    return ret;
                }
            }
        }

        Class<? > provider = definition.getProvide();
        if( DynamicInstancePool.class.isAssignableFrom( definition.getProvide() ) ) {
            if( definition.getCycle() == ReuseCycle.Disposable || definition.getCycle().isSingleton() ) {
                return (InstancePool<?>) this.mCentralFactory.optNewInstance( provider, new Object[]{ this.mCentralFactory, 0, type } );
            }
            return (InstancePool<?>) this.mCentralFactory.optNewInstance( provider, new Object[]{ this.mCentralFactory, 4, type } );
        }
        else if( InstancePool.class.isAssignableFrom( definition.getProvide() ) ) {
            return (InstancePool<?>) this.mCentralFactory.optNewInstance( provider, null );
        }
        else if( definition.getType() != void.class && definition.getType() != Object.class && !definition.getProvideMethod().isEmpty() ) {
            Object ret = this.invokeInstancingProvider( provider, definition.getProvideMethod() );
            if( ret != null ) {
                return ret;
            }
        }
        return null;
    }


    @Override
    public <T> T allotInstance( Class<T> type, @Nullable Structure instanceStructure ) {
        StructureDefinition definition = this.mObjectDefinitions.get( type );
        if( definition == null ) {
            return null; // Unregistered.
        }
        Class<? > innerType = definition.getType();
        if( innerType == Object.class ) {
            innerType = type;
        }

        Object t = this.tryInstancingFromProvider( type, definition, instanceStructure );
        if( t != null ) {
            return type.cast( t );
        }

        Object b = this.mSingletonObjects.get( type );
        if ( b != null ) {
            if( instanceStructure != null && !instanceStructure.cycle().isSingleton() ) {
                return type.cast( this.mObjectInstancer.get( innerType ).allocate() );
            }
            return type.cast( b );
        }

        if(
                definition.getCycle() == ReuseCycle.Disposable ||
                ( instanceStructure != null && instanceStructure.cycle() == ReuseCycle.Disposable )
        ) {
            return type.cast( this.mObjectInstancer.get( innerType ).allocate() );
        }

        T obj = type.cast( this.mObjectInstancer.get( innerType ).allocate() );
        if ( definition.getCycle().isSingleton() ) {
            this.mSingletonObjects.put( innerType, obj );
        }
        return obj;
    }

    @Override
    public <T > T allotInstance( Class<T> type ) {
        return this.allotInstance( type, null );
    }

    @Override
    @SuppressWarnings("unchecked")
    public void free( Class<?> type, Object instance ) {
        InstancePool pool = this.mObjectInstancer.get( type );
        if( pool != null ) {
            pool.free( instance );
        }
        else {
            throw new IllegalArgumentException( type.getName() + " is not owned instance." );
        }
    }

    @Override
    public void free( Object instance ) {
        this.free( instance.getClass(), instance );
    }

    @Override
    public StructureDefinition getStructureDefinition( Class<?> type ) {
        return this.mObjectDefinitions.get( type );
    }

    @Override
    public InstancePool<? > getInstancePool( Class<?> type ) {
        return this.mObjectInstancer.get( type );
    }

    @Override
    public DynamicFactory getCentralFactory() {
        return this.mCentralFactory;
    }
}
