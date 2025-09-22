package com.mankind.shippingservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateShipmentRequest(
    @NotNull Long orderId,
    @Valid @NotNull Address origin,
    @Valid @NotNull Address destination,
    @NotBlank String carrier,
    @NotBlank String serviceCode,
    @Valid @NotNull List<Item> items,
    @Valid @NotNull Parcel parcel
) {
  public record Item(
      @NotBlank String sku,
      @NotBlank String name,
      @Min(1) int quantity,
      @Min(1) Integer weightGrams
  ) {}
}
