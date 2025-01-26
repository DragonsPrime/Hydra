package com.pinecone.hydra.thrift.server;

import com.pinecone.framework.system.ProxyProvokeHandleException;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.lang.DynamicFactory;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;

public class MultiplexedServer implements ThriftServer {

    protected Logger logger = LoggerFactory.getLogger( MultiplexedServer.class );

    protected JSONObject                  mjoSectionConf;

    protected ServerConnectArguments      connectionArguments ;

    protected InetSocketAddress           primaryBindAddress  ;

    protected final TMultiplexedProcessor multiplexedProcessor;

    protected TServer server;

    public MultiplexedServer( Map<String, Object> conf ){
        this( conf, null );
    }

    public MultiplexedServer( Map<String, Object> conf, ServerConnectArguments arguments ){
        this.multiplexedProcessor = new TMultiplexedProcessor();
        this.mjoSectionConf       = MultiplexedServer.asConfig( conf );
        this.connectionArguments  = arguments;

        if ( this.connectionArguments == null ) {
            this.connectionArguments = new ServerConnectionArguments( this.mjoSectionConf );
        }
    }

    protected static JSONObject asConfig( Map<String, Object> joConf ) {
        if( joConf instanceof JSONObject ) {
            return (JSONObject) joConf;
        }
        else {
            return new JSONMaptron( joConf, true );
        }
    }

    @Override
    public ThriftServer apply( Map<String, Object> conf ) {
        this.mjoSectionConf = MultiplexedServer.asConfig( conf );
        JSONObject joConf = this.getSectionConf();

        this.connectionArguments = new ServerConnectionArguments( joConf );
//        this.mChannelPool         = new PassiveRegisterChannelPool<>(
//                this, new UlfIdleFirstBalanceStrategy(), joConf.optInt( "MaximumConnections", (int)1e7 )
//        );

        return this;
    }


    public void registerProcessor( TProcessor processor ) {
        String name = processor.getClass().getName();
        String[] parts = name.split("[.$]");
        name = parts[parts.length - 2];

        this.registerProcessor( name, processor );
    }

    public void registerProcessor( String serviceName, TProcessor processor ) {
        this.multiplexedProcessor.registerProcessor( serviceName, processor );
    }

    @Override
    public void start() {
        try {
            String szHost           = this.getConnectionArguments().getHost();
            short  nPort            = this.getConnectionArguments().getPort();
            if( StringUtils.isEmpty( szHost ) ) {
                this.primaryBindAddress = new InetSocketAddress( nPort );
            }
            else {
                this.primaryBindAddress = new InetSocketAddress( szHost, nPort );
            }

            TServerSocket serverTransport = new TServerSocket( this.primaryBindAddress );

            TSimpleServer.Args tArgs = new TSimpleServer.Args(serverTransport);
            tArgs.processor( this.multiplexedProcessor );
            tArgs.protocolFactory( new TBinaryProtocol.Factory() );

            this.server = new TSimpleServer(tArgs);
            this.server.serve();

            this.logger.info( "ThriftServer started at " + this.primaryBindAddress );
        }
        catch ( TTransportException e ) {
            e.printStackTrace();
        }
    }


    public JSONObject getSectionConf() {
        return this.mjoSectionConf;
    }

    @Override
    public ServerConnectArguments getConnectionArguments() {
        return this.connectionArguments;
    }

    @Override
    public void close() {

    }
}
