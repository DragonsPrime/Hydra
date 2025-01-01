package com.pinecone.framework.util.json.homotype;

public class DirectJSONInjector extends JSONInjector {
    public DirectJSONInjector() {
        super();
    }

    public static JSONInjector instance() {
        return new DirectJSONInjector();
    }
}
