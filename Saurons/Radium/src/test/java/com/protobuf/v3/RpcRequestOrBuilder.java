// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: rpc.proto

package com.protobuf.v3;

public interface RpcRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:RpcRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string method = 1;</code>
   * @return The method.
   */
  java.lang.String getMethod();
  /**
   * <code>string method = 1;</code>
   * @return The bytes for method.
   */
  com.google.protobuf.ByteString
      getMethodBytes();

  /**
   * <code>bytes payload = 2;</code>
   * @return The payload.
   */
  com.google.protobuf.ByteString getPayload();
}
