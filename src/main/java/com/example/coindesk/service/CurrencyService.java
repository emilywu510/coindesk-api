
package com.example.coindesk.service;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.repository.CurrencyRepository;
import com.example.coindesk.util.CryptoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository repository;

    @Autowired
    private CoindeskService coindeskService;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Autowired
    private MessageSource messageSource;

    public List<Currency> findAll() {
        return repository.findAllByOrderByCodeAsc();
    }

    public Currency create(Currency currency) {

        // 呼叫 Coindesk 取得即時匯率
        Map<String, Object> data = coindeskService.fetchOriginalData();
        Map<String, Object> bpi = (Map<String, Object>) data.get("bpi");

        // 找出該幣別的匯率
        Map<String, Object> currencyInfo = (Map<String, Object>) bpi.get(currency.getCode().toUpperCase());
        if (currencyInfo == null) {
            throw new RuntimeException("找不到該幣別匯率：" + currency.getCode());
        }

        double rate = Double.parseDouble(currencyInfo.get("rate_float").toString());

        // 加密
        String encrypted = cryptoUtil.encrypt(String.valueOf(rate));
        currency.setRateEncrypted(encrypted);
        currency.setUpdatedTime(LocalDateTime.now());
        return repository.save(currency);
    }


    public Currency update(Long id, Currency currency, Locale locale) {
        Currency existing = repository.findById(id).orElseThrow(() -> new RuntimeException(messageSource.getMessage("currency.notfound", null, locale)));
        existing.setName(currency.getName());
        return repository.save(existing);
    }

    public void delete(Long id) {

        repository.deleteById(id);
    }
}
