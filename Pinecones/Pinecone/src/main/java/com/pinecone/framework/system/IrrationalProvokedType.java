package com.pinecone.framework.system;

public enum IrrationalProvokedType {
    Aberration     ( "Aberration"      ), // Should never happen, but happened.
    Expected       ( "Expected"        ), // Programmatic designed exception.
    Architectural  ( "Architectural"   ), // Architecturally critical errors.
    Destructive    ( "Destructive"     ); // Structural breach trigger.

    private final String name;

    IrrationalProvokedType( String name ){
        this.name  = name;
    }

    public String getName(){
        return this.name;
    }
}
