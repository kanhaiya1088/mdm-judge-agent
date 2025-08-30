package com.cvs.mdmjudge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetailerRecord {
    private String retailerId; // e.g., RETAILER_A
    private String sku;


    private String title;
    private String brand;
    private String description;
    private BigDecimal price;
    private String currency;
    private String gtin;
    private String size;
    private String color;
    private Double weightKg;


    private Map<String, Object> attributes;
}