package com.util.json;

import java.util.List;
import java.util.Map;

import com.pinecone.framework.util.json.hometype.DirectJSONInjector;

public class Slave {
    public String    name  ;
    public long      length;
    public int       emnus;
    public Parasite  parasite;
    public Map       atts;
    public Object[]      li;

    public Slave() {

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

    public void setParasite2( Parasite parasite ) {
        this.parasite = parasite;
    }

    public String toJSONString() {
        return DirectJSONInjector.instance().inject( this ).toString();
    }

    public String toString(){
        return DirectJSONInjector.instance().inject( this ).toString();
    }
}