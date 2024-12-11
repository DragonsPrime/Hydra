package com.sparta;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.bucket.ibatis.hydranium.BucketMappingDriver;
import com.pinecone.hydra.service.ibatis.hydranium.ServiceMappingDriver;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.UniformServicesInstrument;
import com.pinecone.hydra.service.kom.entity.GenericServiceElement;
import com.pinecone.hydra.service.kom.marshaling.ServicesJSONDecoder;
import com.pinecone.hydra.storage.bucket.TitanBucketInstrument;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.pinecone.ulf.util.id.GUIDs;
import com.sauron.radium.Radium;

class Ken extends Radium {
    public Ken( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public Ken( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );
    }

    @Override
    public void vitalize () throws Exception {
        KOIMappingDriver koiMappingDriver = new BucketMappingDriver(
                this, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );

        TitanBucketInstrument bucketInstrument = new TitanBucketInstrument( koiMappingDriver );
    }

}

public class TestBucket {
    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{
            Ken Ken = (Ken) Pinecone.sys().getTaskManager().add( new Ken( args, Pinecone.sys() ) );
            Ken.vitalize();
            return 0;
        }, (Object[]) args );
    }
}
