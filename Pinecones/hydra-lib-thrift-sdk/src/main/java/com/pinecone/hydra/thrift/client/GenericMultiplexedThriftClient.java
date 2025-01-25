package com.pinecone.hydra.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.lang.reflect.Constructor;

public class GenericMultiplexedThriftClient implements MultiplexedThriftClient{
    private String              host;

    private int                 port;

    private TTransport          transport;

    private TBinaryProtocol     protocol;

    public GenericMultiplexedThriftClient( String host, int port ) throws TTransportException {
        this.host = host;
        this.port = port;
        this.transport = new TSocket(this.host,this.port);
        this.transport.open();
        this.protocol = new TBinaryProtocol(this.transport);

    }
    @Override
    public <T extends TServiceClient> T getClient(String serviceName, Class<T> clientClass) throws TException {
        // 创建多路复用协议
        TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(this.protocol, serviceName);

        try {
            // 获取 Client 类的构造方法
            Constructor<T> constructor = clientClass.getConstructor(TProtocol.class);

            // 使用构造方法创建 Client 对象
            return constructor.newInstance(multiplexedProtocol);
        } catch (Exception e) {
            throw new TException("Failed to create client for service: " + serviceName, e);
        }
    }
}
