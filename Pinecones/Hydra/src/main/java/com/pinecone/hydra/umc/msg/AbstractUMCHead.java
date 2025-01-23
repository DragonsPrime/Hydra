package com.pinecone.hydra.umc.msg;

import java.util.Map;

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
}
