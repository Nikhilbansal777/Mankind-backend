package com.mankind.shippingservice.dto;

import java.util.List;

public record QuoteResponse(
  List<QuoteOption> options
) {}
