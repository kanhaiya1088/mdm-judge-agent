package com.cvs.mdmjudge.service;

import com.cvs.mdmjudge.model.RetailerRecord;

import java.util.List;


public interface ScraperService {
    /**
     * Fetch retailer records by SKU from target retailers.
     * Implementations can call APIs, scrape HTML (jsoup), or query internal marts.
     */
    List<RetailerRecord> fetchRetailerRecords(String sku, List<String> retailerIds);

    /**
     * Get retailer products by retailer ID and category.
     * Implementations can call APIs, scrape HTML (jsoup), or query internal marts.
     */
    List<RetailerRecord> getRetailerProducts(String retailerId, String category);
}