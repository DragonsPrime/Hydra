package com.sauron.radium.util;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.json.homotype.DirectObjectInjector;
import com.pinecone.framework.util.json.JSONArray;
import com.pinecone.framework.util.json.JSONObject;
import com.sauron.radium.heistron.HeistConfigConstants;
import com.sauron.radium.heistron.Heistum;

public class HttpBrowserConf implements Pinenut {
    public enum ProxyStrategy {
        NoProxy,
        SystemOnly,
        ProxyGroup
    }

    protected Heistum    mParentHeistum;

    public JSONObject    protoConfig;
    public JSONArray     headConfigGroup;
    public boolean       agentConfusion;
    public JSONObject    systemProxy;
    public JSONArray     proxyGroup;
    public ProxyStrategy proxyStrategy     = ProxyStrategy.NoProxy;
    public boolean       enableRandomDelay = false;
    public int           randomDelayMin    = 0;
    public int           randomDelayMax    = 0;
    public int           socketTimeout     = 20000;
    public String        charset           = "UTF-8";

    public HttpBrowserConf( Heistum heistum ) {
        this.mParentHeistum   = heistum;
        JSONObject parentConf = this.mParentHeistum.getConfig();
        this.protoConfig      = parentConf.optJSONObject( HeistConfigConstants.KeyHttpBrowser );

        if( this.protoConfig == null ) {
            this.protoConfig = this.mParentHeistum.getHeistgram().getComponentsConfig().optJSONObject( HeistConfigConstants.KeyHttpBrowser ) ;
        }

        DirectObjectInjector.instance( ConfigHelper.fnToSmallHumpName, this.getClass() ).typeInject(
                this.protoConfig, this
        );
        this.proxyStrategy = ProxyStrategy.valueOf( this.protoConfig.optString( "ProxyStrategy" ) );
    }

}