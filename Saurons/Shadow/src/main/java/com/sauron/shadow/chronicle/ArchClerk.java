package com.sauron.shadow.chronicle;

import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.pinecone.hydra.auto.Instructation;
import com.sauron.radium.heistron.HTTPCrew;
import com.sauron.radium.heistron.HTTPHeist;

public abstract class ArchClerk extends HTTPCrew implements Clerk {
    protected Instructation       mAffinityPrimeDirective;
    protected JSONObject          mConfig;

    @JSONGet( "__proto__.NewsDataTable" )
    protected String              mszNewsDataTable;

    public ArchClerk( HTTPHeist heist, int id, JSONObject joConfig ) {
        super( heist, id );

        this.mConfig                 = joConfig;
        this.mAffinityPrimeDirective = new AffinitySuggestation( this );
    }

    public ArchClerk( HTTPHeist heist, int id, JSONObject joConfig, Class<?> childType ) {
        this( heist, id, joConfig );
        this.autoInject( ArchClerk.class );
        this.autoInject( childType );
    }

    @Override
    public ChronicleHeist parentHeist() {
        return (ChronicleHeist)super.parentHeist();
    }

    @Override
    public JSONObject getConfig() {
        return this.mConfig;
    }

    @Override
    public Instructation getPrimeDirective() {
        return this.mAffinityPrimeDirective;
    }
}
