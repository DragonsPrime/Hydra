package com.pinecone.hydra.storage.file.builder;

public enum Feature {
    EnableGlobalCache
    ;

    public final long mask = 1 << this.ordinal();

    private Feature() {
    }

    public final long getMask() {
        return this.mask;
    }

    public static boolean isEnabled( long features, Feature feature ) {
        return (features & feature.mask) != 0;
    }

    public static long config( long features, Feature feature, boolean state ) {
        if ( state ) {
            features |= feature.mask;
        }
        else {
            features &= ~feature.mask;
        }

        return features;
    }

    public static long of( Feature[] features ) {
        if ( features == null ) {
            return 0L;
        }
        else {
            long value = 0L;

            for ( int i = 0; i < features.length; ++i ) {
                Feature feature = features[ i ];
                value |= feature.mask;
            }

            return value;
        }
    }

    public static int featuresSize() {
        return Feature.values().length;
    }
}

