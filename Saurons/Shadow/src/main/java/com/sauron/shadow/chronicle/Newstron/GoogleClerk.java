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
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;

import java.text.Normalizer;

public class GoogleClerk extends ArchClerk implements Clerk {
    @JSONGet( "NewsHref" )
    protected String mszNewsHref;

    @JSONGet( "TopN" )
    protected int mnTopN;

    public GoogleClerk( HTTPHeist heist, int id, JSONObject joConfig ){
        super( heist, id, joConfig, GoogleClerk.class );
    }

    @Override
    public void toRavage() {
        JSONObject jGoogleIndex = new JSONMaptron();
        this.fetchGoogleNewsIndexTopN( jGoogleIndex );
        this.parentHeist().getBasicChronicleManipulator().insertOneNews(
                this.mszNewsDataTable, "GoogleNewsTop" + this.mnTopN + "Pages", this.nowDateTime(), StringUtils.addSlashes( jGoogleIndex.toJSONString() )
        );
        //Debug.trace( jGoogleIndex );
    }


    public static String googleStringNormalize( String sz ) {
        if ( sz == null || sz.isEmpty() ) {
            return sz;
        }

        int nStemFrontAt = 0;
        int nStemEndAt   = sz.length() - 1;

        while ( nStemFrontAt < sz.length() && Character.isWhitespace(sz.charAt(nStemFrontAt)) ) {
            nStemFrontAt++;
        }

        while ( nStemEndAt > nStemFrontAt && Character.isWhitespace(sz.charAt(nStemEndAt)) ) {
            nStemEndAt--;
        }

        if ( nStemFrontAt > nStemEndAt ) {
            return "";
        }

        String trimmedString = sz.substring( nStemFrontAt, nStemEndAt + 1 );
        return Normalizer.normalize( trimmedString, Normalizer.Form.NFC );
    }

    protected void fetchGoogleNewsIndexTopN( JSONObject jIndex ) {
        for ( int i = 0; i < this.mnTopN; ++i ) {
            this.parseGoogleNewsIndexSinglePage( jIndex, i );
        }
    }

    protected void parseGoogleNewsIndexSinglePage( JSONObject jIndex, int nPageId ) {
        String szHrefById = this.mszNewsHref + ( nPageId * 10 );
        try {
            Page httpPage = this.getHTTPPage( szHrefById );
            Document document = httpPage.getHtml().getDocument();
            Element lpList = document.getElementById( "search" );
            if ( lpList != null ) {
                Elements children = lpList.children();
                if( children.size() == 1 ) {
                    children = children.get(0).children();
                    if( children.size() == 1 || children.size() == 2 ) {
                        if( children.size() == 2 && children.get(0).tagName().toLowerCase().equals( "h1" ) ) {
                            children = children.get(1).children();
                        }
                        else {
                            children = children.get(0).children();
                        }

                        if( children.size() == 1 ) {
                            children = children.get(0).children();
                            if( children.size() == 1 ) {
                                children = children.get(0).children();
                            }
                        }
                    }
                }

                int nNews = nPageId * 10;
                for ( Element lpChild : children ) {
                    Elements aNodes = lpChild.getElementsByTag( "a" );
                    if( aNodes.size() == 1 ) {
                        Element aNode  = aNodes.get(0);
                        Elements nexts = aNode.children();
                        if( !nexts.isEmpty() ){
                            nexts = nexts.get(0).children();
                            if( nexts.size() == 2 ) {
                                Element contentDiv = nexts.get( 1 ); // The final content.

                                JSONObject jNews = new JSONMaptron();
                                jNews.put( "id", nNews );
                                jNews.put( "href", aNode.attr("href") );

                                Elements divElements = contentDiv.children();
                                int nDiv = 0;
                                for ( Element divElement : divElements ) {
                                    if ( divElement.tagName().equals("div") ) {
                                        if ( nDiv == 0 ) {
                                            jNews.put( "source", GoogleClerk.googleStringNormalize( divElement.text() ) );
                                        }
                                        else if ( nDiv == 1 ) {
                                            jNews.put( "title", GoogleClerk.googleStringNormalize( divElement.text() ) );
                                        }
                                        else if ( nDiv == 2 ) {
                                            jNews.put( "abstract", GoogleClerk.googleStringNormalize( divElement.text() ) );
                                        }
                                        else if ( nDiv == 4 ) {
                                            jNews.put( "timeSpan", GoogleClerk.googleStringNormalize( divElement.text() ) );
                                        }
                                        ++nDiv;
                                    }
                                }
                                jIndex.append( "data", jNews );
                                ++nNews;
                            }
                        }
                    }
                }
            }
        }
        catch ( Exception e ) {
            this.tracer().warn(String.format( "[Fatality] (%s : %s : %d) <Continue>", e.getClass().getSimpleName(), e.getMessage(), nPageId) );
        }
    }
}
