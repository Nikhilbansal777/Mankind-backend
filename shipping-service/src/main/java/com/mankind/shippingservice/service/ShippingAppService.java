package com.mankind.shippingservice.service;

import com.mankind.shippingservice.carrier.CarrierClient;
import com.mankind.shippingservice.domain.Shipment;
import com.mankind.shippingservice.domain.ShipmentItem;
import com.mankind.shippingservice.dto.CreateShipmentRequest;
import com.mankind.shippingservice.dto.QuoteRequest;
import com.mankind.shippingservice.dto.QuoteResponse;
import com.mankind.shippingservice.dto.ShipmentResponse;
import com.mankind.shippingservice.repo.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShippingAppService {
  private final ShipmentRepository repo;
  private final CarrierClient carrier;

  public ShippingAppService(ShipmentRepository repo, CarrierClient carrier) {
    this.repo = repo;
    this.carrier = carrier;
  }

  @Transactional(readOnly = true)
  public QuoteResponse quotes(QuoteRequest req) {
    return carrier.getQuotes(req);
  }

  @Transactional
  public ShipmentResponse createShipment(CreateShipmentRequest req) {
    var buy = carrier.buyLabel(req);

    Shipment s = new Shipment();
    s.setOrderId(req.orderId());
    s.setCarrier(req.carrier() != null ? req.carrier() : "MOCK");
    s.setServiceCode(buy.serviceCode());
    s.setTrackingNumber(buy.trackingNumber());
    s.setLabelUrl(buy.labelUrl());
    s.setStatus("LABEL_PURCHASED");

    if (req.items() != null) {
      for (var it : req.items()) {
        ShipmentItem si = new ShipmentItem();
        si.setShipment(s);
        si.setSku(it.sku());
        si.setName(it.name());
        si.setQuantity(it.quantity());
        si.setWeightGrams(it.weightGrams());
        s.getItems().add(si);
      }
    }
    s = repo.save(s);
    return new ShipmentResponse(s.getId(), s.getTrackingNumber(), s.getLabelUrl(), s.getStatus());
  }

  @Transactional(readOnly = true)
  public ShipmentResponse getByTracking(String tracking) {
    var s = repo.findByTrackingNumber(tracking).orElseThrow();
    return new ShipmentResponse(s.getId(), s.getTrackingNumber(), s.getLabelUrl(), s.getStatus());
  }
}
