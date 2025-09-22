package com.mankind.shippingservice.dto;

import java.util.List;

public record QuoteRequest(
  Address origin,
  Address destination,
  List<QuoteItem> items,
  Parcel parcelHint     // optional, can be null
){
  public record QuoteItem(String sku, String name, int quantity, Integer weightGrams) {}
}
