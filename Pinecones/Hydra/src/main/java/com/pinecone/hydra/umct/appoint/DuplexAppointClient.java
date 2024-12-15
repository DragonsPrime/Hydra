package com.pinecone.hydra.umct.appoint;

public interface DuplexAppointClient extends AppointClient, DuplexAppointNode {

    void createEmbraces( int nLine );

    void joinEmbraces( int nLine );

}
