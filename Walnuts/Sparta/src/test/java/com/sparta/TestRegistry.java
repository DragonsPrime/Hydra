package com.sparta;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.registry.KOMRegistry;
import com.pinecone.hydra.registry.GenericKOMRegistry;
import com.pinecone.hydra.registry.entity.ElementNode;
import com.pinecone.hydra.registry.entity.Properties;
import com.pinecone.hydra.registry.ibatis.hydranium.RegistryMappingDriver;
import com.pinecone.hydra.registry.marshaling.RegistryDOMEncoder;
import com.pinecone.hydra.registry.marshaling.RegistryEncoder;
import com.pinecone.hydra.registry.marshaling.RegistryJSONDecoder;
import com.pinecone.hydra.registry.marshaling.RegistryJSONEncoder;
import com.pinecone.hydra.system.ko.driver.KOIMappingDriver;
import com.pinecone.slime.jelly.source.ibatis.IbatisClient;
import com.sauron.radium.Radium;

class StanMarsh extends Radium {
    public StanMarsh( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public StanMarsh( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );
    }

    @Override
    public void vitalize () throws Exception {
        KOIMappingDriver koiMappingDriver = new RegistryMappingDriver(
                this, (IbatisClient)this.getMiddlewareManager().getRDBManager().getRDBClientByName( "MySQLKingHydranium" ), this.getDispenserCenter()
        );

        KOMRegistry registry = new GenericKOMRegistry( koiMappingDriver );

        //this.testBasicInsert( registry );
        //this.testDeletion( registry );
        //this.testDataExtends( registry );
        //this.testHardLink( registry );
        //this.testCopy( registry );
        //this.testMove( registry );
        //this.testMisc( registry );
        //this.testSelector( registry );
        //this.testAttributes( registry );
        this.testMarshaling( registry );
    }

    private void testBasicInsert( KOMRegistry registry ) {
        registry.putProperties( "game/minecraft/wizard1", new JSONMaptron( "{ name:ken, age:22, species:human, job:wizard }" ) );
        registry.putProperties( "game/minecraft/sorcerer1", new JSONMaptron( "{ name:dragonking, hp:666, species:dragon, job:sorcerer }" ) );
        registry.putProperties( "game/terraria/mob1", new JSONMaptron( "{ name:lural, age:666, species:cthulhu, job:mob }" ) );
        registry.putProperties( "game/witcher/mob1", new JSONMaptron( "{ name:witcher_mob1, age:-789, species:'undefined', job:mob }" ) );
        registry.putProperties( "game/witcher/mob2", new JSONMaptron( "{ name:wxsdw, age:666, species:cthulhu, job:mob }" ) );
        registry.putProperties( "game/witcher/mob3", new JSONMaptron( "{ name:mob3, age:661, species:cthulhu2, job:mob2 }" ) );
        registry.putProperties( "game/witcher/people/xxx", new JSONMaptron( "{ name:xxxx, age:999, species:elf, job:warrior }" ) );
        registry.putProperties( "game/witcher/people/xx2", new JSONMaptron( "{ name:xxx2, age:992, species:elf, job:warrior }" ) );

        registry.putProperties( "game3a/witcher/people/s4/urge", new JSONMaptron( "{ name:darkurge, age:996, species:dragon, job:warrior }" ) );

        registry.putTextValue( "game/witcher/jesus", "JSONObject", "{k:p}" );
    }

    private void testDeletion( KOMRegistry registry ) {
        registry.remove( "game" );
        registry.remove("game3a");
//        registry.remove( "game/witcher" );
//        registry.remove( "game/minecraft" );
//        registry.remove("game/terraria");
//        registry.remove("game/witcher");
        //Debug.fmp( 2, registry.getProperties( registry.queryGUIDByFN( "game.witcher.mob3" ) ).getValue( "name" ) );
        //Debug.fmp( 2, registry.get( registry.queryGUIDByFN( "game3a" ) ) );

        //registry.remove( "game" );

//        registry.affirmProperties( "泰拉瑞亚.灾厄.至尊灾厄" );
//        registry.remove( "泰拉瑞亚.灾厄.至尊灾厄" );
//        Debug.fmp( 4, registry.getProperties( "泰拉瑞亚.灾厄.至尊灾厄" ).toJSONObject() );
    }

    private void testMove( KOMRegistry registry ) {
        //registry.move( "game/terraria/mob1", "game/minecraft/mob1" );
        //registry.move( "game/minecraft/", "game/terraria/more" );

        //registry.move( "game/minecraft/sorcerer1 ", "game/terraria/." );
        //Debug.trace( registry.queryElement( "game/terraria/sorcerer1" ), registry.queryElement( "game/minecraft/sorcerer1" )  );
    }

