package com.example.coindesk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "currency")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "rate_encrypted")
    private String rateEncrypted;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    // 取得解密匯率
    @Transient
    private Double decryptedRate;

    public Double getDecryptedRate() {
        if (rateEncrypted != null) {
            try {
                return Double.parseDouble(com.example.coindesk.util.CryptoUtil.decrypt(rateEncrypted));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
