package com.pinecone.hydra.umb.broadcast;

public class GenericUNT implements UNT {
    protected String mszTopic;

    protected String mszNamespace;

    protected String[] mNameSegments;

    public GenericUNT ( String topic, String ns, String[] segs ) {
        this.mszTopic      = topic;
        this.mszNamespace  = ns;

        this.mNameSegments = segs;
    }

    @Override
    public String getTopic() {
        return this.mszTopic;
    }

    @Override
    public String getNamespace() {
        return this.mszNamespace;
    }

    @Override
    public String[] getNameSegments() {
        return this.mNameSegments;
    }
}
