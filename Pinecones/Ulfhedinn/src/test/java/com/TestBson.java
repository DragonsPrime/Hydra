package com;

import com.pinecone.Pinecone;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.json.JSONMaptron;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.ulf.util.bson.UlfJSONCompiler;
import com.pinecone.ulf.util.bson.UlfJSONDecompiler;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class TestBson {
    public static void testCompiler() throws Exception {
        UlfJSONCompiler jsonCompiler = new UlfJSONCompiler();
        JSONObject object = new JSONMaptron( "{ key:'ssss jesus christ, hahahaha', int64:64, float64:3.1415926, bool: false, 'null': null, next: { arr: ['ha', 'xi', { k: true, a: [], obj:{} }] } }" );

        try( OutputStream os = new FileOutputStream( "E:/test.bson" ) ){
            jsonCompiler.compile( object, os );
        }
    }

    public static void testDecompiler() throws Exception {
        InputStream is = new FileInputStream( "E:/test.bson" );
        UlfJSONDecompiler decompiler = new UlfJSONDecompiler( is );

        Object jo = decompiler.nextValue();

        Debug.trace( jo );
    }

    public static void main( String[] args ) throws Exception {
        //String szJson = FileUtils.readAll("J:/120KWordsPhonetics.json5");
        Pinecone.init( (Object...cfg )->{

            //TestBson.testCompiler();
            TestBson.testDecompiler();

            return 0;
        }, (Object[]) args );
    }
}
