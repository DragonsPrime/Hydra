// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: rpc.proto

package com.protobuf;

public final class Rpc {
  private Rpc() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RpcRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RpcRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_RpcResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_RpcResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\trpc.proto\"-\n\nRpcRequest\022\016\n\006method\030\001 \001(" +
      "\t\022\017\n\007payload\030\002 \001(\014\"=\n\013RpcResponse\022\014\n\004cod" +
      "e\030\001 \001(\005\022\017\n\007message\030\002 \001(\t\022\017\n\007payload\030\003 \001(" +
      "\014B\020\n\014com.protobufP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_RpcRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_RpcRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RpcRequest_descriptor,
        new java.lang.String[] { "Method", "Payload", });
    internal_static_RpcResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_RpcResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_RpcResponse_descriptor,
        new java.lang.String[] { "Code", "Message", "Payload", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
