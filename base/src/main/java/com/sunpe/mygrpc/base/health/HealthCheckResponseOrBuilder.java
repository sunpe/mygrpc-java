// Generated by the protocol buffer compiler.  DO NOT EDIT!
// NO CHECKED-IN PROTOBUF GENCODE
// source: health.proto
// Protobuf Java Version: 4.27.2

package com.sunpe.mygrpc.base.health;

public interface HealthCheckResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:grpc.health.v1.HealthCheckResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.grpc.health.v1.HealthCheckResponse.ServingStatus status = 1;</code>
   * @return The enum numeric value on the wire for status.
   */
  int getStatusValue();
  /**
   * <code>.grpc.health.v1.HealthCheckResponse.ServingStatus status = 1;</code>
   * @return The status.
   */
  HealthCheckResponse.ServingStatus getStatus();
}
