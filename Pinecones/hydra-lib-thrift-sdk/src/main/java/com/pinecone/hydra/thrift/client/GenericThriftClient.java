package com.pinecone.hydra.thrift.client;

import org.apache.thrift.TException;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class GenericThriftClient<T extends TServiceClient> implements ThriftClient<T> {
    private String          host;

    private int             port;

    private int             outTime;

    private TTransport      transport;

    private T               client;


    public GenericThriftClient( String host, int port, int outTime, Class<T> clientClass ){
        this.host = host;
        this.port = port;
        this.outTime = outTime;
        try {
            // 创建传输层和协议
            this.transport = new TSocket(this.host, this.port, this.outTime);
            TProtocol protocol = new TBinaryProtocol(this.transport);

            // 使用反射创建客户端实例
            this.client = clientClass.getConstructor(TProtocol.class).newInstance(protocol);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the client", e);
        }
    }

    @Override
    public T getClient() throws TException {
        if (!transport.isOpen()) {
            transport.open();
        }
        return client;
    }

    @Override
    public void close() {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }
}
