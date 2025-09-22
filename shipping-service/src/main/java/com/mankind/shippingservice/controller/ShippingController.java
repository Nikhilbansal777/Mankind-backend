package com.mankind.shippingservice.controller;

import com.mankind.shippingservice.dto.CreateShipmentRequest;
import com.mankind.shippingservice.dto.QuoteRequest;
import com.mankind.shippingservice.dto.QuoteResponse;
import com.mankind.shippingservice.dto.ShipmentResponse;
import com.mankind.shippingservice.service.ShippingAppService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
public class ShippingController {

  private final ShippingAppService app;

  public ShippingController(ShippingAppService app) { this.app = app; }

  @GetMapping("/ping")
  public ResponseEntity<String> ping() { return ResponseEntity.ok("shipping-service OK"); }

  @PostMapping("/quotes")
  public ResponseEntity<QuoteResponse> quotes(@RequestBody @Valid QuoteRequest req) {
    return ResponseEntity.ok(app.quotes(req));
  }

  @PostMapping("/shipments")
  public ResponseEntity<ShipmentResponse> create(@RequestBody @Valid CreateShipmentRequest req) {
    return ResponseEntity.ok(app.createShipment(req));
  }

  @GetMapping("/track/{tracking}")
  public ResponseEntity<ShipmentResponse> track(@PathVariable String tracking) {
    return ResponseEntity.ok(app.getByTracking(tracking));
  }
}
