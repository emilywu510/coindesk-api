package com.example.coindesk.strategy;

import java.util.HashMap;
import java.util.Map;

public class MockRateParser implements RateParserStrategy {

    @Override
    public Map<String, Double> parseRates(Map<String, Object> json) {
        Map<String, Double> fixedRates = new HashMap<>();
        fixedRates.put("USD", 12345.678);
        fixedRates.put("EUR", 9876.543);
        fixedRates.put("GBP", 5555.000);
        return fixedRates;
    }
}
