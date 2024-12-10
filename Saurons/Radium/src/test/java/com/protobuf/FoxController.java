package com.protobuf;

import com.pinecone.hydra.umct.AddressMapping;
import com.pinecone.hydra.umct.stereotype.Controller;

@Controller
@AddressMapping( "/fox" )
public class FoxController {
    @AddressMapping( "/scratch" )
    public String scratch( String target, int time ) {
        return "Fox Scratch " + target + time;
    }
}
