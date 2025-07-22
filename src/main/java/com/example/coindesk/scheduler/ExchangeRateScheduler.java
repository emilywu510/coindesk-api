package com.example.coindesk.scheduler;

import com.example.coindesk.service.CoindeskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExchangeRateScheduler {

    @Autowired
    private CoindeskService coindeskService;

    // 每小時同步一次
    @Scheduled(fixedRate = 3600000)
    public void syncRates() {
        log.info("Start scheduled exchange rate sync...");
        try {
            coindeskService.updateExchangeRatesFromCoindesk();
            log.info("Exchange rate sync completed.");
        } catch (Exception e) {
            log.error("Error during scheduled syncRates", e);
        }
    }
}
