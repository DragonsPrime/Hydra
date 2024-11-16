package com.util.json;

import java.util.List;
import java.util.Map;

import com.pinecone.framework.system.prototype.ObjectiveClass;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.hometype.BeanJSONEncoder;
import com.pinecone.framework.util.json.hometype.DirectJSONInjector;
import com.pinecone.framework.util.json.hometype.StructJSONEncoder;

public class Slave {
    public String    name  ;
    public long      length;
    public int       emnus;
    public Parasite  parasite;
    public Map       atts;
    public Object[]  li;

    //public Slave     child;
    public List<Slave>    children;

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

    public List<Slave> getChildren() {
        return this.children;
    }

    public String toJSONString() {
        return StructJSONEncoder.BasicEncoder.encode( this );
    }

    public String toString(){
        return StructJSONEncoder.BasicEncoder.encode( this );
    }
}