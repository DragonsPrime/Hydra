package com.protobuf;

public class RaccoonKing implements Raccoon {
    @Override
    public String scratch( String target, int time ) {
        return "Scratch " + target + time;
    }
}
