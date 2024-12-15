package com.pinecone.hydra.system.component;

import com.pinecone.framework.system.construction.StructureInstanceDispenser;
import com.pinecone.framework.system.construction.UnifyCentralInstanceDispenser;
import com.pinecone.framework.util.name.Namespace;
import com.pinecone.hydra.system.ArchSystemCascadeComponent;
import com.pinecone.hydra.system.HyComponent;
import com.pinecone.hydra.system.Hydrarum;

public class GenericResourceDispenserCenter extends ArchSystemCascadeComponent implements ResourceDispenserCenter {
    protected StructureInstanceDispenser mInstanceDispenser;

    public GenericResourceDispenserCenter( Namespace name, Hydrarum system, HyComponent parent ) {
        super( name, system, system.getComponentManager(), parent );

        this.mInstanceDispenser = new UnifyCentralInstanceDispenser();
    }

    public GenericResourceDispenserCenter( Hydrarum system, HyComponent parent ) {
        this( null, system, parent );
    }

    public GenericResourceDispenserCenter( Hydrarum system ) {
        this( system, null );
    }

    @Override
    public StructureInstanceDispenser getInstanceDispenser() {
        return this.mInstanceDispenser;
    }

    @Override
    public Hydrarum getSystem() {
        return super.getSystem();
    }
}