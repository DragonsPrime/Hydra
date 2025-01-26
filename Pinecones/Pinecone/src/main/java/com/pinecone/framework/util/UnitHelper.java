package com.pinecone.framework.util;

import java.lang.reflect.Array;
import java.util.List;

public final class UnitHelper {
    public static int accumulateInt( int from, int to, List<Integer > list ){
        int sum = 0;
        for( int i = from; i < to; i++ ){
            sum += list.get( i );
        }
        return sum;
    }

    public static int accumulateInt( List<Integer > list ) {
        return UnitHelper.accumulateInt( 0, list.size(), list );
    }

    public static double accumulateDouble( int from, int to, List<Double > list ){
        double sum = 0;
        for( int i = from; i < to; ++i ){
            sum += list.get( i );
        }
        return sum;
    }

    public static double accumulateDouble( List<Double > list ) {
        return UnitHelper.accumulateDouble( 0, list.size(), list );
    }


    @SuppressWarnings( "unchecked" )
    public static <T> T[] append( T[] original, int currentSize, T element ) {
        if ( currentSize >= original.length ) {
            T[] newArray = (T[]) Array.newInstance( original.getClass().getComponentType(), original.length + 1 );
            System.arraycopy( original, 0, newArray, 0, original.length );
            newArray[ currentSize ] = element;
            return newArray;
        }
        else {
            original[ currentSize ] = element;
            return original;
        }
    }

    public static <T> T[] append( T[] original, T element ) {
        return UnitHelper.append( original, original.length, element );
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T[] remove( T[] original, int index ) {
        if ( index < 0 || index >= original.length ) {
            throw new IndexOutOfBoundsException( "Index: " + index + ", Size: " + original.length );
        }

        T[] newArray = (T[]) Array.newInstance( original.getClass().getComponentType(), original.length - 1 );
        for ( int i = 0, j = 0; i < original.length; ++i ) {
            if ( i != index ) {
                newArray[ j++ ] = original[ i ];
            }
        }
        return newArray;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T[] popBack( T[] original ) {
        if ( original.length == 0 ) {
            throw new IllegalStateException( "Cannot pop from an empty array." );
        }

        T[] newArray = (T[]) Array.newInstance( original.getClass().getComponentType(), original.length - 1 );
        System.arraycopy( original, 0, newArray, 0, original.length - 1 );
        return newArray;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T[] pollFirst( T[] original ) {
        if ( original.length == 0 ) {
            throw new IllegalStateException( "Cannot poll from an empty array." );
        }

        T firstElement = original[ 0 ];
        T[] newArray = (T[]) Array.newInstance( original.getClass().getComponentType(), original.length - 1 );
        System.arraycopy( original, 1, newArray, 0, original.length - 1 );
        return newArray;
    }

    @SuppressWarnings( "unchecked" )
    public static <T> T[] insert( T[] original, int index, T element ) {
        if ( index < 0 || index > original.length ) {
            throw new IndexOutOfBoundsException( "Index: " + index + ", Size: " + original.length );
        }

        T[] newArray = (T[]) Array.newInstance( original.getClass().getComponentType(), original.length + 1 );
        System.arraycopy( original, 0, newArray, 0, index );
        newArray[ index ] = element;
        System.arraycopy( original, index, newArray, index + 1, original.length - index );

        return newArray;
    }



    public static Object mergeArr( Object... arrays ) {
        return UnitHelper.mergeArrays( arrays );
    }

    public static Object mergeArrays( Object[] arrays ) {
        if ( arrays == null || arrays.length == 0 ) {
            throw new IllegalArgumentException("Input arrays cannot be null or empty.");
        }

        Class<?> componentType = arrays[ 0 ].getClass().getComponentType();
        int totalLength = 0;

        for ( Object array : arrays ) {
            totalLength += Array.getLength(array);
        }

        Object result = Array.newInstance( componentType, totalLength );

        int currentIndex = 0;
        for ( Object array : arrays ) {
            int length = Array.getLength(array);
            System.arraycopy( array, 0, result, currentIndex, length );
            currentIndex += length;
        }

        return result;
    }
}
