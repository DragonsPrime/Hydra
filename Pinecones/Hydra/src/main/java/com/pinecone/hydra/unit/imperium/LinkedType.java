package com.pinecone.hydra.unit.imperium;

public enum LinkedType {
    Owned           ( "Owned" ),

    // HardLink
    Hard            ( "Hard" );

    private final String value;

    LinkedType( String value ){
        this.value = value;
    }

    public String getName(){
        return this.value;
    }

    public static String queryName( LinkedType type ) {
        return type.getName();
    }

    public static LinkedType queryLinkedType( String sz ) {
        return LinkedType.valueOf( sz );
    }
}
