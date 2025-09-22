package com.mankind.shippingservice.dto;

import jakarta.validation.constraints.NotBlank;

public record Address(
    @NotBlank String name,
    @NotBlank String line1,
    String line2,
    @NotBlank String city,
    @NotBlank String state,
    @NotBlank String postalCode,
    @NotBlank String country
) {}
