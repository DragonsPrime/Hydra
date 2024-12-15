package com.pinecone.hydra.system.component;

import com.pinecone.framework.system.construction.StructureInstanceDispenser;
import com.pinecone.hydra.system.HyComponent;

public interface ResourceDispenserCenter extends HyComponent {
    StructureInstanceDispenser getInstanceDispenser() ;
}
