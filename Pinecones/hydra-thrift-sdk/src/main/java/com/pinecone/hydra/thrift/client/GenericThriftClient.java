package com.pinecone.hydra.thrift.client;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class GenericThriftClient<T> implements ThriftClient<T> {
    private final String host;
    private final int port;
    private final Class<T> clientClass;
    private TTransport transport;
    private T client;

    public GenericThriftClient(String host, int port, Class<T> clientClass) {
        this.host = host;
        this.port = port;
        this.clientClass = clientClass;
    }

    @Override
    public T getClient() {
        if (client == null) {
            try {
                transport = new TSocket(host, port);
                transport.open();
                TProtocol protocol = new TBinaryProtocol(transport);
                client = clientClass.getConstructor(TProtocol.class).newInstance(protocol);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create Thrift client", e);
            }
        }
        return client;
    }

    @Override
    public void close() {
        if (transport != null) {
            transport.close();
        }
    }
}
