package com.cvs.mdmjudge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRecord {
    private String productId;
    private String name;
    private String description;
    private String brand;
    private String category;
    private String upc;
    private String sku;
    private Double price;
    private Map<String, String> attributes;
    private String source; // MDM or Retailer name
}