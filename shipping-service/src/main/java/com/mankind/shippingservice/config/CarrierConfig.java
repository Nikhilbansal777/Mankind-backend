package com.mankind.shippingservice.config;

import com.mankind.shippingservice.carrier.CarrierClient;
import com.mankind.shippingservice.carrier.MockCarrierClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarrierConfig {

  @Bean
  public CarrierClient carrierClient(
      @Value("${shipping.carrier:mock}") String carrier,
      @Value("${shipping.labelBaseUrl:http://localhost:8089/labels}") String labelBaseUrl
  ) {
    if ("mock".equalsIgnoreCase(carrier)) {
      return new MockCarrierClient(labelBaseUrl);
    }
    // add real providers later
    throw new IllegalStateException("Carrier '" + carrier + "' not implemented");
  }
}
