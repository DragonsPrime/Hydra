package com.util.json;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.prototype.ObjectiveBean;
import com.pinecone.framework.system.prototype.ObjectiveClass;
import com.pinecone.framework.system.prototype.ObjectiveEvaluator;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.*;
import com.pinecone.framework.util.json.hometype.*;

class Dick {
    @JSONGet( "name" )
    public String    mName  ;
    @MapStructure
    public long      length;
    public int       emnus;

    public Dick() {

    }

    public Dick( long      length ) {
        this.mName = "dick";
        this.length = length;
    }

    public String toJSONString() {
        return AnnotatedJSONInjector.instance().inject( this ).toString();
    }

    public String toString(){
        return AnnotatedJSONInjector.instance().inject( this ).toString();
    }
}

class Shit{
    @JSONGet
    public  String    mName ;
    public int        length;
    @JSONGet
    Dick[] array;
    //public JSONArray  array;
    @JSONGet
    public Dick       dick = new Dick();

    public Shit(  ){

    }

//    public Object getName() {
//        return this.mName;
//    }

    public Object test( int i, Integer c, String sz ) {
        return sz + i + c;
    }

    public Object trial( Object... arg ){
        return arg;
    }


/*    public String toJSONString() {
        return DirectJSONInjector.instance().inject( this ).toString();
    }*/

/*    public String toString(){
        return DirectJSONInjector.instance().inject( this ).toString();
    }*/

}

class Vagina {
    public String    name  ;
    public long      length;
    public int       emnus;

    public Vagina() {

    }

    public String getName() {
        return this.name;
    }

    public long getLength() {
        return this.length;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setLength( long length ) {
        this.length = length;
    }

    public String toJSONString() {
        return DirectJSONInjector.instance().inject( this ).toString();
    }

    public String toString(){
        return DirectJSONInjector.instance().inject( this ).toString();
    }
}

public class TestJSON {
    public static void testDirectlyInjector() {
        Shit shit = new Shit();
        JSONObject jsonShit = new JSONMaptron("{ name:'shit',  'fuck':7, 'length': 1, 'array':[{name:'shit',length:1998}] }");
        Debug.trace( jsonShit );
        shit = (Shit) ( new DirectObjectInjector(  true, Shit.class ) ).inject( jsonShit );
        Debug.trace(shit);

        Debug.echo( JSON.marshal( shit ) );

    }

    public static void testAnnotatedInjector() {
//        Dick dick = new Dick();
//        JSONObject jsonShit = new JSONMaptron("{ name:'shit', 'length': 1, 'array':[{name:'shit',length:1998}] }");
//        Debug.trace( jsonShit );
//        dick = (Dick) ( new AnnotatedObjectInjector( Dick.class ) ).inject( jsonShit );
//        Debug.trace( dick );
    }

    public static void testObjectom() {
        Dick dick = new Dick();
        JSONObject jsonShit = new JSONMaptron("{ name:'shit', 'length': 1, 'array':[{name:'shit',length:1998}] }");
        Debug.trace( jsonShit );
        dick = (Dick) ( new AnnotatedObjectInjector( Dick.class ) ).inject( jsonShit );

        ObjectiveClass objectom = new ObjectiveClass( dick );
        Debug.echo( objectom.toJSONString(), objectom.get( "length" ), objectom.get( "mName" ) );
    }

    public static void testObjectiveBean() {
        Vagina vagina = new Vagina();
        JSONObject jsonShit = new JSONMaptron("{ name:'shit', 'length': 1 }");
        Debug.trace( jsonShit );
        vagina = (Vagina) ( new DirectObjectInjector( Vagina.class ) ).inject( jsonShit );

//        JSONObject o = new JSONMaptron( vagina );
//        Debug.echo( o.toJSONString() );


        ObjectiveBean bean = new ObjectiveBean( vagina );
        Debug.echo( bean.toJSONString() );
        bean.set( "name", "fuck" );
        Debug.echo( bean.toJSONString() );
        //bean.set( "key", "fuck" );

        Debug.trace( bean.keys() );


        Vagina na = new Vagina();
        ObjectiveBean naBean = new ObjectiveBean( na );
        for ( String key : bean.keys() ) {
            naBean.set( key, bean.get( key ) );
        }
        Debug.trace( vagina, na );

        Debug.trace( ObjectiveEvaluator.MapStructures.get( na, "name" ) );
        ObjectiveEvaluator.MapStructures.set( na, "name", "test2" );
        Debug.trace( ObjectiveEvaluator.MapStructures.get( na, "name" ) );
        Debug.trace( ObjectiveEvaluator.MapStructures.get( na, "emnus" ) );
        ObjectiveEvaluator.MapStructures.set( na, "emnus", 124 );
        Debug.trace( ObjectiveEvaluator.MapStructures.get( na, "emnus" ) );
    }

    public static void testStringfiy() {
        JSONMaptron jo = new JSONMaptron( "{ k1:v1, k2:v2, k3:{ k3_1: v3_1, k3_2:[ 0, 1, true, false, undefined, [[[[],[],[],{}]]], [{ k3_a_1: v3_a_1, k3_a_2: 3.1415926 }]  ] } }" );
        Debug.trace( jo );

        Object[] arr = new Object[] { "v1", 1, 3.1415926, null, false, "v_end" };
        Debug.trace( arr );

    }

    public static void testMarshal() {
        Slave j = JSON.unmarshal( "{ /*name:Slave, length:1234, parasite:{ name: parasite, length:20241102 }, atts: { key:val }, li:[1,2,3, 'ssss'],*/" +
                "children: [{ name:Slave, length:1234, parasite:{ name: parasitec, length:20241117 }  } ] }", Slave.class );
        Debug.trace( 2, j );
    }


    public static void main( String[] args ) throws Exception {
        //String szJson = FileUtils.readAll("J:/120KWordsPhonetics.json5");
        Pinecone.init( (Object...cfg )->{

            //TestJSON.testDirectlyInjector();
            //TestJSON.testAnnotatedInjector();
            //TestJSON.testObjectom();
            //TestJSON.testObjectiveBean();
            //TestJSON.testStringfiy();
            TestJSON.testMarshal();



            return 0;
        }, (Object[]) args );
    }
}
