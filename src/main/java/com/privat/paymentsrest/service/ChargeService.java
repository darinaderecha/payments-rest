package com.privat.paymentsrest.service;

import com.privat.paymentsrest.dto.ChargeCreateDto;
import com.privat.paymentsrest.dto.ChargeDto;
import com.privat.paymentsrest.dto.PaymentDto;
import com.privat.paymentsrest.dto.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChargeService {

    private final RestTemplate restTemplate;
    @Value("${dao.service.url}")
    private String serviceBaseUrl;
    private static final Logger logger = LoggerFactory.getLogger(ChargeService.class);

    private String controller = "/v1/charges-dao";

    public ChargeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ChargeDto createCharge(UUID paymentId) {
        PaymentDto payment = getPaymentById(paymentId);
        logger.info("PaymentId {}", paymentId);
        ChargeCreateDto chargeCreateDto = new ChargeCreateDto(paymentId, LocalDateTime.now(), payment.amount(), Status.ACTIVE);
        String endpoint = serviceBaseUrl + controller + "/";
        ResponseEntity<ChargeDto> response = restTemplate.postForEntity(endpoint, chargeCreateDto, ChargeDto.class);
        return response.getBody();
    }

    public Boolean checkIfNeedCharge(UUID paymentId) {
        PaymentDto payment = getPaymentById(paymentId);
        List<ChargeDto> charges = getChargesByPaymentId(paymentId);

        if ( charges != null && !charges.isEmpty()) {
            ChargeDto latestCharge = charges.getLast();
            LocalDateTime lastChargeTime = latestCharge.chargeTime();
            if (isChargeTerminated(payment.withdrawalPeriod(), lastChargeTime)) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private boolean isChargeTerminated(Long minutes, LocalDateTime lastChargeTime) {
        return minutes > Duration.between(lastChargeTime, LocalDateTime.now()).toMinutes();

    }


    public void reverseCharge(UUID chargeId) {
        String endpoint = serviceBaseUrl + controller + chargeId;
        try {
            restTemplate.exchange(
                    endpoint,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to reverse charge with ID: " + chargeId + ". Error: " + e.getResponseBodyAsString(), e);
        }
    }


    public List<ChargeDto> getChargesByPaymentId(UUID paymentId) {
        String endpoint = serviceBaseUrl + controller + "/payment/" + paymentId;
        try {
            ResponseEntity<List<ChargeDto>> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ChargeDto>>() {
                    }
            );
            return response.getBody();
        }catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch charges by paymentId: " + paymentId + ". Error: " + e.getResponseBodyAsString(), e);
        }
    }

    public PaymentDto getPaymentById(UUID paymentId) {
        String endpoint = serviceBaseUrl + "/v1/payments-dao/" + paymentId;
        ResponseEntity<PaymentDto> response = restTemplate.exchange(
                endpoint,
                HttpMethod.GET,
                null,
                PaymentDto.class
        );
        return response.getBody();
    }

}

