package com.example.coindesk.controller;

import com.example.coindesk.dto.CoindeskResponseDTO;
import com.example.coindesk.service.CoindeskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoindeskController.class)
public class CoindeskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CoindeskService coindeskService;

    @Test
    void testFetchRawFromCoindesk() throws Exception {
        Map<String, Object> mockJson = Map.of(
                "time", Map.of("updatedISO", "2022-08-03T20:25:00+00:00"),
                "bpi", Map.of("USD", Map.of("rate_float", 23342.0112))
        );

        when(coindeskService.fetchOriginalData()).thenReturn(mockJson);

        mockMvc.perform(get("/coindesk/raw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bpi.USD.rate_float").value(23342.0112));
    }

    @Test
    void testConvertCoindeskData() throws Exception {
        CoindeskResponseDTO.CurrencyInfo info = new CoindeskResponseDTO.CurrencyInfo("USD", "美元", 23342.0112);
        CoindeskResponseDTO mockDto = CoindeskResponseDTO.builder()
                .updateTime("2022/08/03 20:25:00")
                .currencyList(List.of(info))
                .build();

        when(coindeskService.fetchOriginalData()).thenReturn(Map.of());
        when(coindeskService.convertData(Map.of())).thenReturn(mockDto);

        mockMvc.perform(get("/coindesk/converted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updateTime").value("2022/08/03 20:25:00"))
                .andExpect(jsonPath("$.currencyList[0].code").value("USD"));
    }
}
