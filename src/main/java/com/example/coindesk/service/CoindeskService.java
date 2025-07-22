package com.example.coindesk.service;

import com.example.coindesk.dto.CoindeskResponseDTO;
import com.example.coindesk.entity.Currency;
import com.example.coindesk.repository.CurrencyRepository;
import com.example.coindesk.strategy.RateParserStrategy;
import com.example.coindesk.util.CryptoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class CoindeskService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private RateParserStrategy rateParserStrategy;

    private final String API = "https://api.coindesk.com/v1/bpi/currentprice.json";

    public Map<String, Object> fetchOriginalData() {
        try {
            log.info("Sending GET to CoinDesk API: {}", API);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(API, HttpMethod.GET, null, Map.class);

            log.info("Received response from CoinDesk API: {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.warn("Failed to call CoinDesk API, using fallback data", e);
            try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream is = getClass().getClassLoader().getResourceAsStream("mock/coindesk-response.json");
                Map<String, Object> fallback = mapper.readValue(is, Map.class);
                log.info("Loaded mock response: {}", fallback);
                return fallback;
            } catch (IOException ioException) {
                log.error("Failed to load fallback mock data", ioException);
                throw new RuntimeException("Unable to fetch exchange rate data.");
            }
        }
    }

    public CoindeskResponseDTO convertData(Map<String, Object> json) {
        Map<String, Object> time = (Map<String, Object>) json.get("time");
        String timeStr = (String) time.get("updatedISO");

        OffsetDateTime dateTime = OffsetDateTime.parse(timeStr);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").withZone(ZoneOffset.UTC);
        String formattedTime = formatter.format(dateTime);

        Map<String, Double> rates = rateParserStrategy.parseRates(json);
        List<CoindeskResponseDTO.CurrencyInfo> list = new ArrayList<>();

        for (Map.Entry<String, Double> entry : rates.entrySet()) {
            String code = entry.getKey();
            String name = currencyRepository.findByCode(code)
                    .map(Currency::getName)
                    .orElse("未知幣別");

            Double decryptedRate = currencyRepository.findByCode(code)
                    .map(Currency::getDecryptedRate)
                    // 如果沒加密值就用原值
                    .orElse(entry.getValue());

            list.add(new CoindeskResponseDTO.CurrencyInfo(code, name, decryptedRate));
        }

        return CoindeskResponseDTO.builder()
                .updateTime(formattedTime)
                .currencyList(list)
                .build();
    }

    public void updateExchangeRatesFromCoindesk() {
        Map<String, Object> json = fetchOriginalData();

        try {
            Map<String, Object> time = (Map<String, Object>) json.get("time");
            String updatedISO = (String) time.get("updatedISO");
            LocalDateTime updatedTime = LocalDateTime.parse(updatedISO, DateTimeFormatter.ISO_DATE_TIME);

            Map<String, Double> rates = rateParserStrategy.parseRates(json);

            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                String code = entry.getKey();
                double rate = entry.getValue();

                currencyRepository.findByCode(code).ifPresent(currency -> {
                    currency.setRateEncrypted(CryptoUtil.encrypt(String.valueOf(rate)));
                    currency.setUpdatedTime(updatedTime);
                    currencyRepository.save(currency);
                    log.info("Updated currency {} with rate {} at {}", code, rate, updatedTime);
                });
            }
        } catch (Exception e) {
            log.error("Failed to update exchange rates", e);
        }
    }
}
