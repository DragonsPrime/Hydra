package com.util.json;

import com.pinecone.framework.util.json.homotype.DirectJSONInjector;

public class Parasite {
    public String    name  ;
    public long      length;
    public int       emnus;

    public Parasite() {

    }

    public String getName() {
        return this.name;
    }

    public long getLength() {
        return this.length;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setLength( long length ) {
        this.length = length;
    }

    public String toJSONString() {
        return DirectJSONInjector.instance().inject( this ).toString();
    }

    public String toString(){
        return DirectJSONInjector.instance().inject( this ).toString();
    }
}

