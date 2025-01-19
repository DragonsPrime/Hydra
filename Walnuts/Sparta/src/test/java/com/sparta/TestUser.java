package com.sparta;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.account.UniformAccountManager;
import com.pinecone.hydra.account.entity.GenericAccount;
import com.pinecone.hydra.account.entity.GenericDomain;
import com.pinecone.hydra.account.entity.GenericGroup;
import com.pinecone.hydra.account.ibatis.hydranium.UserMappingDriver;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
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
        //this.testInsert( uniformAccountManager );
        this.testQuery( uniformAccountManager );
    }

    public void testInsert( UniformAccountManager uniformAccountManager ){
        GenericDomain genericDomain = new GenericDomain();
        genericDomain.setName("用户域");
        GenericGroup genericGroup = new GenericGroup();
        genericGroup.setName("用户组");
        GenericAccount genericAccount = new GenericAccount();
        genericAccount.setName("用户");

        uniformAccountManager.put( genericAccount );
        uniformAccountManager.put( genericGroup );
        uniformAccountManager.put( genericDomain );
    }

    public void testQuery( UniformAccountManager uniformAccountManager ){
        Debug.trace(uniformAccountManager.queryGUIDByPath( "用户域/用户组/用户" ));
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
