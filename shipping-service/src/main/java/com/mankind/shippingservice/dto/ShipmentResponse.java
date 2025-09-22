package com.mankind.shippingservice.dto;

public record ShipmentResponse(
  Long shipmentId,
  String trackingNumber,
  String labelUrl,
  String status
) {}
