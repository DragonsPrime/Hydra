package com.util.json;

import java.util.List;
import java.util.Map;

import com.pinecone.framework.util.json.homotype.StructJSONEncoder;

public class Slave {
    public String    name  ;
    public long      length;
    public int       emnus;
    public Parasite  parasite;
    public Map       atts;
    public Object[]  li;

    //public Slave     child;
    public List<Slave>    children;
    //public Slave[]    children2;
    //public Object[]    children;
    //public Map[]    children;
    //public List<JSONObject>    children;
    //public List<Map>    children;
    //public List<TreeMap>    children;
    //public List<Object>    children;


    public Map<String, Slave > ms;

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

//    public void setChildren( List<Slave> slaves ) {
//        this.children = slaves;
//    }

//    public List<Slave> getChildren() {
//        return this.children;
//    }

    public String toJSONString() {
        return StructJSONEncoder.BasicEncoder.encode( this );
    }

    public String toString(){
        return StructJSONEncoder.BasicEncoder.encode( this );
    }
}