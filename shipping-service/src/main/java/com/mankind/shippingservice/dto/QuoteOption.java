package com.mankind.shippingservice.dto;

import java.math.BigDecimal;

public record QuoteOption(
  String carrier,
  String serviceCode,
  String serviceName,
  BigDecimal cost,
  String currency,
  String etaDays   // "2-4", "1", etc.
) {}
