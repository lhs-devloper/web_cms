package com.lhsdev.cmsproject.domain.payment;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String provider; // "KCP", "KAKAOPAY", "TOSSPAY"

    private String clientKey;      // KCP: siteCd, 카카오: adminKey, 토스: clientKey
    private String secretKey;      // KCP: siteKey, 카카오: secretKey, 토스: secretKey
    private String cid;            // 카카오페이 가맹점 코드 (TC0ONETIME 등)
    private String apiUrl;         // PG사 API 기본 URL
    private String displayName;    // 표시 이름

    @Builder.Default
    @Column(nullable = false)
    private boolean isActive = false;

    public void update(String clientKey, String secretKey, String cid,
                       String apiUrl, String displayName, boolean isActive) {
        this.clientKey = clientKey;
        this.secretKey = secretKey;
        this.cid = cid;
        this.apiUrl = apiUrl;
        this.displayName = displayName;
        this.isActive = isActive;
    }
}
