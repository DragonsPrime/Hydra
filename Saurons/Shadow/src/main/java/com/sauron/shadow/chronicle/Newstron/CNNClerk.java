package com.sauron.shadow.chronicle.Newstron;

import com.pinecone.framework.util.Randomium;
import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.sauron.radium.heistron.HTTPHeist;
import com.sauron.shadow.chronicle.ArchClerk;
import com.sauron.shadow.chronicle.Clerk;
import us.codecraft.webmagic.Page;

public class CNNClerk extends ArchClerk implements Clerk {
    @JSONGet( "NewsHref" )
    protected String mszNewsHref;

    @JSONGet( "TopN" )
    protected int mnTopN;

    @JSONGet( "request_id" )
    protected String mszRequestId;

    public CNNClerk( HTTPHeist heist, int id, JSONObject joConfig ){
        super( heist, id, joConfig, CNNClerk.class );
    }

    @Override
    public void toRavage() {
        JSONObject jIndex = new JSONMaptron();
        this.parseCNNIndex( jIndex );
        this.parentHeist().getBasicChronicleManipulator().insertOneNews(
                this.mszNewsDataTable, "CNNNewsTop" + this.mnTopN, this.nowDateTime(), StringUtils.addSlashes( jIndex.toJSONString() )
        );
        //Debug.trace( jIndex );
    }

    protected void parseCNNIndex0( JSONObject jIndex ) throws IllegalStateException {
        String szHref     = String.format( this.mszNewsHref, this.mnTopN, ( new Randomium() ).nextString( 8 ) );
        Page httpPage     = this.getHTTPPage( szHref );
        jIndex.clear();
        jIndex.jsonDecode( httpPage.getRawText() );
        jIndex.eliminateExcepts( "result" );
    }

    protected void parseCNNIndex( JSONObject jIndex ) throws IllegalStateException {
        try {
            this.parseCNNIndex0( jIndex );
        }
        catch ( Exception e ) {
            try {
                this.parseCNNIndex0( jIndex );
            }
            catch ( Exception e1 ) {
                throw new IllegalStateException( "IllegalStateException: CompromisedParseCNN", e );
            }
        }
    }


}
