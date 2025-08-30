package com.cvs.mdmjudge.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JudgeRequest {
    @NotNull
    private ProductRecord mdmProduct;

    @NotNull
    private List<ProductRecord> retailerProducts;

    private List<String> specificFields;  // Optional fields to focus comparison on
    private Double confidenceThreshold;   // Minimum confidence score for suggestions
    private Boolean strictMode;          // Whether to use strict matching rules
}