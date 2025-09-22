package com.mankind.shippingservice.repo;

import com.mankind.shippingservice.domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
  Optional<Shipment> findByTrackingNumber(String trackingNumber);
}
