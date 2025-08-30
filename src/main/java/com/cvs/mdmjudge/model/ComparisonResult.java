package com.cvs.mdmjudge.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComparisonResult {
    private String field;
    private List<Discrepancy> discrepancies;
    private Double confidenceScore;
    private String analysisDetails;
    private String retailerSource;
}