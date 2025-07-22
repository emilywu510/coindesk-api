package com.example.coindesk.init;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.repository.CurrencyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private CurrencyRepository currencyRepository;

    @PostConstruct
    public void init() {
        if (currencyRepository.count() == 0) {
            currencyRepository.save(Currency.builder().code("USD").name("美元").build());
            currencyRepository.save(Currency.builder().code("EUR").name("歐元").build());
            currencyRepository.save(Currency.builder().code("GBP").name("英鎊").build());
        }
    }
}