package com.example.coindesk.service;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.factory.CurrencyFactory;
import com.example.coindesk.repository.CurrencyRepository;
import com.example.coindesk.util.CryptoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CoindeskService coindeskService;

    @Mock
    private CryptoUtil cryptoUtil;

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
        Map<String, Object> mockData = Map.of(
                "bpi", Map.of("USD", Map.of("rate_float", 23342.0112)
                ), "time", Map.of("updatedISO", "2022-08-03T20:25:00+00:00")
        );

        when(coindeskService.fetchOriginalData()).thenReturn(mockData);

        Currency newCurrency = CurrencyFactory.create("USD", "美元");
        when(currencyRepository.save(any())).thenReturn(newCurrency);

        Currency result = currencyService.create(newCurrency);

        assertEquals("USD", result.getCode());
        verify(coindeskService).fetchOriginalData();
        verify(currencyRepository).save(any());
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
