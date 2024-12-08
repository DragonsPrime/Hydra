package com.sparta;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.hydra.account.UniformAccountManager;
import com.pinecone.hydra.account.entity.GenericAccount;
import com.pinecone.hydra.account.ibatis.hydranium.UserMappingDriver;
import com.pinecone.hydra.file.ibatis.hydranium.FileMappingDriver;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.UniformObjectFileSystem;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.storage.volume.entity.VolumeAllotment;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.volume.ibatis.hydranium.VolumeMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.pinecone.ulf.util.id.GuidAllocator;
import com.sauron.radium.Radium;

class Geralt extends Radium {
    public Geralt(String[] args, CascadeSystem parent) {
        this(args, null, parent);
    }

    public Geralt(String[] args, String szName, CascadeSystem parent) {
        super(args, szName, parent);
    }

    @Override
    public void vitalize() throws Exception {
        KOIMappingDriver koiMappingDriver = new UserMappingDriver(
                this, (IbatisClient) this.getMiddlewareManager().getRDBManager().getRDBClientByName("MySQLKingHydranium"), this.getDispenserCenter()
        );

        UniformAccountManager uniformAccountManager = new UniformAccountManager( koiMappingDriver );
        GuidAllocator guidAllocator = uniformAccountManager.getGuidAllocator();

        GenericAccount genericAccount = new GenericAccount();
        genericAccount.setUserName("曾文凯");
        genericAccount.setGuid( guidAllocator.nextGUID72() );
        uniformAccountManager.put( genericAccount );

    }
}
public class TestUser {
    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{
            Geralt Geralt = (Geralt) Pinecone.sys().getTaskManager().add( new Geralt( args, Pinecone.sys() ) );
            Geralt.vitalize();
            return 0;
        }, (Object[]) args );
    }
}
