package com.privat.paymentsrest.service;

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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DataServiceTest {

    private RestTemplate restTemplate;
    private DataService dataService;
    private PaymentDto mockPayment;

    private final String serviceBaseUrl = "http://localhost:8088";

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        dataService = new DataService(restTemplate);
        dataService.serviceBaseUrl = serviceBaseUrl;
        UUID paymentId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        mockPayment = new PaymentDto(paymentId, cardId, "UA123412341234123412341234123",
                "123456", "1234567", "Григорій Квітка", BigDecimal.valueOf(100.00), 100L);
    }

    @Test
    void testGetPaymentsByClient_Success() {
        String itn = "1234567890";
        String endpoint = serviceBaseUrl + "/v1/payments-dao/by-itn/" + itn + "?page=" + 0 + "&size=" + 10 ;

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        )).thenReturn(ResponseEntity.ok(Collections.singletonList(mockPayment)));

        List<PaymentDto> payments = dataService.getPaymentsByClient(itn, 0, 10);

        assertNotNull(payments);
        assertEquals(1, payments.size());
        assertEquals(mockPayment, payments.getFirst());

        verify(restTemplate).exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        );
    }

    @Test
    void testGetPaymentsByClient_Failure() {
        String itn = "1234567890";
        String endpoint = serviceBaseUrl + "/v1/payments-dao/by-itn/" + itn + "?page=" + 0 + "&size=" + 10;

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        )).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.NOT_FOUND, "No data found"));

        Exception exception = assertThrows(RuntimeException.class, () -> dataService.getPaymentsByClient(itn, 0, 10));

        verify(restTemplate).exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        );
    }

    @Test
    void testGetPaymentsByReceiver_Success() {

        String zkpo = "9876543210";
        String endpoint = serviceBaseUrl + "/v1/payments-dao/by-zkpo/" + zkpo + "?page=" + 0 + "&size=" + 10;

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        )).thenReturn(ResponseEntity.ok(Collections.singletonList(mockPayment)));


        List<PaymentDto> payments = dataService.getPaymentsByReceiver(zkpo, 0, 10);

        assertNotNull(payments);
        assertEquals(1, payments.size());
        assertEquals(mockPayment, payments.getFirst());

        verify(restTemplate).exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        );
    }

    @Test
    void testGetPaymentsByReceiver_Failure() {

        String zkpo = "9876543210";
        String endpoint = serviceBaseUrl + "/v1/payments-dao/by-zkpo/" + zkpo + "?page=" + 0 + "&size=" + 10;

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        )).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.NOT_FOUND, "No data found"));

        Exception exception = assertThrows(RuntimeException.class, () -> dataService.getPaymentsByReceiver(zkpo, 0, 10));;

        verify(restTemplate).exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<PaymentDto>>>any()
        );
    }

    @Test
    void testGetPaymentHistory_Success() {

        UUID paymentId = UUID.randomUUID();
        ChargeDto mockCharge = new ChargeDto(UUID.randomUUID(), paymentId, LocalDateTime.now(), BigDecimal.valueOf(100.00), Status.ACTIVE);
        String endpoint = serviceBaseUrl + "/v1/charges-dao/payment/" + paymentId;

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<ChargeDto>>>any()
        )).thenReturn(ResponseEntity.ok(Collections.singletonList(mockCharge)));


        List<ChargeDto> charges = dataService.getPaymentHistory(paymentId);

        assertNotNull(charges);
        assertEquals(1, charges.size());
        assertEquals(mockCharge, charges.getFirst());

        verify(restTemplate).exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<ChargeDto>>>any()
        );
    }

    @Test
    void testGetPaymentHistory_Failure() {

        UUID paymentId = UUID.randomUUID();
        String endpoint = serviceBaseUrl + "/v1/charges-dao/payment/" + paymentId;

        when(restTemplate.exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<ChargeDto>>>any()
        )).thenThrow(new HttpClientErrorException(org.springframework.http.HttpStatus.NOT_FOUND, "No data found"));

        Exception exception = assertThrows(RuntimeException.class, () -> dataService.getPaymentHistory(paymentId));
        assertTrue(exception.getMessage().contains("Failed to fetch payments for paymentId"));

        verify(restTemplate).exchange(
                eq(endpoint),
                eq(HttpMethod.GET),
                isNull(),
                ArgumentMatchers.<ParameterizedTypeReference<List<ChargeDto>>>any()
        );
    }
}
