package com.pinecone.framework.unit;

import com.pinecone.framework.system.prototype.ObjectiveEvaluator;
import com.pinecone.framework.system.prototype.Objectom;
import com.pinecone.framework.util.lang.DynamicFactory;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

import java.util.Collection;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public final class Units {
    public static <T> Collection<T> spawnExtendParent( Collection<T > parent ) {
        return Units.spawnExtendParent( parent, ArrayList.class );
    }

    public static <K, V > Map<K, V > spawnExtendParent( Map<K, V > parent ) {
        return Units.spawnExtendParent( parent, TreeMap.class );
    }

    @SuppressWarnings( "unchecked" )
    public static <C > C spawnExtendParent( Object parent, Class<? > basic ) {
        Object subList = null;
        try{
            subList = parent.getClass().getDeclaredConstructor().newInstance();
        }
        catch ( IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e ) {
            try{
                subList = basic.getDeclaredConstructor().newInstance();
            }
            catch ( IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e1 ) {
                throw new IllegalArgumentException( "Illegal 'basic' class given.", e1 );
            }
        }
        return (C)subList;
    }

    @SuppressWarnings( "unchecked" )
    public static <C > C newInstance( Class<? > clazz, Object...args ) {
        Object subList = null;
        try{
            if( args.length == 0 ) {
                subList = clazz.getDeclaredConstructor().newInstance();
            }
            else {
                subList = DynamicFactory.DefaultFactory.newInstance( clazz, null, args );
            }
        }
        catch ( IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e1 ) {
            throw new IllegalArgumentException( "Illegal 'class' class given.", e1 );
        }
        return (C)subList;
    }


    /**
     * getFromMapStructure
     * Similar to other dynamic languages(e.g. Javascript/PHP/Python/etc.), which is using to retrieve the value from the potential gettable object.
     * @param mapLiked Any object that resembles the map operation (get/set/index/query/etc.) in form.
     * @param key The string key( number-fmt/string-key/etc. ) that uses to retrieve the value from the map-liked object.
     * @param bIncludeIterable if true, for iterable object will uses the enum-index as the key.
     * @param bIncludeAnyPotentialMapLiked if true, for other any potential map-liked objects will try get from bean-liked-object.
     * @return null for not found, object for the value which is affiliated to the key.
     */
    public static Object getFromMapStructure ( Object mapLiked, String key, boolean bIncludeIterable, boolean bIncludeAnyPotentialMapLiked ) {
        if( mapLiked instanceof Map ) {
            return ((Map) mapLiked).get( key );
        }
        else if( mapLiked instanceof Objectom ) {
            return ((Objectom) mapLiked).get( key );
        }
        else if( mapLiked instanceof List ) {
            try{
                return ((List) mapLiked).get( Integer.parseInt( key ) );
            }
            catch ( NumberFormatException e ) {
                return null;
            }
        }
        else if( mapLiked.getClass().isArray() ) {
            try{
                return Array.get( mapLiked, Integer.parseInt( key ) );
            }
            catch ( NumberFormatException e ) {
                return null;
            }
        }
        else if( mapLiked instanceof Iterable && bIncludeIterable ) {
            try{
                int k = Integer.parseInt( key );
                int i = 0;
                for( Object v : (Iterable) mapLiked ) {
                    if( i == k ) {
                        return v;
                    }
                    ++i;
                }
                return null;
            }
            catch ( NumberFormatException e ) {
                return null;
            }
        }
        else if( mapLiked.getClass().isPrimitive() ) {
            return null;
        }
        else if( mapLiked.getClass().isEnum() ) {
            return null;
        }
        else if( mapLiked instanceof Number ) {
            return null;
        }
        else if( mapLiked instanceof String ) {
            return null;
        }

        if( bIncludeAnyPotentialMapLiked ) {
            return ObjectiveEvaluator.MapStructures.classGet( mapLiked, key );
        }
        return null;
    }

    public static Object getValueFromMapStructureRecursively( Object mapLiked, String key, String szSplitRegex, boolean bIncludeIterable, boolean bIncludeAnyPotentialMapLiked ) {
        String[] keys = key.split( szSplitRegex );
        Object value = mapLiked;
        for ( String k : keys ) {
            value = Units.getFromMapStructure(
                    value, k, bIncludeIterable, bIncludeAnyPotentialMapLiked
            );
        }
        return value;
    }

    public static Object getValueFromMapStructureRecursively( Object mapLiked, String key ) {
        return Units.getValueFromMapStructureRecursively(
                mapLiked, key, "\\.|\\/", true, true
        );
    }
}
