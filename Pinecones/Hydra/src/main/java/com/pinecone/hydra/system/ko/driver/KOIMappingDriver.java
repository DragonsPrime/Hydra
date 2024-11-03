package com.pinecone.hydra.system.ko.driver;

import java.util.Map;

import com.pinecone.framework.system.homotype.StereotypicInjector;
import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.system.Hydrarum;

public interface KOIMappingDriver extends Pinenut {
    String getVersionSignature();

    Hydrarum getSystem();

    KOIMasterManipulator getMasterManipulator();

    // Temp, TODO
    StereotypicInjector autoConstruct( Class<?> stereotype, Map config, Object instance );
}
