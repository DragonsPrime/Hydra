package com.pinecone.framework.util.json;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pinecone.framework.system.prototype.ObjectiveEvaluator;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.UnitHelper;

public class ObjectJSONCursorUnmarshal extends ArchCursorParser {
    public static JSONDecoder INNER_OBJECT_DECODER = new JSONObjectDecoder() {
        @Override
        protected void set( Object self, String key, Object val ) {
            ObjectiveEvaluator.MapStructures.set( self, key, val );
        }
    };

    public static JSONDecoder INNER_ARRAY_DECODER = new JSONArrayDecoder() {
        @Override
        @SuppressWarnings( "unchecked" )
        protected void add( Object self, Object parent, Object indexKey, Object val ) {
            if( self.getClass().isArray() ) {
                Object[] ref = (Object[])self; // Fuck java, no pointer.
                ref[ 0 ] = UnitHelper.append( (Object[])ref[ 0 ], val );
            }
            else if( self instanceof Collection ) {
                ((Collection) self).add( val );
            }
        }
    };


    protected Class<? >      mClassType;

    public ObjectJSONCursorUnmarshal( Reader reader, Class<? > classType ) {
        super( reader );
        this.mClassType      = classType;
    }

    public ObjectJSONCursorUnmarshal( InputStream inputStream, Class<? > classType ) throws JSONParseException {
        this((Reader)( new InputStreamReader(inputStream)), classType );
    }

    public ObjectJSONCursorUnmarshal( String s, Class<? > classType ) {
        this((Reader)( new StringReader(s)), classType );
    }


    @Override
    protected Object newJSONArray( Object indexKey, ArchCursorParser parser, Object parent, Object[] args ) {
        try{
            Class<? > thisType    = this.mClassType;
            Type fieldGenericType = null;
            if( parent != null ){
                fieldGenericType = ObjectiveEvaluator.MapStructures.getFieldGenericType( parent, indexKey.toString() );
                thisType = ObjectiveEvaluator.MapStructures.getType( parent, indexKey );
            }

            if( thisType.equals( List.class ) || thisType.equals( Void.class ) || thisType.equals( Object.class ) ) {
                thisType = JSONArraytron.class;
                if( fieldGenericType != null ) {
                    String genericTypeName = fieldGenericType.getTypeName();
                    if( !genericTypeName.equals( "?" ) && !genericTypeName.equals( Object.class.getSimpleName() ) ) {
                        thisType = ArrayList.class;
                    }
                }
            }
            else if( thisType.equals( Set.class ) ) {
                thisType = LinkedHashSet.class;
            }

            Object    self;
            if( thisType.isArray() ) {
                self = new Object[]{ new Object[ 0 ] };  // Object[]*, ptr -> Object[]
            }
            else {
                self = thisType.getConstructor().newInstance();
            }

            ObjectJSONCursorUnmarshal.INNER_ARRAY_DECODER.decode( self, parent, indexKey,this, fieldGenericType );
            if( self.getClass().isArray() ) {
                return Array.get( self, 0 );
            }
            return self;
        }
        catch ( NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            return null;
        }
        catch ( InstantiationException e1 ) {
            throw new JSONParseException( e1 );
        }
    }

    @Override
    protected Object newJSONObject( Object indexKey, ArchCursorParser parser, Object parent, Object[] args ) {
        try{
            Class<? > thisType    = this.mClassType;
            Type fieldGenericType = null;
            if( parent != null ){
                thisType = ObjectiveEvaluator.MapStructures.getType( parent, indexKey );
                fieldGenericType = ObjectiveEvaluator.MapStructures.getFieldGenericType( parent, indexKey.toString() );
            }

            if( thisType == null || thisType.equals( Map.class ) || thisType.equals( Void.class ) || thisType.equals( Object.class ) ) {
                thisType = JSONMaptron.class;
            }

            if( args != null && args.length > 0 ) {
                Object dyType = args[ 0 ];
                Type eleType  = (Type) dyType;
                if( eleType != null ) {
                    String genericTypeName = ReflectionUtils.extractSoloGenericClassName( eleType.getTypeName() );
                    if( genericTypeName != null && !genericTypeName.equals( "?" ) && !genericTypeName.equals( Object.class.getSimpleName() ) ) {
                        try{
                            thisType = this.getClass().getClassLoader().loadClass( genericTypeName );
                        }
                        catch ( ClassNotFoundException e ) {
                            thisType = JSONMaptron.class;
                        }
                    }
                }
            }
            Object    self     = thisType.getConstructor().newInstance();

            ObjectJSONCursorUnmarshal.INNER_OBJECT_DECODER.decode( self, parent, indexKey, this, fieldGenericType );
            return self;
        }
        catch ( NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
            return null;
        }
        catch ( InstantiationException e1 ) {
            throw new JSONParseException( e1 );
        }
    }
}