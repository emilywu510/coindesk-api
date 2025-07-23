package com.example.coindesk.service;

import com.example.coindesk.dto.CoindeskResponseDTO;
import com.example.coindesk.entity.Currency;
import com.example.coindesk.factory.CurrencyFactory;
import com.example.coindesk.repository.CurrencyRepository;
import com.example.coindesk.util.CryptoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CoindeskServiceTest {

    @Autowired
    private CoindeskService service;

    @MockBean
    private CurrencyRepository mockRepo;

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private CryptoUtil cryptoUtil;

    @Test
    void testFetchOriginalDataFallbackOnFailure() {
        // 模擬呼叫API時丟出例外
        when(restTemplate.getForObject("https://api.coindesk.com/v1/bpi/currentprice.json", String.class))
                .thenThrow(new RuntimeException("Mock API failure"));

        Map<String, Object> result = service.fetchOriginalData();

        assertTrue(result.containsKey("bpi"));
        Map<String, Object> bpi = (Map<String, Object>) result.get("bpi");

        assertTrue(bpi.containsKey("USD"));
        Map<String, Object> usd = (Map<String, Object>) bpi.get("USD");

        assertEquals(23342.0112, usd.get("rate_float"));

        Map<String, Object> time = (Map<String, Object>) result.get("time");
        assertEquals("2022-08-03T20:25:00+00:00", time.get("updatedISO"));
    }
    @Test
    void testConvertDataParsesCorrectly() throws Exception {
        String encryptedRate = cryptoUtil.encrypt("23342.0112");

        Currency currency = CurrencyFactory.create("USD", "美元");
        currency.setRateEncrypted(encryptedRate);

        when(mockRepo.findByCode("USD")).thenReturn(Optional.of(currency));

        Map<String, Object> mockJson = Map.of(
                "time", Map.of("updatedISO", "2022-08-03T20:25:00+00:00"),
                "bpi", Map.of("USD", Map.of("rate_float", 23342.0112))
        );

        CoindeskResponseDTO dto = service.convertData(mockJson);

        assertEquals("2022/08/03 20:25:00", dto.getUpdateTime());
        assertEquals("美元", dto.getCurrencyList().get(0).getName());
        assertEquals("USD", dto.getCurrencyList().get(0).getCode());
        assertEquals(23342.0112, dto.getCurrencyList().get(0).getRate());
    }

}
