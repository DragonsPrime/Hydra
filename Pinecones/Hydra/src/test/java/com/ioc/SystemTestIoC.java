package com.ioc;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.construction.*;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.auto.ArchInstructation;
import com.pinecone.hydra.auto.Instructation;
import com.pinecone.hydra.auto.PeriodicAutomaton;
import com.pinecone.hydra.auto.PeriodicAutomatron;

class SpawnInstruct extends ArchInstructation {
    @Structure( cycle = ReuseCycle.Disposable )
    Actor npc;

    public SpawnInstruct() {
        super();
    }

    @Override
    public void execute() throws Exception {
        Debug.trace( this.npc.name +" spawned !" );
    }
}

public class SystemTestIoC {
    public static void testUnifyStructureInjector_MobSpawnner( )  throws Exception {
        PeriodicAutomatron modSpawnner = new PeriodicAutomaton( null, 500 );
        modSpawnner.start();

        StructureInstanceDispenser dispenser = new UnifyCentralInstanceDispenser();
        UnifyStructureInjector injector = new UnifyStructureInjector( SpawnInstruct.class, dispenser );

        Thread elderBrain = new Thread(()->{
            for ( int i = 0; i < 100; i++ ) {
                Debug.sleep( 50 );
                modSpawnner.command( (Instructation) injector.inject( new JSONMaptron(
                        "{npc:{ name:NPC"+i+", hp:9999, species: Hydra, weapon:{ name:SuperDragon, damage:9999, attachedMagic:fire } }}"
                ), new SpawnInstruct() ) );
            }
        });

        elderBrain.start();
        elderBrain.join();
        modSpawnner.join();
    }


    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{


            //TestIoC.testInstancePool();
            //TestIoC.testUnifyStructureInjector_Simple();
            //TestIoC.testUnifyStructureInjector_List();
            SystemTestIoC.testUnifyStructureInjector_MobSpawnner();


            return 0;
        }, (Object[]) args );
    }
}
