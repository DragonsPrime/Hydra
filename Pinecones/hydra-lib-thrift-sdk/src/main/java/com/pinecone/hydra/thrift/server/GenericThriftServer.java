package com.pinecone.hydra.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;

public class GenericThriftServer <T extends TProcessor> implements ThriftServer{
    private final T processor;
    private final int port;


    public GenericThriftServer(T processor, int port) {
        this.processor = processor;
        this.port = port;
    }

    @Override
    public void start() {
        try {
            System.out.println("服务端开启....");

            // 创建服务传输层
            TServerSocket serverTransport = new TServerSocket(port);

            // 构造服务参数
            TSimpleServer.Args tArgs = new TSimpleServer.Args(serverTransport);
            tArgs.processor(processor);
            tArgs.protocolFactory(new TBinaryProtocol.Factory());

            // 创建并启动服务
            TServer server = new TSimpleServer(tArgs);;
            server.serve();
        }
        catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
