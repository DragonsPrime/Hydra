package com.pinecone.hydra.umc.msg;

import java.nio.ByteOrder;
import java.util.BitSet;
import java.util.Map;

import com.pinecone.framework.system.prototype.ObjectiveBean;
import com.pinecone.framework.unit.Bitset64;
import com.pinecone.framework.unit.KeyValue;
import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.ReflectionUtils;
import com.pinecone.framework.util.json.JSON;
import com.pinecone.framework.util.json.JSONEncoder;
import com.pinecone.framework.util.json.JSONObject;
import com.pinecone.hydra.umb.UMBHead;
import com.pinecone.hydra.umb.UMBPHeadV1;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

/**
 *  Pinecone Ursus For Java UMCM [ Uniform Message Control - Base-Mutable ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  **********************************************************
 *  Uniform Message Control Protocol - Base-Mutable [UMC-BM]
 *  统一消息控制协议-变基分协议
 *  **********************************************************
 *  引入
 *  **********************************************************
 */
public class UMCMHeadV1 extends UMCHeadV1 implements UMCHead {
    public static final String     ProtocolSignature = "UMC-M/" + UMCHeadV1.ProtocolVersion;

    //protected ExtraEncode            extraEncode       = ExtraEncode.Undefined  ; // :2 sizeof( ExtraEncode/byte ) = 1

    protected long                   fieldIndexBitmap                           ; // :3 sizeof( int64 ) = 8, Field index-control bitmap.
    BitSet bitSet;


    public static void main(String[] args) {
        long i = 0;

        i = Bitset64.set( i, 2,32, true );
        Debug.greenfs(Bitset64.toBinaryStringLE(i));
    }

}