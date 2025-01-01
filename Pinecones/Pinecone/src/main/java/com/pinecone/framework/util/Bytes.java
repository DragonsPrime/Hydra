package com.pinecone.framework.util;

public final class Bytes {
    public static final byte[] Empty = new byte[0];

    // LE
    public static byte[] int16ToBytesLE( short value ) {
        return new byte[]{
                (byte) value,
                (byte) (value >> 8)
        };
    }

    public static byte[] int32ToBytesLE( int value ) {
        return new byte[]{
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24)
        };
    }

    public static byte[] int64ToBytesLE( long value ) {
        return new byte[]{
                (byte) value,
                (byte) (value >> 8),
                (byte) (value >> 16),
                (byte) (value >> 24),
                (byte) (value >> 32),
                (byte) (value >> 40),
                (byte) (value >> 48),
                (byte) (value >> 56)
        };
    }

    public static byte[] float32ToBytesLE( float value ) {
        return Bytes.int32ToBytesLE( Float.floatToIntBits(value) );
    }

    public static byte[] float64ToBytesLE( double value ) {
        return Bytes.int64ToBytesLE( Double.doubleToLongBits(value) );
    }


    // BE
    public static byte[] int16ToBytesBE( short value ) {
        return new byte[]{
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static byte[] int32ToBytesBE( int value ) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static byte[] int64ToBytesBE( long value ) {
        return new byte[]{
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static byte[] float32ToBytesBE( float value ) {
        return Bytes.int32ToBytesBE( Float.floatToIntBits(value) );
    }

    public static byte[] float64ToBytesBE( double value ) {
        return Bytes.int64ToBytesBE( Double.doubleToLongBits(value) );
    }



    // LE / Decode
    public static short bytesToInt16LE( byte[] bytes ) {
        return (short) ((bytes[1] << 8) | (bytes[0] & 0xFF));
    }

    public static int bytesToInt32LE( byte[] bytes ) {
        return (bytes[3] << 24) | ((bytes[2] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF);
    }

    public static long bytesToInt64LE( byte[] bytes ) {
        return ((long) bytes[7] << 56) | ((long) (bytes[6] & 0xFF) << 48) | ((long) (bytes[5] & 0xFF) << 40) |
                ((long) (bytes[4] & 0xFF) << 32) | ((long) (bytes[3] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16) |
                ((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF);
    }

    public static float bytesToFloat32LE( byte[] bytes ) {
        return Float.intBitsToFloat( Bytes.bytesToInt32LE( bytes ) );
    }

    public static double bytesToFloat64LE( byte[] bytes ) {
        return Double.longBitsToDouble( Bytes.bytesToInt64LE( bytes ) );
    }


    // BE / Decode
    public static short bytesToInt16BE( byte[] bytes ) {
        return (short) ((bytes[0] << 8) | (bytes[1] & 0xFF));
    }

    public static int bytesToInt32BE( byte[] bytes ) {
        return (bytes[0] << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }

    public static long bytesToInt64BE( byte[] bytes ) {
        return ((long) bytes[0] << 56) | ((long) (bytes[1] & 0xFF) << 48) | ((long) (bytes[2] & 0xFF) << 40) |
                ((long) (bytes[3] & 0xFF) << 32) | ((long) (bytes[4] & 0xFF) << 24) | ((bytes[5] & 0xFF) << 16) |
                ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
    }

    public static float bytesToFloat32BE( byte[] bytes ) {
        return Float.intBitsToFloat( Bytes.bytesToInt32BE( bytes ) );
    }

    public static double bytesToFloat64BE( byte[] bytes ) {
        return Double.longBitsToDouble( Bytes.bytesToInt64BE( bytes ) );
    }




    public static int calculateParity( byte b ) {
        int count = 0;
        for ( int i = 0; i < 8; i++ ) {
            if ((b & (1 << i)) != 0) {
                count++;
            }
        }
        if( (count % 2) == 0 ){
            return 1;
        }
        return 0;
    }
}
