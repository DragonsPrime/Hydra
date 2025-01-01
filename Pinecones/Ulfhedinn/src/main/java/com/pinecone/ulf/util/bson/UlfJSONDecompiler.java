package com.pinecone.ulf.util.bson;


import com.pinecone.framework.util.json.JSONArraytron;
import com.pinecone.framework.util.json.JSONDecompiler;
import com.pinecone.framework.util.json.JSONMaptron;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class UlfJSONDecompiler extends ArchJSONDecompiler implements JSONDecompiler {
    public UlfJSONDecompiler(InputStream is ) {
        super( is );
    }

    @Override
    protected Map<String, Object > newJSONObject( Object parent ) {
        return new JSONMaptron();
    }

    @Override
    protected List<Object > newJSONArray( Object parent ) {
        return new JSONArraytron();
    }

    @Override
    public Object decompile( Object parent ) {
        return super.decompile( parent );
    }

    @Override
    public Object decompile() {
        return this.decompile( null );
    }
}
