package com.cvs.mdmjudge.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Discrepancy {
    private String field;
    private String mdmValue;
    private String retailerValue;
    private String retailerSource;
    private Double confidenceScore;
    private String discrepancyType; // MISSING, MISMATCH, FORMAT_ERROR
    private String severity; // HIGH, MEDIUM, LOW
}