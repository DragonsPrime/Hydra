package com.pinecone.hydra.umc.msg;

import java.util.Map;

import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

public interface UMCCHead extends UMCHead {
    void enableField( int at );

    void disableField( int at );

    void enableField( String fieldName );

    void disableField( String fieldName );

    long getFieldIndexBitmap();

    long evalIndexBitmap();

    void setBodyLength ( long length );


    void setExtraHead            ( JSONObject jo           ) ;

    void setExtraHead            ( Map<String,Object > jo  ) ;

    void setExtraHead            ( Object o                ) ;

    void setExtraEncode          ( ExtraEncode encode      ) ;

    UMCCHead applyExHead         ( Map<String, Object > jo ) ;

    void applyExtraHeadCoder     ( ExtraHeadCoder coder    ) ;
}
