package com.pinecone.hydra.service;

public enum Status {
    New                        ( "New"               ),
    WaitingDependency          ( "WaitingDependency" ),
    Running                    ( "Running"           ),
    Paused                     ( "Paused"            ),
    Stopped                    ( "Stopped"           );

    private final String value;

    Status( String value ){
        this.value = value;
    }

    public String getName(){
        return this.value;
    }
}
