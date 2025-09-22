package com.mankind.shippingservice.carrier;

import com.mankind.shippingservice.dto.CreateShipmentRequest;
import com.mankind.shippingservice.dto.QuoteOption;
import com.mankind.shippingservice.dto.QuoteRequest;
import com.mankind.shippingservice.dto.QuoteResponse;
// no @Component import here

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class MockCarrierClient implements CarrierClient {

  private final String labelBaseUrl;

  public MockCarrierClient(String labelBaseUrl) {
    this.labelBaseUrl = labelBaseUrl;
  }

  @Override
  public QuoteResponse getQuotes(QuoteRequest req) {
    QuoteOption fast = new QuoteOption("MOCK","EXPRESS","Mock Express",
        new java.math.BigDecimal("12.99"), "USD", "1-2");
    QuoteOption econ = new QuoteOption("MOCK","ECONOMY","Mock Economy",
        new BigDecimal("6.49"), "USD", "3-6");
    return new QuoteResponse(List.of(fast, econ));
  }

  @Override
  public CarrierClient.BuyLabelResult buyLabel(CreateShipmentRequest req) {
    String tracking = "MOCK-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    String labelUrl = labelBaseUrl + "/" + tracking + ".pdf";
    String svc = (req.serviceCode() != null ? req.serviceCode() : "ECONOMY");
    return new CarrierClient.BuyLabelResult(tracking, labelUrl, svc);
  }
}
