package com.pinecone.hydra.thrift.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class GenericThriftServer implements ThriftServer{
    private final int port;
    private final TProcessor processor;
    private TServer server;

    public GenericThriftServer(int port, TProcessor processor) {
        this.port = port;
        this.processor = processor;
    }

    @Override
    public void start() {
        try {
            TServerTransport serverTransport = new TServerSocket(port);
            server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));

            System.out.println("Starting the Thrift server on port " + port + "...");
            new Thread(() -> server.serve()).start();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start Thrift server", e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop();
            System.out.println("Thrift server stopped.");
        }
    }
}