    private void testCopy( KOMRegistry registry ) {
        //this.testBasicInsert( registry );
        //registry.queryElement("game/minecraft/sorcerer1").evinceProperties().copyTo(registry.queryGUIDByPath("game/minecraft/wizard1"));

        //registry.getProperties( "game/terraria/mob1" ).copyTo( "game/moregame/mmob4" );

        //Debug.trace( registry.getProperties( "game/moregame/mmob4" ) );

        //registry.getNamespace( "game3a/witcher/" ).copyTo( registry.affirmNamespace( "game/owo" ).getGuid() );
        //registry.getNamespace( "game3a/witcher" ).copyTo( registry.affirmNamespace( "game/owo" ).getGuid() );


        //Debug.trace( registry.getNamespace( "game/owo" ).getChildren() );


        //registry.copy( "game/minecraft/sorcerer1 ", "game/terraria/." );
        //Debug.trace( registry.queryElement( "game/terraria/sorcerer1" ), registry.queryElement( "game/minecraft/sorcerer1" )  );

        //registry.copy( "game/minecraft", "game/terraria" );
        //Debug.trace( registry.queryElement( "game/terraria" ).evinceNamespace().listItem(), registry.queryElement( "game/minecraft" ).evinceNamespace().listItem()  );

        //registry.copy( "game/minecraft", "game/terraria/" );
        //Debug.trace( registry.queryElement( "game/terraria" ).evinceNamespace().listItem(), registry.queryElement( "game/minecraft" ).evinceNamespace().listItem()  );

        //registry.copy( "game/minecraft", "game/terraria/new" );
        //Debug.trace( registry.queryElement( "game/terraria/new" ).evinceNamespace().listItem(), registry.queryElement( "game/minecraft" ).evinceNamespace().listItem()  );
    }

    private void testDataExtends( KOMRegistry registry ) {
//          Debug.trace(registry.fetchRoot());
//        registry.setAffinity(new GUID72("1f7c33d6-000309-0000-f8"),new GUID72("1f7c33d6-0003c1-0000-b0"));

//        registry.setInheritance();
        //Debug.trace(registry.queryGUIDByPath("game/terraria/mob1"));

        //registry.newLinkTag("game/terraria/mob1","game/minecraft","mob1");
        GUID guid = registry.queryGUIDByPath("game/minecraft/mob1");
        Debug.trace(guid);



        //registry.putProperties( "game/fiction/character/dragonKing", new JSONMaptron( "{ name:DragonKing, age:666, species:dragon, job:sorcerer, hp:999999 }" ) );
        //registry.putProperties( "game/3a/character/red-prince", new JSONMaptron( "{ name: RedPrince, species:lizard, job:warrior, force:777777 }" ) );
        //registry.setDataAffinity( "game/3a/character/red-prince", "game/fiction/character/dragonKing" );


        GUID kingId   = registry.queryGUIDByPath( "game/fiction/character/dragonKing" );
        GUID princeId = registry.queryGUIDByPath( "game/3a/character/red-prince" );

        Debug.fmp( 2, registry.getProperties( "game/fiction/character/dragonKing" ) );
        Debug.fmp( 2, registry.getProperties( princeId ) );

        //Debug.fmp( 2, registry.getProperties( "game/fiction/character/dragonKing" ).toJSONObject() );
        //Debug.fmp( 2, registry.getProperties( princeId ).toJSONObject() );

        Properties princePro = registry.getProperties( princeId );
        Debug.trace( princePro.getValue( "hp" ) );
        Debug.trace( princePro.getValue( "name" ) );
        Debug.trace( princePro.containsKey( "hp" ) );
        Debug.trace( princePro.hasOwnProperty( "hp" ) );


//        Property property = princePro.get( "name" );
//        property.setValue( "RedPrince" );
//        princePro.update( property );
//        princePro.set( "name", "RedPrince" );
//        Debug.trace( princePro.getValue( "name" ) );


        //princePro.put( "hpc", 999999 );
        //Debug.trace( princePro.getValue( "hp" ) );

        //princePro.remove( "hpc" );
        //princePro.remove( "hp" );

//        registry.newHardLink( "game3a/mix/wizard1", "game/witcher" );




        //Debug.fmp( 2, registry.getProperties( "game/terraria/more/sorcerer1" ).toJSONObject() );

        //registry.move();

//        Debug.fmp( 2, registry.getProperties( "game/terraria/mob1" ).toJSONObject() );
        //Debug.fmp( 2, registry.getProperties( "game/minecraft/mob1" ).toJSONObject() );





//
//        registry.putProperties( "movie/terraria/mob1", new JSONMaptron( "{ name:lural, age:666, species:cthulhu, job:mob }" ) );
//        Debug.trace(registry.fetchRoot());


    }

