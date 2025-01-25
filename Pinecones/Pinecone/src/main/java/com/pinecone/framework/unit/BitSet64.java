package com.pinecone.framework.unit;

import com.pinecone.framework.util.Bits;
//import com.pinecone.framework.util.Debug;

public final class BitSet64 {

    public static final int Int64MaxPos = Long.SIZE - 1;

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

    public static String toBinaryStringMSB( long that ) {
        String binaryString = String.format(
                "%64s", Long.toBinaryString( Bits.reverse64Bits(that) )
        ).replace( ' ', '0' );
        return "0b" + binaryString;
    }

    public static String toBinaryStringLSB( long that ) {
        String binaryString = String.format(
                "%64s", Long.toBinaryString(that)
        ).replace( ' ', '0' );
        return "0b" + binaryString;
    }

    public static String toIndexJSONString( long that ) {
        StringBuilder sb = new StringBuilder();
        sb.append( '[' );

        for ( int i = 0; i < Long.SIZE; ++i ) {
            if ( ( that & (1L << i) ) != 0 ) {
                sb.append( i ).append( ',' );
            }
        }

        if( sb.charAt( sb.length() - 1 ) == ',' ) {
            sb.deleteCharAt( sb.length() - 1 );
        }

        sb.append( ']' );

        return sb.toString();
    }


    public static long set( long that, int from, int to, boolean val ) throws IllegalArgumentException {
        int jt = BitSet64.check( from, to );
        long mask = ((1L << (jt - from + 1)) - 1) << from;

        if ( val ) {
            that |= mask;

            if ( to >= BitSet64.Int64MaxPos ) {
                that = BitSet64.setBit( that, to );
            }
        }
        else {
            that &= ~mask;

            if ( to >= BitSet64.Int64MaxPos ) {
                that = BitSet64.clearBit( that, to );
            }
        }

        return that;
    }

    public static long set( long that, int from, int to ) throws IllegalArgumentException {
        return BitSet64.set( that, from, to, true );
    }

    public static long unset( long that, int from, int to ) throws IllegalArgumentException {
        return BitSet64.set( that, from, to, false );
    }

    private static int check( int from, int to ) throws IllegalArgumentException {
        if ( from > to || from < 0 || to > BitSet64.Int64MaxPos ) {
            throw new IllegalArgumentException( "Invalid bit positions" );
        }
        int jt = to;
        if ( to == BitSet64.Int64MaxPos ) {
            jt = BitSet64.Int64MaxPos - 1;
        }

        return jt;
    }

    public static long extract( long that, int from, int to ) throws IllegalArgumentException {
        int jt = BitSet64.check( from, to );
        long mask = ((1L << (jt - from + 1)) - 1) << from;
        long t = (that & mask) >>> from;

        if ( to >= BitSet64.Int64MaxPos ) {
            t =  t | (that & 0x8000000000000000L);
        }

        return t;
    }

    public static long copy( long that, int from, int to, long segment ) {
        int jt = to;
        if ( to == BitSet64.Int64MaxPos ) {
            jt = BitSet64.Int64MaxPos - 1;
        }

        long seg = segment;
        int segmentLength = jt - from + 1;
        segment &= (1L << segmentLength) - 1;
        segment <<= from;

        long mask = ((1L << segmentLength) - 1) << from;
        that &= ~mask;
        if ( to >= BitSet64.Int64MaxPos ) {
            that &= ~(1L << to);
        }

        that |= segment;

        if ( to >= BitSet64.Int64MaxPos ) {
            long lastBit = seg & 0x8000000000000000L;
            that |= lastBit;
        }

        return that;
    }

    public static long reverse( long that, int from, int to ) {
        long seg  = BitSet64.extract( that, from, to );
        long re   = Bits.reverse64Bits( seg );

        int k = to - from;
        long sift = (re >>> (BitSet64.Int64MaxPos - k)) | (re << k);
        if ( to == BitSet64.Int64MaxPos ) {
            sift = re;
        }
        else {
            sift &= ~(1L << BitSet64.Int64MaxPos);
        }

//        Debug.bluef( BitSet64.toBinaryStringLSB( seg ).substring(2 + BitSet64.Int64MaxPos - k, 66) );
//        Debug.bluef( BitSet64.toBinaryStringLSB( re ).substring(2, k + 3) );
//        Debug.bluef( BitSet64.toBinaryStringLSB( sift ).substring(2 + BitSet64.Int64MaxPos - k, 66) );

        return BitSet64.copy( that, from, to, sift );
    }

    public static long flip( long that, int from, int to ) throws IllegalArgumentException {
        int jt = BitSet64.check( from, to );
        long mask = ((1L << (jt - from + 1)) - 1) << from;

        if ( to >= BitSet64.Int64MaxPos ) {
            that = BitSet64.flipBit( that, to );
        }
        return that ^ mask;
    }


    public static int existence ( long that ) {
        return Long.bitCount(that);
    }
}
