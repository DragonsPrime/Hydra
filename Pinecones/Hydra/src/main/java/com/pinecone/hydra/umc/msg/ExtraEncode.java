package com.pinecone.hydra.umc.msg;

public enum ExtraEncode {
    Undefined   ( 0x01, "Undefined"   ),
    JSONString  ( 0x02, "JSONString"  ),
    Binary      ( 0x03, "Binary"      ), // Bson
    Prototype   ( 0x04, "Prototype"   ), // Prototype Raw Binary
    Custom      ( 0xFF, "Custom"      );

    private final int value;

    private final String name;

    ExtraEncode( int value, String name ){
        this.value = value;
        this.name  = name;
    }

    public String getName(){
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public short getShortValue() {
        return (short) this.value;
    }

    public static ExtraEncode asValue( int val ) {
        for ( ExtraEncode type : ExtraEncode.values() ) {
            if ( type.getValue() == val ) {
                return type;
            }
        }

        return ExtraEncode.Custom;
    }
}
