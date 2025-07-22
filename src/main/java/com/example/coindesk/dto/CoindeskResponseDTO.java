
package com.example.coindesk.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoindeskResponseDTO {
    private String updateTime;
    private List<CurrencyInfo> currencyList;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CurrencyInfo {
        private String code;
        private String name;
        private double rate;
    }
}
