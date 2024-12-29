package com.pinecone.hydra.umct.appoint;

import java.io.IOException;

public interface DuplexAppointClient extends AppointClient, DuplexAppointNode {

    void createPassiveChannel( int nLine );

    void embraces( int nLine ) throws IOException;

}
