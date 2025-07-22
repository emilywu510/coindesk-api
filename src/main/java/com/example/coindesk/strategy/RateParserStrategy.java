package com.example.coindesk.strategy;

import java.util.Map;

public interface RateParserStrategy {
    Map<String, Double> parseRates(Map<String, Object> json);
}
