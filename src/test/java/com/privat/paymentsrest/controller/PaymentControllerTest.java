package com.privat.paymentsrest.controller;


import com.privat.paymentsrest.dto.PaymentCreateDto;
import com.privat.paymentsrest.dto.PaymentDto;
import com.privat.paymentsrest.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PaymentControllerTest {

    private PaymentService paymentService;
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        paymentController = new PaymentController(paymentService);
    }

    @Test
    void testAddPayment_Success() {
        UUID cardId = UUID.randomUUID();
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto(cardId, "UA1234567890", "300335", "1234567890", "Лев Анна", BigDecimal.valueOf(100.00), 30L);
        PaymentDto mockPayment = new PaymentDto(UUID.randomUUID(), cardId, "UA1234567890", "300335", "1234567890", "Крушельницька Соломія ", BigDecimal.valueOf(100.00), 30L);

        when(paymentService.createPayment(ArgumentMatchers.any(PaymentCreateDto.class))).thenReturn(mockPayment);

        ResponseEntity<PaymentDto> response = paymentController.addPayment(paymentCreateDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        PaymentDto savedPayment = response.getBody();
        assertNotNull(savedPayment);
        assertEquals(mockPayment.id(), savedPayment.id());
        assertEquals(mockPayment.amount(), savedPayment.amount());

        verify(paymentService).createPayment(ArgumentMatchers.any(PaymentCreateDto.class));
    }

    @Test
    void testGetAllPayments_WithResults() {

        PaymentDto mockPayment = new PaymentDto(UUID.randomUUID(), UUID.randomUUID(), "UA1234567890", "300335", "1234567890",
                "Василенко Василь", BigDecimal.valueOf(100.00), 30L);
        when(paymentService.getAll()).thenReturn(Collections.singletonList(mockPayment));

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(mockPayment, response.getBody().getFirst());

        verify(paymentService).getAll();
    }

    @Test
    void testGetAllPayments_NoResults() {

        when(paymentService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());

        verify(paymentService).getAll();
    }
}
