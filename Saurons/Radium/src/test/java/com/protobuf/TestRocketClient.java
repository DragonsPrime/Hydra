package com.protobuf;

import com.pinecone.Pinecone;
import com.pinecone.framework.system.CascadeSystem;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.hydra.umb.UlfMBInformMessage;
import com.pinecone.hydra.umb.UlfPackageMessageHandler;
import com.pinecone.hydra.umb.broadcast.BroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastControlConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastControlNode;
import com.pinecone.hydra.umb.broadcast.BroadcastControlProducer;
import com.pinecone.hydra.umb.broadcast.BroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastProducer;
import com.pinecone.hydra.umb.rocket.RocketClient;
import com.pinecone.hydra.umb.rocket.RocketMQClient;
import com.pinecone.hydra.umb.rocket.UlfRocketClient;
import com.pinecone.hydra.umb.rocket.WolfMCRocketClient;
import com.pinecone.hydra.umb.wolf.WolfMCBClient;
import com.pinecone.hydra.umc.msg.Medium;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.UMCReceiver;
import com.pinecone.hydra.umc.msg.UMCTransmit;
import com.pinecone.hydra.umct.UMCTExpressHandler;
import com.pinecone.hydra.umct.WolfMCExpress;
import com.sauron.radium.Radium;

import javassist.ClassPool;


class Garrison extends Radium {
    public Garrison( String[] args, CascadeSystem parent ) {
        this( args, null, parent );
    }

    public Garrison( String[] args, String szName, CascadeSystem parent ){
        super( args, szName, parent );
    }

    @Override
    public void vitalize () throws Exception {
        //this.testFundamental();
        //this.testWolfMB();
        this.testWolfMCTB();
    }

    public void testFundamental() throws Exception {
        String nameSrvAddr = "localhost:9876";
        String groupName = "testGroup";
        String topic = "testTopic";
        String tags = "*";
        String keys = "testKeys";
        String body = "This is a test message";


        RocketClient client = new RocketMQClient( nameSrvAddr, groupName );
        BroadcastConsumer consumer = client.createConsumer( topic );
        consumer.start(new UlfPackageMessageHandler() {
            @Override
            public void onSuccessfulMsgReceived( byte[] body, Object[] args ) throws Exception {
                Debug.trace( new String( body ) );
            }
        });


        BroadcastProducer producer = client.createProducer();
        producer.start();
        producer.sendMessage( topic, body.getBytes() );

        Debug.sleep( 100000 );
    }

    public void testWolfMB() throws Exception {
        String nameSrvAddr = "localhost:9876";
        String groupName = "testGroup";
        String topic = "testTopic";
        String tags = "*";
        String keys = "testKeys";


        UlfRocketClient client = new WolfMCRocketClient( nameSrvAddr, groupName );
        UMCBroadcastConsumer consumer = client.createUlfConsumer( topic );
        consumer.start(new UMCTExpressHandler() {
            @Override
            public void onSuccessfulMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
                if ( msg.evinceTransferMessage() != null ) {
                    Debug.greenfs( msg.getHead(), new String( (byte[]) msg.evinceTransferMessage().getBody() ) );
                }
                else {
                    Debug.redf( msg.getHead() );
                }
            }
        });


        UMCBroadcastProducer producer = client.createUlfProducer();
        producer.start();

        producer.sendMessage( topic, new UlfMBInformMessage( new JSONMaptron( "{ path: '/user/getName ' }" ) ) );
        //producer.sendMessage( topic, new UlfMBInformMessage( new JSONMaptron( "{ msg: 'Jesus, Mr.Garrison! ' }" ), 0xFA ) );
        //producer.sendMessage( topic, new UlfBytesTransferMessage( new JSONMaptron( "{ msg: 'Jesus, Mr.Garrison! ' }" ), "fuck you" ) );


        Debug.sleep( 100000 );
    }

    public void testWolfMCTB() throws Exception {
        String nameSrvAddr = "b-serverkingpin:9876";
        String groupName = "testGroup";
        String topic = "testTopic";
        String tags = "*";
        String keys = "testKeys";


        BroadcastControlNode client = new WolfMCBClient( new WolfMCRocketClient( nameSrvAddr, groupName ), "", this, WolfMCExpress.class );



//        UMCBroadcastConsumer consumer = client.createUlfConsumer( topic );
//        consumer.start(new UMCTExpressHandler() {
//            @Override
//            public void onSuccessfulMsgReceived( Medium medium, UMCTransmit transmit, UMCReceiver receiver, UMCMessage msg, Object[] args ) throws Exception {
//                if ( msg.evinceTransferMessage() != null ) {
//                    Debug.greenfs( msg.getHead(), new String( (byte[]) msg.evinceTransferMessage().getBody() ) );
//                }
//                else {
//                    byte[] bytes = (byte[]) msg.evinceInformMessage().getExHead();
//                    for ( int i = 0; i < bytes.length; ++i ) {
//                        try{
//                            Debug.greenfs( (char)bytes[ i ] );
//                        }
//                        catch ( Exception e ) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    Debug.redf( msg.getHead() );
//                }
//            }
//        });


        BroadcastControlConsumer consumer = client.createBroadcastControlConsumer( topic );
        RaccoonController controller  = new RaccoonController();
        consumer.registerController( controller );
        consumer.start();



        client.compile( Raccoon.class, false );
        BroadcastControlProducer producer = client.createBroadcastControlProducer();


        producer.start();
        producer.issueInform( topic, "com.protobuf.Raccoon.scratch", "fuck you !", 2025 );



        Debug.sleep( 100000 );
    }
}


public class TestRocketClient {
    public static void main(String[] args) throws Exception {
        Pinecone.init( (Object...cfg )->{

            Garrison garrison = (Garrison) Pinecone.sys().getTaskManager().add( new Garrison( args, Pinecone.sys() ) );
            garrison.vitalize();

            return 0;
        }, (Object[]) args );
    }
}
