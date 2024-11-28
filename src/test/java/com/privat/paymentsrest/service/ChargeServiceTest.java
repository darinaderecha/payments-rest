package com.privat.paymentsrest.service;

import com.privat.paymentsrest.dto.ChargeCreateDto;
import com.privat.paymentsrest.dto.ChargeDto;
import com.privat.paymentsrest.dto.PaymentDto;
import com.privat.paymentsrest.dto.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ChargeServiceTest {

    private RestTemplate restTemplate;
    private ChargeService chargeService;
    private PaymentDto mockPayment;

    private final String serviceBaseUrl = "http://localhost:8088";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        chargeService = new ChargeService(restTemplate);
        chargeService.serviceBaseUrl = serviceBaseUrl;
        UUID paymentId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        mockPayment = new PaymentDto(paymentId, cardId, "UA123412341234123412341234123",
                "123456", "1234567", "Григорій Квітка", BigDecimal.valueOf(100.00), 100L);
    }

    @Test
    void testCreateCharge_Success() {
        ChargeDto mockCharge = new ChargeDto(UUID.randomUUID(), mockPayment.id(), LocalDateTime.now(),
                BigDecimal.valueOf(100.00), Status.ACTIVE);
        String endpoint = serviceBaseUrl + "/v1/charges-dao/";

        when(restTemplate.exchange(
                eq(serviceBaseUrl + "/v1/payments-dao/" + mockPayment.id()),
                eq(HttpMethod.GET),
                isNull(),
                eq(PaymentDto.class)
        )).thenReturn(ResponseEntity.ok(mockPayment));

        when(restTemplate.postForEntity(eq(endpoint), any(ChargeCreateDto.class), eq(ChargeDto.class)))
                .thenReturn(ResponseEntity.ok(mockCharge));

        ChargeDto chargeDto = chargeService.createCharge(mockPayment.id());

        assertNotNull(chargeDto);
        assertEquals(mockPayment.id(), chargeDto.payment());
        assertEquals(BigDecimal.valueOf(100.00), chargeDto.amount());

        verify(restTemplate).exchange(
                eq(serviceBaseUrl + "/v1/payments-dao/" + mockPayment.id()),
                eq(HttpMethod.GET),
                isNull(),
                eq(PaymentDto.class)
        );
        verify(restTemplate).postForEntity(eq(endpoint), any(ChargeCreateDto.class), eq(ChargeDto.class));
    }


    @Test
    void testGetChargesByPaymentId_Success() {
        UUID paymentId = UUID.randomUUID();
        ChargeDto mockCharge = new ChargeDto(UUID.randomUUID(), paymentId, LocalDateTime.now(),
                BigDecimal.valueOf(100.00), Status.ACTIVE);
        String endpoint = serviceBaseUrl + "/v1/charges-dao/payment/" + paymentId;

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<ChargeDto>>>any()
        )).thenReturn(ResponseEntity.ok(Collections.singletonList(mockCharge)));

        List<ChargeDto> charges = chargeService.getChargesByPaymentId(paymentId);

        assertNotNull(charges);
        assertEquals(1, charges.size());
        assertEquals(paymentId, charges.getFirst().payment());

        verify(restTemplate).exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<ChargeDto>>>any()
        );
    }


    @Test
    void testGetPaymentById_Success() {
        UUID paymentId = mockPayment.id();
        String endpoint = serviceBaseUrl + "/v1/payments-dao/" + paymentId;

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                eq(PaymentDto.class)
        )).thenReturn(ResponseEntity.ok(mockPayment));

        PaymentDto paymentDto = chargeService.getPaymentById(paymentId);

        assertNotNull(paymentDto);
        assertEquals(paymentId, paymentDto.id());
        assertEquals(BigDecimal.valueOf(100.00), paymentDto.amount());

        verify(restTemplate).exchange(eq(endpoint), eq(HttpMethod.GET), isNull(), eq(PaymentDto.class));
    }
}

