package com.mankind.shippingservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record Parcel(
    @NotNull @Min(1) Integer weightGrams,
    @NotNull @Min(1) Integer lengthCm,
    @NotNull @Min(1) Integer widthCm,
    @NotNull @Min(1) Integer heightCm
) {}
