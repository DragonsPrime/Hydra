package com.pinecone.hydra.thrift.service.impl;

import com.pinecone.hydra.thrift.service.HelloWorldService;
import org.apache.thrift.TException;

import javax.annotation.Resource;


public class HelloWorldServiceImpl implements HelloWorldService.Iface {
    @Override
    public String sayHello(String name) throws TException {
        System.out.println(name);
        try {
            return "Hello, " + name;
        } catch (Exception e) {
            e.printStackTrace();
            throw new TException("Error in sayHello: " + e.getMessage());
        }
    }
}
