package com.cvs.mdmjudge.service;

import com.cvs.mdmjudge.model.JudgeRequest;
import com.cvs.mdmjudge.model.JudgeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JudgeAgentService {
    private final ComparisonService comparisonService;


    public Flux<JudgeResponse> streamProductAnalysis(JudgeRequest request) {
        if (request.getRetailerProducts() == null || request.getRetailerProducts().isEmpty()) {
            return Flux.error(new IllegalArgumentException("No retailer products provided"));
        }
        return Flux.fromIterable(request.getRetailerProducts())
                .map(retailerProduct -> {
                    JudgeRequest singleRequest = JudgeRequest.builder()
                            .mdmProduct(request.getMdmProduct())
                            .retailerProducts(List.of(retailerProduct))
                            .specificFields(request.getSpecificFields())
                            .confidenceThreshold(request.getConfidenceThreshold())
                            .strictMode(request.getStrictMode())
                            .build();
                    return comparisonService.compareProducts(singleRequest);
                });
    }
}