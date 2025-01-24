package com.pinecone.framework.unit;

public final class Bitset64 {

    public static long setBit( long that, int position ) {
        return that | (1L << position);
    }

    public static long clearBit( long that, int position ) {
        return that & ~(1L << position);
    }

    public static boolean isBitSet( long that, int position ) {
        return (that & (1L << position)) != 0;
    }

    public static long flipBit( long that, int position ) {
        return that ^ (1L << position);
    }

    public static String toBinaryString( long that ) {
        return Long.toBinaryString(that);
    }

    public static String toBinaryStringLE( long that ) {
        String binaryString = String.format(
                "%64s", Long.toBinaryString( that )
        ).replace( ' ', '0' );
        return "0b" + binaryString;
    }

    public static String toBinaryStringBE( long that ) {
        String binaryString = String.format(
                "%64s", Long.toBinaryString(that)
        ).replace( ' ', '0' );
        return "0b" + binaryString;
    }


    public static long set( long that, int from, int to, boolean val ) throws IllegalArgumentException {
        if ( from > to || from < 0 || to > 63 ) {
            throw new IllegalArgumentException( "Invalid bit positions" );
        }

        long mask = ((1L << (to - from + 1)) - 1) << from;

        if ( val ) {
            that |= mask;
        }
        else {
            that &= ~mask;
        }

        return that;
    }
}
