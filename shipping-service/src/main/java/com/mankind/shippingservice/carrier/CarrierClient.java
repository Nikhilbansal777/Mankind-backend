package com.mankind.shippingservice.carrier;

import com.mankind.shippingservice.dto.CreateShipmentRequest;
import com.mankind.shippingservice.dto.QuoteRequest;
import com.mankind.shippingservice.dto.QuoteResponse;

public interface CarrierClient {
  QuoteResponse getQuotes(QuoteRequest req);
  BuyLabelResult buyLabel(CreateShipmentRequest req);

  // nested result type for buying a label
  record BuyLabelResult(String trackingNumber, String labelUrl, String serviceCode) {}
}
