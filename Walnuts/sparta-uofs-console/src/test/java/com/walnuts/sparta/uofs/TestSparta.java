package com.walnuts.sparta.uofs;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.system.functions.Executor;
import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.bucket.ibatis.hydranium.BucketMappingDriver;
import com.pinecone.hydra.file.ibatis.hydranium.FileMappingDriver;
import com.pinecone.hydra.storage.bucket.TitanBucketInstrument;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.UniformObjectFileSystem;
import com.pinecone.hydra.storage.version.TitanVersionManage;
import com.pinecone.hydra.storage.version.VersionManage;
import com.pinecone.hydra.storage.volume.UniformVolumeManager;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.hydra.version.ibatis.hydranium.VersionMappingDriver;
import com.pinecone.hydra.volume.ibatis.hydranium.VolumeMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.sauron.radium.Radium;
import com.walnuts.sparta.uofs.console.Sparta;
import com.walnuts.sparta.uofs.console.SpartaBoot;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

class JesusChrist extends Radium {
    public JesusChrist( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public JesusChrist( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );
    }

    @Override
    public void vitalize () throws Exception {
        Sparta sparta = new Sparta( "Sparta", this );


        Thread shutdowner = new Thread(()->{
            Debug.sleep( 5000 );
            sparta.terminate();
        });
        //shutdowner.start();




        sparta.setPrimarySources( SpartaBoot.class );

        KOIMappingDriver koiMappingDriver = new VolumeMappingDriver(
                sparta, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );
        KOIMappingDriver koiFileMappingDriver = new FileMappingDriver(
                sparta, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );
        KOIMappingDriver koiBucketMappingDriver = new BucketMappingDriver(
                sparta, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );
        KOIMappingDriver koiVersionMappingDriver = new VersionMappingDriver(
                sparta, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );



        KOMFileSystem fileSystem = new UniformObjectFileSystem( koiFileMappingDriver );
        UniformVolumeManager volumeTree = new UniformVolumeManager( koiMappingDriver );
        TitanBucketInstrument bucketInstrument = new TitanBucketInstrument( koiBucketMappingDriver );
        TitanVersionManage versionManage = new TitanVersionManage( koiVersionMappingDriver );

        sparta.setInitializer(new Executor() {
            @Override
            public void execute() throws Exception {
                sparta.getSpringApplication().addInitializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
                    @Override
                    public void initialize( ConfigurableApplicationContext applicationContext ) {
                        GenericApplicationContext genericApplicationContext = (GenericApplicationContext) applicationContext;
                        genericApplicationContext.registerBean("primaryFileSystem", UniformObjectFileSystem.class, () -> (UniformObjectFileSystem)fileSystem);
                        genericApplicationContext.registerBean("primaryVolume", UniformVolumeManager.class, () -> (UniformVolumeManager) volumeTree);
                        genericApplicationContext.registerBean("primaryBucket", TitanBucketInstrument.class, () -> (TitanBucketInstrument) bucketInstrument);
                        genericApplicationContext.registerBean("primaryVersion", VersionManage.class, () -> (VersionManage) versionManage);
                    }
                });
            }
        });


        sparta.execute();





        this.getTaskManager().add( sparta );
        this.getTaskManager().syncWaitingTerminated();
    }
}


public class TestSparta {
    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{
            JesusChrist jesus = (JesusChrist) Pinecone.sys().getTaskManager().add( new JesusChrist( args, Pinecone.sys() ) );
            jesus.vitalize();
            return 0;
        }, (Object[]) args );
    }
}
