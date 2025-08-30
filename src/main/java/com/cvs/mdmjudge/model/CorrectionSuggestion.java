package com.cvs.mdmjudge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionSuggestion {
    private String field;
    private String currentValue;
    private String suggestedValue;
    private Double confidenceScore;
    private String rationale;
    private String priority; // HIGH, MEDIUM, LOW
    private String source; // Which retailer data this suggestion is based on
    private String impactAssessment; // Potential impact of making this correction
    private String sku;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}