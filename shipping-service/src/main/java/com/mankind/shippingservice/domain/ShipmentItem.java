package com.mankind.shippingservice.domain;

import jakarta.persistence.*;

@Entity @Table(name="shipment_items")
public class ShipmentItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="shipment_id", nullable=false)
  private Shipment shipment;

  private String sku;
  private String name;
  private int quantity;
  private Integer weightGrams;

  // getters/setters
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public Shipment getShipment(){return shipment;} public void setShipment(Shipment s){this.shipment=s;}
  public String getSku(){return sku;} public void setSku(String s){this.sku=s;}
  public String getName(){return name;} public void setName(String n){this.name=n;}
  public int getQuantity(){return quantity;} public void setQuantity(int q){this.quantity=q;}
  public Integer getWeightGrams(){return weightGrams;} public void setWeightGrams(Integer w){this.weightGrams=w;}
}
