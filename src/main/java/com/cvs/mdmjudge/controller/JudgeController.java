package com.cvs.mdmjudge.controller;

import com.cvs.mdmjudge.model.JudgeRequest;
import com.cvs.mdmjudge.model.JudgeResponse;
import com.cvs.mdmjudge.model.RetailerRecord;
import com.cvs.mdmjudge.service.ComparisonService;
import com.cvs.mdmjudge.service.JudgeAgentService;
import com.cvs.mdmjudge.service.ScraperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping(path = "/api/v1/judge", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class JudgeController {
    private final ComparisonService comparisonService;
    private final JudgeAgentService judgeAgentService;
    private final ScraperService retailerScraperService;

    @PostMapping("/compare")
    public ResponseEntity<JudgeResponse> compareProducts(@Valid @RequestBody JudgeRequest request) {
        log.info("Processing comparison request for product ID: {}", request.getMdmProduct().getProductId());
        try {
            JudgeResponse response = comparisonService.compareProducts(request);
            response.setStatus("SUCCESS");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing comparison request", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JudgeResponse.builder()
                            .productId(request.getMdmProduct().getProductId())
                            .status("ERROR")
                            .error(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/compare/batch")
    public ResponseEntity<List<JudgeResponse>> compareBatchProducts(
            @Valid @RequestBody List<JudgeRequest> requests,
            @RequestParam(defaultValue = "false") boolean async) {

        log.info("Processing batch comparison request for {} products", requests.size());
        try {
            if (async) {
                List<CompletableFuture<JudgeResponse>> futures = requests.stream()
                        .map(request -> CompletableFuture.supplyAsync(() ->
                                processRequest(request)))
                        .toList();

                List<JudgeResponse> responses = futures.stream()
                        .map(CompletableFuture::join)
                        .toList();

                return ResponseEntity.ok(responses);
            } else {
                List<JudgeResponse> responses = requests.stream()
                        .map(this::processRequest)
                        .toList();
                return ResponseEntity.ok(responses);
            }
        } catch (Exception e) {
            log.error("Error processing batch comparison request", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @PostMapping("/analyze/stream")
    public Flux<JudgeResponse> streamAnalysis(@Valid @RequestBody JudgeRequest request) {
        return judgeAgentService.streamProductAnalysis(request)
                .map(response -> {
                    response.setStatus("SUCCESS");
                    return response;
                })
                .doOnNext(response -> log.info("Streamed analysis completed"))
                .doOnError(error -> log.error("Error in analysis stream: {}", error.getMessage()));
    }

    @GetMapping("/retailer/{retailerId}/products")
    public ResponseEntity<List<RetailerRecord>> getRetailerProducts(
            @PathVariable String retailerId,
            @RequestParam(required = false) String category) {
        try {
            List<RetailerRecord> products = retailerScraperService.getRetailerProducts(retailerId, category);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            log.error("Error fetching retailer products for retailer {}: {}", retailerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/compare/quality")
    public ResponseEntity<Map<String, Object>> assessQuality(@Valid @RequestBody JudgeRequest request) {
        try {
            JudgeResponse response = comparisonService.compareProducts(request);
            Map<String, Object> qualityMetrics = Map.of(
                    "productId", request.getMdmProduct().getProductId(),
                    "overallScore", response.getOverallConfidenceScore(),
                    "qualityAssessment", response.getQualityAssessment(),
                    "suggestions", response.getSuggestions(),
                    "timestamp", LocalDateTime.now().toString(),
                    "status", "SUCCESS"
            );
            return ResponseEntity.ok(qualityMetrics);
        } catch (Exception e) {
            log.error("Error assessing product quality: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", "ERROR",
                            "error", e.getMessage(),
                            "timestamp", LocalDateTime.now().toString()
                    ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "MDM Judge Agent",
                "version", "1.0",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    private JudgeResponse processRequest(JudgeRequest request) {
        try {
            JudgeResponse response = comparisonService.compareProducts(request);
            response.setStatus("SUCCESS");
            return response;
        } catch (Exception e) {
            log.error("Error processing request for product {}: {}",
                    request.getMdmProduct().getProductId(), e.getMessage());
            return JudgeResponse.builder()
                    .productId(request.getMdmProduct().getProductId())
                    .status("ERROR")
                    .error(e.getMessage())
                    .build();
        }
    }
}