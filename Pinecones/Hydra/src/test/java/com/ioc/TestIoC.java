package com.ioc;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.construction.*;
import com.pinecone.framework.system.prototype.ObjectiveMap;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.DirectJSONInjector;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.pinecone.framework.util.json.homotype.MapStructure;
import com.pinecone.framework.util.lang.DynamicFactory;
import com.pinecone.framework.util.lang.GenericDynamicFactory;

import java.util.List;
import java.util.Map;

enum Species {
    Dragon,
    Lizard,
    Bear,
    Wolf,
    Devil,
    Human,
    Druid,
    Elf,
    Dwarf,
}

class Weapon {
    @JSONGet
    protected String    name;

    @MapStructure
    protected int       damage;

    @JSONGet( "attachedMagic" )
    protected String    magic;

    public String toJSONString() {
        return DirectJSONInjector.instance().inject( this ).toString();
    }

    public String toString(){
        return DirectJSONInjector.instance().inject( this ).toString();
    }
}

class Actor {
    @JSONGet
    protected String    name;

    @JSONGet
    protected int       hp;

    @JSONGet
    protected Species   species = Species.Dragon;

    @Structure( cycle = ReuseCycle.Disposable )
    protected Weapon    weapon;

    protected boolean   live = true;

    public String toJSONString() {
        return DirectJSONInjector.instance().inject( this ).toString();
    }

    public String toString(){
        return DirectJSONInjector.instance().inject( this ).toString();
    }

}


class Team {
    @Structure( type = Actor.class, cycle = ReuseCycle.Disposable )
    protected List<Actor > craws;

    public String toJSONString() {
        return DirectJSONInjector.instance().inject( this ).toString();
    }

    public String toString(){
        return DirectJSONInjector.instance().inject( this ).toString();
    }
}

class Tale {
    @Structure
    protected String              type;

    @Structure( type = Team.class, cycle = ReuseCycle.Recyclable )
    protected Map<String, Team >  worlds;

    public String toJSONString() {
        return ( (JSONObject)DirectJSONInjector.instance().inject( this )).toJSONStringI(2);
    }

    public String toString(){
        return DirectJSONInjector.instance().inject( this ).toString();
    }
}


public class TestIoC {
    public static void testInstancePool( )  {
        DynamicFactory wolfNPCFactory           = new GenericDynamicFactory();
        GenericDynamicInstancePool<Actor > npcs = new GenericDynamicInstancePool<>( wolfNPCFactory, 0, 0, Actor.class );

        for ( int i = 0; i < 1e2; ++i ) {
            Actor npc = npcs.allocate();
            Debug.trace( npc );
            npcs.free(npc);
        }
    }

    public static void testUnifyStructureInjector_Simple( )  throws Exception {
        StructureInstanceDispenser dispenser = new UnifyCentralInstanceDispenser();
        UnifyStructureInjector injector = new UnifyStructureInjector( Actor.class, dispenser );
        Actor actor = new Actor();

        JSONObject jo = new JSONMaptron( "{ name:RedPrince, hp:100, species: Lizard, weapon:{ name:TyrantSuit, damage:70, attachedMagic:fire } }" );
        injector.inject( new ObjectiveMap<>(jo), actor );
        Debug.trace( actor );
    }

    public static void testUnifyStructureInjector_List( )  throws Exception {
        StructureInstanceDispenser dispenser = new UnifyCentralInstanceDispenser();
        UnifyStructureInjector injector = new UnifyStructureInjector( Team.class, dispenser );
        Team team = new Team();

        JSONObject jo = new JSONMaptron( "{ craws: [" +
                "{ name:RedPrince, hp:100, species: Lizard, weapon:{ name:TyrantSuit, damage:70, attachedMagic:fire } }, " +
                "{ name:Ifan, hp:90, species: Human, weapon:{ name:Ranger, damage:50, attachedMagic:lightning } }, " +
                "] }"
        );
        injector.inject( new ObjectiveMap<>(jo), team );
        Debug.trace( team );
    }

    public static void testUnifyStructureInjector_Sophisticate( )  throws Exception {
        StructureInstanceDispenser dispenser = new UnifyCentralInstanceDispenser();
        UnifyStructureInjector injector = new UnifyStructureInjector( Tale.class, dispenser );
        Tale tale = new Tale();

        JSONObject jo = new JSONMaptron( "{ type:fantasy, worlds :{ d2: { craws: [" +
                "{ name:RedPrince, hp:100, species: Lizard, weapon:{ name:TyrantSuit, damage:70, attachedMagic:fire } }, " +
                "{ name:Ifan, hp:90, species: Human, weapon:{ name:Ranger, damage:50, attachedMagic:lightning } }, " +
                "] }," +
                "b3: { craws: [" +
                "{ name:DarkUrge, hp:120, species: Dragon, weapon:{ name:DragonSoul, damage:9999, attachedMagic:fire } }, " +
                "{ name:Karlack, hp:100, species: Devil, weapon:{ name:Everburn Blade, damage:120, attachedMagic:fire } }, " +
                "] }," +
                "no: { craws: [" +
                "{ name:Hydra, hp:9999, species: Hydra, weapon:{ name:SuperDragon, damage:9999, attachedMagic:fire } }, " +
                "] }" +
                " }}"
        );
        injector.inject( new ObjectiveMap<>(jo), tale );
        Debug.trace( tale );
    }


    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{


            //TestIoC.testInstancePool();
            //TestIoC.testUnifyStructureInjector_Simple();
            //TestIoC.testUnifyStructureInjector_List();
            TestIoC.testUnifyStructureInjector_Sophisticate();


            return 0;
        }, (Object[]) args );
    }
}
