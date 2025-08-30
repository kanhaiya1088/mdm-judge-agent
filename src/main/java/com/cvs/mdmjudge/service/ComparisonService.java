package com.cvs.mdmjudge.service;

import com.cvs.mdmjudge.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComparisonService {
    private final OpenAIService openAIService;
    private final ObjectMapper objectMapper;
    private final QualityAssessmentService qualityAssessmentService;

    public JudgeResponse compareProducts(JudgeRequest request) {
        // Get AI analysis of discrepancies
        String discrepanciesAnalysis = openAIService.analyzeDiscrepancies(
                request.getMdmProduct(),
                request.getRetailerProducts()
        );

        log.info("Discrepancies Analysis: {}", discrepanciesAnalysis);

        // Generate correction suggestions
        String suggestionsAnalysis = openAIService.generateCorrectionSuggestions(discrepanciesAnalysis);
        log.info("Suggestions Analysis: {}", suggestionsAnalysis);

        // Process and structure the results
        List<ComparisonResult> comparisonResults = processDiscrepancies(discrepanciesAnalysis);
        List<CorrectionSuggestion> suggestions = processSuggestions(suggestionsAnalysis);

        // Calculate overall confidence score
        double overallScore = calculateOverallConfidence(comparisonResults);

        // Get quality assessment
        String qualityAssessment = qualityAssessmentService.assessQuality(
                comparisonResults,
                suggestions,
                overallScore
        );

        return JudgeResponse.builder()
                .productId(request.getMdmProduct().getProductId())
                .comparisonResults(comparisonResults)
                .suggestions(suggestions)
                .overallConfidenceScore(overallScore)
                .qualityAssessment(qualityAssessment)
                .analysisTimestamp(LocalDateTime.now().toString())
                .build();
    }

    private List<ComparisonResult> processDiscrepancies(String discrepanciesAnalysis) {
        try {
            discrepanciesAnalysis = discrepanciesAnalysis.replace("```json", "").replace("```", "");
            log.info("Processing Discrepancies Analysis: {}", discrepanciesAnalysis);

            Map<String, Object> analysisMap = objectMapper.readValue(discrepanciesAnalysis, Map.class);
            List<Map<String, Object>> discrepanciesList = (List<Map<String, Object>>) analysisMap.get("discrepancies");

            return discrepanciesList.stream()
                    .map(this::mapToComparisonResult)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error processing discrepancies analysis Error Followed By: ", e);
            throw new RuntimeException("Error processing discrepancies analysis", e);
        }
    }

    private ComparisonResult mapToComparisonResult(Map<String, Object> discrepancyMap) {
        return ComparisonResult.builder()
                .field((String) discrepancyMap.get("field"))
                .discrepancies(mapToDiscrepancies((List<Map<String, Object>>) discrepancyMap.get("details")))
                .confidenceScore((Double) discrepancyMap.get("confidenceScore"))
                .build();
    }

    private List<Discrepancy> mapToDiscrepancies(List<Map<String, Object>> details) {
        return details.stream()
                .map(detail -> Discrepancy.builder()
                        .field((String) detail.get("field"))
                        .mdmValue((String) detail.get("mdmValue"))
                        .retailerValue((String) detail.get("retailerValue"))
                        .retailerSource((String) detail.get("retailerSource"))
                        .confidenceScore((Double) detail.get("confidenceScore"))
                        .discrepancyType((String) detail.get("type"))
                        .severity((String) detail.get("severity"))
                        .build())
                .collect(Collectors.toList());
    }

    private List<CorrectionSuggestion> processSuggestions(String suggestionsAnalysis) {
        try {
            suggestionsAnalysis = suggestionsAnalysis.replace("```json", "").replace("```", "");
            Map<String, Object> suggestionsMap = objectMapper.readValue(suggestionsAnalysis, Map.class);
            List<Map<String, Object>> suggestionsList = (List<Map<String, Object>>) suggestionsMap.get("correction_suggestions");

            return suggestionsList.stream()
                    .map(this::mapToCorrectionSuggestion)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error processing suggestions analysis", e);
        }
    }

    private CorrectionSuggestion mapToCorrectionSuggestion(Map<String, Object> suggestionMap) {
        return CorrectionSuggestion.builder()
                .field((String) suggestionMap.get("field"))
                .currentValue((String) suggestionMap.get("currentValue"))
                .suggestedValue((String) suggestionMap.get("suggestion"))
                .confidenceScore((Double) suggestionMap.get("confidence_score"))
                .rationale((String) suggestionMap.get("rationale"))
                .priority((String) suggestionMap.get("priority"))
                .build();
    }

    private double calculateOverallConfidence(List<ComparisonResult> results) {
        return results.stream()
                .mapToDouble(ComparisonResult::getConfidenceScore)
                .average()
                .orElse(0.0);
    }
}