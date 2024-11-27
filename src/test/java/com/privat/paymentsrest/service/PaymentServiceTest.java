package com.privat.paymentsrest.service;

import com.privat.paymentsrest.dto.PaymentCreateDto;
import com.privat.paymentsrest.dto.PaymentDto;
import com.privat.paymentsrest.exception.PaymentCreationException;
import com.privat.paymentsrest.exception.PaymentFetchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PaymentServiceTest {

    private RestTemplate restTemplate;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        paymentService = new PaymentService(restTemplate);
        String serviceBaseUrl = "http://localhost:8088";
        paymentService.serviceBaseUrl = serviceBaseUrl; // Inject mock URL
    }


    @Test
    void testCreatePayment_Success() {
        // Arrange
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(
                UUID.randomUUID(),
                "UA1234567890",
                "300335",
                "1234567890",
                "John Doe",
                BigDecimal.valueOf(100.00),
                30L
        );
        PaymentDto mockResponse = new PaymentDto(
                UUID.randomUUID(),
                paymentCreateDto.card(),
                paymentCreateDto.IBAN(),
                paymentCreateDto.MFO(),
                paymentCreateDto.ZKPO(),
                paymentCreateDto.receiverName(),
                paymentCreateDto.amount(),
                paymentCreateDto.withdrawalPeriod()
        );

        when(restTemplate.postForEntity(anyString(), any(), eq(PaymentDto.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        PaymentDto result = paymentService.createPayment(paymentCreateDto);

        assertNotNull(result);
        assertEquals(mockResponse.id(), result.id());
        assertEquals(mockResponse.amount(), result.amount());

        verify(restTemplate).postForEntity(anyString(), any(), eq(PaymentDto.class));
    }

    @Test
    void testCreatePayment_Failure() {
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(
                UUID.randomUUID(),
                "UA1234567890",
                "300335",
                "1234567890",
                "John Doe",
                BigDecimal.valueOf(100.00),
                30L
        );

        when(restTemplate.postForEntity(anyString(), any(), eq(PaymentDto.class)))
                .thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid input"));

        Exception exception = assertThrows(PaymentCreationException.class, () -> paymentService.createPayment(paymentCreateDto));
        assertTrue(exception.getMessage().contains("Error occurred while calling payment API"));

        verify(restTemplate).postForEntity(anyString(), any(), eq(PaymentDto.class));
    }

    @Test
    void testGetAll_Success() {
        PaymentDto mockPayment = new PaymentDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "UA1234567890",
                "300335",
                "1234567890",
                "John Doe",
                BigDecimal.valueOf(100.00),
                30L
        );
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        )).thenReturn(ResponseEntity.ok(Collections.singletonList(mockPayment)));

        List<PaymentDto> payments = paymentService.getAll();

        assertNotNull(payments);
        assertEquals(1, payments.size());
        assertEquals(mockPayment, payments.getFirst());

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        );
    }

    @Test
    void testGetAll_Failure() {
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        )).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Server error"));

        Exception exception = assertThrows(PaymentFetchException.class, () -> paymentService.getAll());
        assertTrue(exception.getMessage().contains("Error occurred while calling payment API"));

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        );
    }
}

