package com.example.coindesk.factory;

import com.example.coindesk.entity.Currency;

public class CurrencyFactory {

    public static Currency create(String code, String name) {
        Currency currency = new Currency();
        currency.setCode(code);
        currency.setName(name);
        return currency;
    }
    public static Currency create(Long id, String code, String name) {
        Currency currency = new Currency();
        currency.setId(id);
        currency.setCode(code);
        currency.setName(name);
        return currency;
    }

}
