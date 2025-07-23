package com.example.coindesk.service;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.factory.CurrencyFactory;
import com.example.coindesk.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<Currency> mockList = Arrays.asList(
                CurrencyFactory.create(1L, "EUR", "歐元"),
                CurrencyFactory.create(2L, "USD", "美元")
        );

        when(currencyRepository.findAllByOrderByCodeAsc()).thenReturn(mockList);

        List<Currency> result = currencyService.findAll();

        assertEquals(2, result.size());
        assertEquals("EUR", result.get(0).getCode());
        // 呼叫一次
        verify(currencyRepository, times(1)).findAllByOrderByCodeAsc();
    }

    @Test
    void testCreate() {
        Currency newCurrency = CurrencyFactory.create( "JPY", "日圓");
        Currency savedCurrency = CurrencyFactory.create(3L, "JPY", "日圓");

        when(currencyRepository.save(newCurrency)).thenReturn(savedCurrency);

        Currency result = currencyService.create(newCurrency);

        assertEquals("JPY", result.getCode());
        verify(currencyRepository, times(1)).save(newCurrency);
    }

    @Test
    void testUpdateSuccess() {
        Currency existing = CurrencyFactory.create(1L, "USD", "美元");
        Currency updateData = CurrencyFactory.create("USD", "美金");

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(currencyRepository.save(existing)).thenReturn(existing);

        Currency result = currencyService.update(1L, updateData, Locale.TAIWAN);

        assertEquals("美金", result.getName());
        verify(currencyRepository, times(1)).findById(1L);
        verify(currencyRepository, times(1)).save(existing);
    }

    @Test
    void testUpdateNotFound() {
        Currency updateData = CurrencyFactory.create("USD", "美金");

        when(currencyRepository.findById(999L)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("currency.notfound"), any(), eq(Locale.TAIWAN)))
                .thenReturn("Currency not found");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                currencyService.update(999L, updateData, Locale.TAIWAN));

        assertEquals("Currency not found", ex.getMessage());
        verify(currencyRepository, times(1)).findById(999L);
        verify(currencyRepository, never()).save(any());
    }

    @Test
    void testDelete() {
        Long id = 10L;

        doNothing().when(currencyRepository).deleteById(id);

        currencyService.delete(id);

        verify(currencyRepository, times(1)).deleteById(id);
    }
}
