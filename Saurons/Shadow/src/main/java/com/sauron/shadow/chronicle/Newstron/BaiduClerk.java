package com.sauron.shadow.chronicle.Newstron;

import com.pinecone.framework.util.StringUtils;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.framework.util.json.homotype.JSONGet;
import com.sauron.radium.heistron.HTTPHeist;
import com.sauron.shadow.chronicle.ArchClerk;
import com.sauron.shadow.chronicle.Clerk;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import us.codecraft.webmagic.Page;

public class BaiduClerk extends ArchClerk implements Clerk {
    @JSONGet( "TopHref" )
    protected String mszTopHref;

    public BaiduClerk( HTTPHeist heist, int id, JSONObject joConfig ){
        super( heist, id, joConfig, BaiduClerk.class );
    }

    @Override
    public void toRavage() {
        JSONObject jBuiduToIndex = new JSONMaptron();
        try{
            this.parseBaiduTopIndex( jBuiduToIndex );
            this.parentHeist().getBasicChronicleManipulator().insertOneNews(
                    this.mszNewsDataTable, "BaiduTop", this.nowDateTime(), StringUtils.addSlashes( jBuiduToIndex.toJSONString() )
            );
            //Debug.trace( jBuiduToIndex );
        }
        catch ( IllegalStateException e ) {
            this.tracer().warn( String.format("[Fatality] (%s : %s) <Continue>", e.getClass().getSimpleName(), e.getMessage()) );
        }
    }

    protected void parseBaiduTopIndex( JSONObject jIndex ) throws IllegalStateException {
        try {
            Page httpPage = this.getHTTPPage( this.mszTopHref );

            Document document   = httpPage.getHtml().getDocument();
            Element rootElement = document.getElementById( "sanRoot" ); // API sanRoot 20221127
            if ( rootElement != null ) {
                Node firstChild = rootElement.childNode( 0 );
                if ( firstChild.nodeName().equals("#comment") ) {
                    String szInner = firstChild.toString();
                    int nJsonAt = szInner.indexOf("s-data:");
                    if ( nJsonAt != -1 ) {
                        nJsonAt += 7;
                        jIndex.jsonDecode( szInner.substring( nJsonAt ).trim() );
                        return;
                    }
                }
            }
        }
        catch ( Exception e ) {
            throw new IllegalStateException( "IllegalStateException: CompromisedParseBaiduTop", e );
        }

        throw new IllegalStateException( "IllegalStateException: CompromisedParseBaiduTop" );
    }
}
