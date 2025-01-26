package com.pinecone.hydra.umc.msg;

import java.util.Map;

import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

public abstract class AbstractUMCHead implements UMCHead {
    protected abstract void setSignature            ( String signature       );

    protected abstract void setBodyLength           ( long length            );

    protected abstract void setMethod               ( UMCMethod umcMethod    );

    protected abstract void setExtraEncode          ( ExtraEncode encode     );



    protected abstract void setExtraHead            ( JSONObject jo          );

    protected abstract void setExtraHead            ( Map<String,Object > jo );

    protected abstract void setExtraHead            ( Object o               );

    protected abstract void transApplyExHead        (                        );

    protected abstract void applyExtraHeadCoder     ( ExtraHeadCoder coder   );


    protected abstract UMCHead applyExHead( Map<String, Object > jo      );


    public static void transApplyExHeadExplicitly ( AbstractUMCHead that ) {
        that.transApplyExHead();
    }

    public static void transApplyExHeadExplicitly ( UMCHead that ) {
        AbstractUMCHead.transApplyExHeadExplicitly( (AbstractUMCHead) that );
    }

    public static void transApplyExHeadExplicitly ( UMCHead that, ExtraHeadCoder coder ) {
        ( (AbstractUMCHead) that ).applyExtraHeadCoder( coder );
        AbstractUMCHead.transApplyExHeadExplicitly( (AbstractUMCHead) that );
    }

    public static void setExtraHeadExplicitly ( UMCHead that, Object o ) {
        ( (AbstractUMCHead) that ).setExtraHead( o );
    }


    protected String jsonifyExtraHead() {
        Map<String, Object > joExtraHead = this.getMapExtraHead();
        String szExtraHead;
        if( joExtraHead == null ) {
            szExtraHead = "[object Object]";
        }
        else {
            szExtraHead = JSON.stringify( this.getMapExtraHead() );
        }

        return szExtraHead;
    }

    @Override
    public String toJSONString() {
        String szExtraHead = this.jsonifyExtraHead();

        return JSONEncoder.stringifyMapFormat( new KeyValue[]{
                new KeyValue<>( "Signature"      , this.getSignature()                                             ),
                new KeyValue<>( "ExtraHeadLength", this.getExtraHeadLength()                                       ),
                new KeyValue<>( "ExtraEncode"    , this.getExtraEncode().getName()                                 ),
                new KeyValue<>( "BodyLength"     , this.getBodyLength()                                            ),
                new KeyValue<>( "KeepAlive"      , this.getKeepAlive()                                             ),
                new KeyValue<>( "Method"         , this.getMethod()                                                ),
                new KeyValue<>( "Status"         , this.getStatus().getName()                                      ),
                new KeyValue<>( "ControlBits"    , "0x" + Long.toUnsignedString( this.getControlBits(),16 )  ),
                new KeyValue<>( "IdentityId"     , this.getIdentityId()                                            ),
                new KeyValue<>( "SessionId"      , this.getSessionId()                                             ),
                new KeyValue<>( "ExtraHead"      , szExtraHead                                                     ),
        } );
    }
}
