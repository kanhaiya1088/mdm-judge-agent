package com.cvs.mdmjudge.service;

import com.cvs.mdmjudge.model.ProductRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromptTemplates {
    private final ObjectMapper objectMapper;

    public String getSystemPrompt() {
        return """
                You are an expert Product Data Quality Analyst specialized in comparing and analyzing product data between MDM systems and retailer sources.
                Your task is to identify discrepancies, assess their significance, and provide detailed analysis.
                Focus on accuracy, completeness, and consistency of product information.
                """;
    }

    public String getSuggestionsSystemPrompt() {
        return """
                You are an expert Product Data Quality Analyst.
                Based on the discrepancies analysis provided, generate specific correction suggestions.
                Prioritize suggestions based on their impact on data quality and business operations.
                Provide clear, actionable recommendations with confidence scores.
                """;
    }

    public String getComparisonPrompt(ProductRecord mdmProduct, List<ProductRecord> retailerProducts) {
        try {
            String mdmJson = objectMapper.writeValueAsString(mdmProduct);
            String retailerJson = objectMapper.writeValueAsString(retailerProducts);

            return String.format("""
                    Compare the following MDM product data with retailer product data:

                    MDM Product:
                    %s

                    Retailer Products:
                    %s

                    Analyze and identify:
                    1. Field-level discrepancies
                    2. Missing information
                    3. Format inconsistencies
                    4. Data quality issues

                    Provide a structured JSON response with:
                    - List of discrepancies
                    - Confidence score for each comparison
                    - Severity level for each issue
                    - Overall data quality assessment
                    """, mdmJson, retailerJson);
        } catch (Exception e) {
            throw new RuntimeException("Error creating comparison prompt", e);
        }
    }

    public String getSuggestionsPrompt(String discrepanciesAnalysis) {
        return String.format("""
                Based on the following discrepancies analysis:

                %s

                Generate specific correction suggestions:
                1. Prioritize corrections by severity and impact
                2. Provide confidence scores for each suggestion
                3. Include rationale for each suggested change
                4. Format response as structured JSON
                """, discrepanciesAnalysis);
    }
}