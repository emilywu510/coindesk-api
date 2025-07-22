package com.example.coindesk.config;

import com.example.coindesk.strategy.CoindeskRateParser;
import com.example.coindesk.strategy.MockRateParser;
import com.example.coindesk.strategy.RateParserStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParserConfig {

    @Value("${mock.enabled:false}")
    private boolean mockEnabled;

    @Bean
    public RateParserStrategy rateParserStrategy() {
        return mockEnabled ? new MockRateParser() : new CoindeskRateParser();
    }
}
