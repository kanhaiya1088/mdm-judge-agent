package com.cvs.mdmjudge.service;

import com.cvs.mdmjudge.model.ComparisonResult;
import com.cvs.mdmjudge.model.CorrectionSuggestion;
import com.cvs.mdmjudge.model.Discrepancy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QualityAssessmentService {
    private static final Map<String, Double> SEVERITY_WEIGHTS = Map.of(
            "HIGH", 1.0,
            "MEDIUM", 0.6,
            "LOW", 0.3
    );

    public String assessQuality(
            List<ComparisonResult> comparisonResults,
            List<CorrectionSuggestion> suggestions,
            double overallConfidence) {

        // Calculate weighted severity score
        double severityScore = calculateSeverityScore(comparisonResults);

        // Calculate impact score based on suggestions
        double impactScore = calculateImpactScore(suggestions);

        // Calculate final quality score
        double qualityScore = calculateQualityScore(severityScore, impactScore, overallConfidence);

        return generateQualityAssessment(qualityScore, comparisonResults, suggestions);
    }

    private double calculateSeverityScore(List<ComparisonResult> results) {
        List<Discrepancy> allDiscrepancies = results.stream()
                .flatMap(result -> result.getDiscrepancies().stream())
                .collect(Collectors.toList());

        if (allDiscrepancies.isEmpty()) {
            return 1.0;
        }

        return allDiscrepancies.stream()
                .mapToDouble(disc -> SEVERITY_WEIGHTS.getOrDefault(disc.getSeverity(), 0.5))
                .average()
                .orElse(0.5);
    }

    private double calculateImpactScore(List<CorrectionSuggestion> suggestions) {
        if (suggestions.isEmpty()) {
            return 1.0;
        }

        return suggestions.stream()
                .mapToDouble(suggestion -> {
                    String priority = suggestion.getPriority().toUpperCase();
                    return SEVERITY_WEIGHTS.getOrDefault(priority, 0.5);
                })
                .average()
                .orElse(0.5);
    }

    private double calculateQualityScore(double severityScore, double impactScore, double confidence) {
        // Weighted average of all scores
        return (severityScore * 0.4 + impactScore * 0.3 + confidence * 0.3);
    }

    private String generateQualityAssessment(
            double qualityScore,
            List<ComparisonResult> results,
            List<CorrectionSuggestion> suggestions) {

        StringBuilder assessment = new StringBuilder();

        // Overall quality rating
        assessment.append(String.format("Overall Quality Score: %.2f%n", qualityScore * 100));

        if (qualityScore >= 0.9) {
            assessment.append("Data Quality: EXCELLENT - Very high consistency with retailer data.");
        } else if (qualityScore >= 0.8) {
            assessment.append("Data Quality: GOOD - Minor discrepancies present.");
        } else if (qualityScore >= 0.6) {
            assessment.append("Data Quality: FAIR - Several significant discrepancies found.");
        } else {
            assessment.append("Data Quality: POOR - Major inconsistencies detected.");
        }

        // Summary of findings
        assessment.append("\n\nKey Findings:");
        assessment.append(String.format("%n- Total Discrepancies: %d",
                results.stream().mapToInt(r -> r.getDiscrepancies().size()).sum()));
        assessment.append(String.format("%n- Correction Suggestions: %d", suggestions.size()));

        // Critical issues
        List<Discrepancy> criticalIssues = results.stream()
                .flatMap(r -> r.getDiscrepancies().stream())
                .filter(d -> "HIGH".equals(d.getSeverity()))
                .collect(Collectors.toList());

        if (!criticalIssues.isEmpty()) {
            assessment.append("\n\nCritical Issues Requiring Attention:");
            criticalIssues.forEach(issue ->
                    assessment.append(String.format("%n- %s: %s", issue.getField(), issue.getDiscrepancyType()))
            );
        }

        return assessment.toString();
    }
}