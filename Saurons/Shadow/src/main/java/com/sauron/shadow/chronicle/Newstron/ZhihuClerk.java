package com.sauron.shadow.chronicle.Newstron;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.JSONArray;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.pinecone.hydra.auto.Instructation;
import com.sauron.radium.heistron.HTTPHeist;
import com.sauron.shadow.chronicle.ArchClerk;
import com.sauron.shadow.chronicle.Clerk;

public class ZhihuClerk extends ArchClerk implements Clerk {
    @JSONGet( "HotlineApi" )
    protected String mszHotlineApi;

    @JSONGet( "Global.IndexFrom" )
    protected int mnIndexFrom;

    @JSONGet( "Global.IndexTo" )
    protected int mnIndexTo;

    @JSONGet( "Global.IndexStep" )
    protected int mnIndexStep;

    public ZhihuClerk( HTTPHeist heist, int id, JSONObject joConfig ){
        super( heist, id, joConfig, ZhihuClerk.class );
    }

    @Override
    public void toRavage() {
        JSONObject jZhihuIndex = new JSONMaptron();
        this.fetchZhihuByRange( this.mnIndexFrom, this.mnIndexTo, this.mnIndexStep, jZhihuIndex );

        this.parentHeist().getBasicChronicleManipulator().insertOneNews(
                this.mszNewsDataTable, "ZhihuTop" + this.mnIndexStep, this.nowDateTime(), StringUtils.addSlashes( jZhihuIndex.toJSONString() )
        );
        //Debug.trace( jZhihuIndex );
    }

    protected void fetchZhihuByRange( int nFrom, int nTo, int nStep, JSONObject jIndex ) {
        String szApi = this.mszHotlineApi; // Zhihu v4 api
        int nItems   = nTo - nFrom;
        int nRound   = nItems / nStep;
        int nMoving  = 0;
        if ( nRound * nStep < nItems ) {
            ++nRound;
        }

        if ( nRound == 1 ) {
            szApi = this.mszHotlineApi + "&limit=" + nStep + "&offset=" + nFrom + "&period=hour";
            try {
                String szHtml = this.getHTTPPage( szApi, false ).getRawText();
                jIndex.jsonDecode( szHtml );
            }
            catch ( Exception e ) {
                this.tracer().warn( String.format("[Fatality] (%s : %s) <Continue>", e.getClass().getSimpleName(), e.getMessage()) );
            }
        }
        else {
            for ( int i = 0; i < nRound; ++i ) {
                JSONObject jEach = new JSONMaptron();
                int    nStepPace = nStep;
                if ( nMoving + nStep > nItems ) {
                    nStepPace = nMoving + nStep - nItems;
                }

                szApi = this.mszHotlineApi + "&limit=" + nStepPace + "&offset=" + nMoving + "&period=hour";
                try {
                    String szHtml = this.getHTTPPage( szApi, false ).getRawText();
                    jEach.jsonDecode(szHtml);
                    JSONArray   data = jIndex.optJSONArray("data");
                    for ( int j = 0; j < data.length(); ++j ) {
                        data.put( data.getJSONObject( j ) );
                    }
                    jIndex.put("paging", jEach.getJSONObject("paging"));
                    nMoving += nStep;
                }
                catch ( Exception e ) {
                    this.tracer().warn( String.format("[Fatality] (%s : %s) <Continue>", e.getClass().getSimpleName(), e.getMessage()) );
                }
            }
        }
    }

    @Override
    public Instructation getPrimeDirective() {
        return this.mAffinityPrimeDirective;
    }
}