    private void testMisc( KOMRegistry registry ) {
        //registry.putProperties( "game/fiction/character/dragonKing", new JSONMaptron( "{ name:DragonKing, age:666, species:dragon, job:sorcerer, hp:999999 }" ) );
        //registry.putProperties( "game/3a/character/red-prince", new JSONMaptron( "{ name: RedPrince, species:lizard, job:warrior, force:777777 }" ) );

        //registry.rename( "game/3a/character/red-prince", "red-prince2" );

        Debug.trace( registry.getProperties( "game/3a/character/red-prince" ) );
    }

    private void testHardLink( KOMRegistry registry ) {
        //this.testBasicInsert( registry );

        //Debug.trace( registry.queryElement( "game/minecraft" ) );
        //registry.newLinkTag( "game/witcher", "game/minecraft", "mount" );

        //Debug.trace( registry.getMasterTrieTree().queryAllLinkedCount( registry.queryGUIDByPath( "game/witcher" ) ) );
        //Debug.trace( registry.getMasterTrieTree().queryStrongLinkedCount( registry.queryGUIDByPath( "game/witcher" ) ) );

        //Debug.fmp( 2, registry.queryElement( "game/minecraft/mount/" ) );
        //Debug.fmp( 2, registry.queryElement( "game/minecraft/mount/mob2" ) );
        //Debug.fmp( 2, registry.queryElement( "mount" ) );
        //Debug.fmp( 2, registry.queryElement( "game/witcher/jesus/" ) );

        //registry.remove( "game/minecraft/mount" );
        Debug.fmp( 2, registry.queryElement( "game3a" ).evinceNamespace().getEnumId() );

        //var children = registry.queryElement( "game" ).evinceNamespace().getChildren();
        //var mc = children.get("minecraft").evinceNamespace().getChildren();

//        var children = registry.queryElement( "game3a" ).evinceNamespace().getChildren();
//        var mc = children.get("witcher").evinceNamespace().getChildren();
//        Debug.trace( 2, mc );

        //Debug.trace( registry.get )
        //Debug.fmp( 2, registry.queryElement( "game/minecraft/" ).evinceNamespace().listItem() );


    }

    private void testSelector( KOMRegistry registry ) {
        this.testBasicInsert( registry );
        //Debug.trace( registry.querySelectorJ( "game.minecraft.wizard1.name" ) );

        Debug.fmp( 2, registry.querySelectorJ( "game/witcher/jesus" ) );
    }

    private void testAttributes( KOMRegistry registry ) throws Exception {
        ElementNode node = registry.queryElement( "game/minecraft/sorcerer1" );
        //node.getAttributes().setAttribute( "title", "king" );
        //node.getAttributes().clear();

        Debug.fmp( 2, node.getAttributes().size() );


    }

    private void testMarshaling( KOMRegistry registry ) {
//        RegistryJSONEncoder encoder = new RegistryJSONEncoder( registry );

//        ElementNode node = registry.queryElement( "game/witcher/jesus" );
//        Debug.trace( encoder.encode( node ) );


        RegistryJSONDecoder decoder = new RegistryJSONDecoder( registry );

        Debug.trace( decoder.decode( new JSONMaptron( "{ game: { character: { " +
                "Ifan: { name: Ifan, hp:90, species: Human }, RedPrince: { name:RedPrince, hp:100, species: Lizard } " +
                "}, attr: 1234, file: text_files } }" ), null ).evinceNamespace().toJSONObject() );

        Debug.trace( registry.querySelectorJ( "game" ) );
//        registry.queryElement( "game/character/Ifan" ).getAttributes().setAttribute( "state", "live" );


//        RegistryEncoder encoder = new RegistryDOMEncoder( registry );
//        ElementNode node = registry.queryElement( "game" );
//        Debug.echo( encoder.encode( node ).toString() );

    }

}


public class TestRegistry {
    public static void main( String[] args ) throws Exception {
        Pinecone.init( (Object...cfg )->{
            StanMarsh ladyGaga = (StanMarsh) Pinecone.sys().getTaskManager().add( new StanMarsh( args, Pinecone.sys() ) );
            ladyGaga.vitalize();
            return 0;
        }, (Object[]) args );
    }
}
