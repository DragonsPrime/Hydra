package com.pinecone.hydra.umct.appoint;

import java.io.IOException;

public interface DuplexAppointClient extends AppointClient, DuplexAppointNode {

    void createEmbraces( int nLine );

    void joinEmbraces( int nLine ) throws IOException;

}
