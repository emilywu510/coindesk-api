package com.example.coindesk.controller;

import com.example.coindesk.entity.Currency;
import com.example.coindesk.factory.CurrencyFactory;
import com.example.coindesk.service.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
public class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetCurrenciesSortedByCode() throws Exception {
        when(currencyService.findAll()).thenReturn(List.of(
            CurrencyFactory.create(1L, "EUR", "歐元"),
            CurrencyFactory.create(2L, "USD", "美元")
        ));

        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("EUR"));
    }

    @Test
    void testCreateCurrency() throws Exception {
        Currency input = CurrencyFactory.create(null, "GBP", "英鎊");
        Currency saved = CurrencyFactory.create(3L, "GBP", "英鎊");

        when(currencyService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/currencies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("GBP"));
    }

    @Test
    void testUpdateCurrency() throws Exception {
        Currency existing = CurrencyFactory.create("USD", "美元");
        Currency updated = CurrencyFactory.create("USD", "美金");

        when(currencyService.update(eq(1L), any(Currency.class), any(Locale.class))).thenReturn(updated);

        mockMvc.perform(put("/currencies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("美金"));
    }

    @Test
    void testDeleteCurrency() throws Exception {
        doNothing().when(currencyService).delete(1L);
        mockMvc.perform(delete("/currencies/1"))
                .andExpect(status().isNoContent());
    }
}
