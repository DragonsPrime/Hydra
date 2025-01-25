package com.pinecone.hydra.umc.msg;

import com.pinecone.framework.system.prototype.Pinenut;

/**
 *  Pinecone Ursus For Java EMC [ Elastic Uniform Message Control ]
 *  Author: Harold.E / JH.W (DragonKing)
 *  Copyright © 2008 - 2028 Bean Nuts Foundation All rights reserved.
 *  ********************************************************************************************************************
 *  Variable-length message protocol header
 *  可变长弹性协议头
 *  ********************************************************************************************************************
 *  A typical top-level UMC header only contains the signature and the ExtraHeadLength.
 *  In practice, the ExtraHeadLength is usually left empty, so the minimum sizeof = 8.
 *  This allows for elastic and excellent performance while ensuring the supreme uniformity based on the specific message type.
 *  Unlike C/C++, other languages cannot use unions or struct segments for memory manipulation.
 *  To ensure maximum compatibility, the UMC uses the highest bit length (dword/qword).
 *  For uniformity, it will inevitably lead to memory overhead, but the sacrifice is worth it.
 *  ********************************************************************************************************************
 *  一个典型顶级的UMC头仅包含协议签名、扩展头长度，实践中扩展头长度字段（ExtraHeadLength）
 *  默认是置空的，因此最小 sizeof = 8。这样可根据具体的消息类型，灵活确保最高统一抽象和极致性能。
 *  由于其他语言不像C/C++，无法使用union、结构体段等内存手段，UMC协议为确保最高兼容，因此使用了最高位长（dword/qword）
 *  这会不可避免地带来内存损益，为了统一牺牲是值得的。
 *  ********************************************************************************************************************
 */
public interface EMCHead extends Pinenut {
    String          getSignature();

    int             getSignatureLength();

    int             sizeof();

    int             fieldsSize(); // UMCHead (Non-Dynamic) Fields size.

    int             getExtraHeadLength();
}
