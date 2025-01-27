package com.protobuf;

import com.pinecone.framework.util.Debug;
import com.pinecone.hydra.umct.AddressMapping;
import com.pinecone.hydra.umct.stereotype.Controller;

@Controller
//@AddressMapping( {"/fox", "/vulpis"} )
@AddressMapping( "com.protobuf.Raccoon." )
public class RaccoonController {
    //@AddressMapping( { "/scratch", "/scratches" } )
    //@AddressMapping()
    @AddressMapping( "scratch" )
    public String scratch( String target, int time ) {
        Debug.whitef( "Raccoon invoked " + target + time  );
        return "Raccoon Scratch " + target + time;
    }

    @AddressMapping( "scratchA" )
    public String scratchA( String target, int time, Rabbit map ) {
        Debug.whitef( "Raccoon invoked " + target + time + map.getName()  );
        return "Raccoon Scratch " + target + time;
    }


    @AddressMapping( "scratchV" )
    public void scratchV( String target, int time ) {
        //return "Raccoon Scratch " + target + time;
    }
}
