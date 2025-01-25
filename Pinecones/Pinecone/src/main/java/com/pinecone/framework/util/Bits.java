package com.pinecone.framework.util;

public final class Bits {
    /**
     * From Pinecone CPP,
     * Pinecone/Framework/Util/Bits/BitsProcessor.h
     * Pinecone/Framework/Util/Bits/BitsProcessor.cpp
     */
    // Reverse All Bits (Smallest Unit: Bit)
    private static final byte[] BitReverseTable256 = new byte[ 256 ];

    static {
        for ( int i = 0; i < 256; ++i ) {
            Bits.BitReverseTable256[i] = (byte) (((i & 0x01) << 7) | ((i & 0x02) << 5) | ((i & 0x04) << 3) | ((i & 0x08) << 1)
                    | ((i & 0x10) >> 1) | ((i & 0x20) >> 3) | ((i & 0x40) >> 5) | ((i & 0x80) >> 7));
        }
    }

    public static byte reverse8Bits( byte nNum ) {
        return Bits.BitReverseTable256[ nNum & 0xFF ];
    }

    public static short reverse16Bits( short nNum ) {
        int nRes = 0;
        byte p0 = (byte) (nNum & 0xFF);
        byte p1 = (byte) ((nNum >> 8) & 0xFF);

        byte q1 = Bits.BitReverseTable256[ p0 & 0xFF ];
        byte q0 = Bits.BitReverseTable256[ p1 & 0xFF ];
        nRes = (q0 & 0xFF) | ((q1 & 0xFF) << 8);
        return (short) nRes;
    }

    public static int reverse32Bits( int nNum ) {
        int nRes = 0;
        byte p0 = (byte) (nNum & 0xFF);
        byte p1 = (byte) ((nNum >> 8) & 0xFF);
        byte p2 = (byte) ((nNum >> 16) & 0xFF);
        byte p3 = (byte) ((nNum >> 24) & 0xFF);

        byte q3 = Bits.BitReverseTable256[ p0 & 0xFF ];
        byte q2 = Bits.BitReverseTable256[ p1 & 0xFF ];
        byte q1 = Bits.BitReverseTable256[ p2 & 0xFF ];
        byte q0 = Bits.BitReverseTable256[ p3 & 0xFF ];
        nRes = (q0 & 0xFF) | ((q1 & 0xFF) << 8) | ((q2 & 0xFF) << 16) | ((q3 & 0xFF) << 24);
        return nRes;
    }

    public static long reverse64Bits( long nNum ) {
        long nRes = 0;
        int lower = (int) (nNum & 0xFFFFFFFFL);
        int upper = (int) ((nNum >> 32) & 0xFFFFFFFFL);
        int reversedLower = Bits.reverse32Bits(upper);
        int reversedUpper = Bits.reverse32Bits(lower);
        nRes = ((long) reversedLower & 0xFFFFFFFFL) | (((long) reversedUpper & 0xFFFFFFFFL) << 32);
        return nRes;
    }
}
