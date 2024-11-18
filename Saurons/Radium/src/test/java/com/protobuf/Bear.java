package com.protobuf;

import java.util.List;

import com.pinecone.framework.util.json.homotype.StructJSONEncoder;

public class Bear {
    private String        name;
    private int           force;
    private List<Integer> values;
    private String        type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public List<Integer> getValues() {
        return values;
    }

    public void setValues(List<Integer> values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toJSONString() {
        return StructJSONEncoder.BasicEncoder.encode( this, true );
    }
}
