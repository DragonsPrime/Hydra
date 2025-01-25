package com.pinecone.hydra.thrift.server;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

public class MultiplexedServer implements ThriftServer{
    private final TMultiplexedProcessor multiplexedProcessor;
    private final int port;

    public MultiplexedServer(int port){
        this.multiplexedProcessor = new TMultiplexedProcessor();
        this.port = port;
    }

    public void registerService(String serviceName, TProcessor processor) {
        multiplexedProcessor.registerProcessor(serviceName, processor);
    }

    @Override
    public void start() {
        try {
            System.out.println("服务端开启....");

            // 创建服务传输层
            TServerSocket serverTransport = new TServerSocket(port);

            // 构造服务参数
            TSimpleServer.Args tArgs = new TSimpleServer.Args(serverTransport);
            tArgs.processor(multiplexedProcessor); // 使用多路复用处理器
            tArgs.protocolFactory(new TBinaryProtocol.Factory());

            // 创建并启动服务
            TServer server = new TSimpleServer(tArgs);
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
