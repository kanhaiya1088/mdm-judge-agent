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
public class JudgeResponse {
    private String productId;
    private List<ComparisonResult> comparisonResults;
    private List<CorrectionSuggestion> suggestions;
    private Double overallConfidenceScore;
    private String qualityAssessment;
    private String analysisTimestamp;
    private String error;  // For error messages
    private String status; // SUCCESS or ERROR

    public void setStatus(String status) {
        this.status = status;
    }
}