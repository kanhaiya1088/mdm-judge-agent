package com.cvs.mdmjudge.service;

import com.cvs.mdmjudge.model.RetailerRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class RetailerScraperServiceImpl implements ScraperService {
    @Override
    public List<RetailerRecord> fetchRetailerRecords(String sku,
                                                     List<String> retailerIds) {
        // TODO: Replace with real integrations. Below is a safe, deterministic stub.
        List<RetailerRecord> out = new ArrayList<>();
        for (String rid : retailerIds) {
            out.add(RetailerRecord.builder()
                    .retailerId(rid)
                    .sku(sku)
                    .title("Sample Title for " + sku + " (" + rid + ")")
                    .brand("ACME")
                    .description("Retailer description for " + sku)
                    .price(new BigDecimal("19.99"))
                    .currency("USD")
                    .gtin("0123456789012")
                    .size("M")
                    .color("Black")
                    .weightKg(0.45)
                    .attributes(Map.of("material", "cotton"))
                    .build());
        }
        return out;
    }

    public List<RetailerRecord> getRetailerProducts(String retailerId, String category) {
        // TODO: Replace with real scraping logic. This is a stub for demonstration.
        List<RetailerRecord> out = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            out.add(RetailerRecord.builder()
                    .retailerId(retailerId)
                    .sku("SKU-" + i)
                    .title("Sample Product " + i + (category != null ? " (" + category + ")" : ""))
                    .brand("ACME")
                    .description("Retailer description for SKU-" + i)
                    .price(new BigDecimal("19.99"))
                    .currency("USD")
                    .gtin("01234567890" + i)
                    .size("M")
                    .color("Black")
                    .weightKg(0.45)
                    .attributes(Map.of("material", "cotton"))
                    .build());
        }
        return out;
    }
}