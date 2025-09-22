package com.mankind.shippingservice.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "shipments")
public class Shipment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="order_id", nullable=false) private Long orderId;
  @Column(nullable=false) private String carrier;
  private String serviceCode;
  private String labelUrl;
  private String trackingNumber;
  @Column(nullable=false) private String status = "CREATED";
  private BigDecimal cost;
  private String currency = "USD";

  @OneToMany(mappedBy="shipment", cascade=CascadeType.ALL, orphanRemoval=true)
  private List<ShipmentItem> items = new ArrayList<>();

  // getters/setters
  // (you can add Lombok later if you prefer)
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Long getOrderId(){return orderId;} public void setOrderId(Long o){this.orderId=o;}
  public String getCarrier(){return carrier;} public void setCarrier(String c){this.carrier=c;}
  public String getServiceCode(){return serviceCode;} public void setServiceCode(String s){this.serviceCode=s;}
  public String getLabelUrl(){return labelUrl;} public void setLabelUrl(String l){this.labelUrl=l;}
  public String getTrackingNumber(){return trackingNumber;} public void setTrackingNumber(String t){this.trackingNumber=t;}
  public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
  public BigDecimal getCost(){return cost;} public void setCost(BigDecimal c){this.cost=c;}
  public String getCurrency(){return currency;} public void setCurrency(String c){this.currency=c;}
  public List<ShipmentItem> getItems(){return items;} public void setItems(List<ShipmentItem> i){this.items=i;}
}
