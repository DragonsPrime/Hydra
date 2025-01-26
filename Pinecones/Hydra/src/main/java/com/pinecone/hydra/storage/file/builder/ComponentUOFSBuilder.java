package com.pinecone.hydra.storage.file.builder;

import com.pinecone.framework.unit.BitSet64;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.UniformObjectFileSystem;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.umc.msg.UMCHeadV1;

public class ComponentUOFSBuilder implements UOFSBuilder {
    public static long DEFAULT_GENERATE_FEATURE = 0L;

    protected UOFSComponentor[] mComponentorIndex = new UOFSComponentor[ Feature.featuresSize() ];

    public KOIMappingDriver mKOIMappingDriver;

    public long             mFeatureValues = DEFAULT_GENERATE_FEATURE;

    public ComponentUOFSBuilder ( KOIMappingDriver driver ) {
        this.mKOIMappingDriver = driver;
    }




    @Override
    public UOFSBuilder registerComponentor( UOFSComponentor componentor ) {
        int i = componentor.getFeature().ordinal();
        this.mComponentorIndex[ i ] = componentor;
        this.mFeatureValues = BitSet64.setBit( this.mFeatureValues, i );
        return this;
    }

    @Override
    public KOMFileSystem buildByRegistered() {
        return this.build( this.mFeatureValues );
    }

    @Override
    public KOMFileSystem build( Feature... features ) {
        long featureValues = DEFAULT_GENERATE_FEATURE;

        for ( int i = 0; i < features.length; ++i ) {
            Feature feature = features[ i ];
            featureValues = Feature.config( featureValues, feature, true );
        }

        return this.build( featureValues );
    }

    @Override
    public KOMFileSystem build( long featureValues ) {
        KOMFileSystem fs = new UniformObjectFileSystem( this.mKOIMappingDriver );

        for ( int i = 0; i < Feature.featuresSize(); ++i ) {
            if ( ( featureValues & (1L << i) ) != 0 ) {
                this.mComponentorIndex[ i ].apply( fs );
            }
        }

        return fs;
    }

}
