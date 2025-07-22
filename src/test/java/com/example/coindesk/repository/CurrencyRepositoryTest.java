package com.example.coindesk.repository;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.factory.CurrencyFactory;
import com.example.coindesk.repository.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CurrencyRepositoryTest {

    @Autowired
    private CurrencyRepository repository;

    @Test
    void testFindByCodeReturnsCurrency() {
        repository.save(CurrencyFactory.create(null, "USD", "美元"));
        Currency found = repository.findByCode("USD").orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("美元");
    }

    @Test
    void testFindAllByOrderByCodeAsc() {
        repository.save(CurrencyFactory.create("USD", "美元"));
        repository.save(CurrencyFactory.create("EUR", "歐元"));
        List<Currency> result = repository.findAllByOrderByCodeAsc();
        assertThat(result.get(0).getCode()).isEqualTo("EUR");
        assertThat(result.get(1).getCode()).isEqualTo("USD");
    }
}
