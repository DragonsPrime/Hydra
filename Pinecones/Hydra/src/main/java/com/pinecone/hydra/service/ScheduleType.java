package com.pinecone.hydra.service;

public enum ScheduleType {
    Automatic                  ( "Automatic"         ),
    Manual                     ( "Manual"            ),
    //DryRun                     ( "DryRun"            ),
    Disable                    ( "Disable"           );

    private final String value;

    ScheduleType( String value ){
        this.value = value;
    }

    public String getName(){
        return this.value;
    }
}
