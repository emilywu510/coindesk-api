package com.example.coindesk.strategy;

import java.util.HashMap;
import java.util.Map;

public class CoindeskRateParser implements RateParserStrategy {

    @Override
    public Map<String, Double> parseRates(Map<String, Object> json) {
        Map<String, Object> bpi = (Map<String, Object>) json.get("bpi");
        Map<String, Double> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : bpi.entrySet()) {
            String code = entry.getKey();
            Map<String, Object> data = (Map<String, Object>) entry.getValue();
            double rate = Double.parseDouble(data.get("rate_float").toString());
            result.put(code, rate);
        }

        return result;
    }
}
