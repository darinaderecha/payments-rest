package com.privat.paymentsrest.controller;

import com.privat.paymentsrest.dto.ChargeDto;
import com.privat.paymentsrest.dto.PaymentDto;
import com.privat.paymentsrest.dto.Status;
import com.privat.paymentsrest.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DataControllerTest {

    private DataService dataService;
    private DataController dataController;
    private PaymentDto mockPayment;

    @BeforeEach
    void setUp() {
        dataService = mock(DataService.class);
        dataController = new DataController(dataService);
        UUID paymentId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        mockPayment = new PaymentDto(paymentId, cardId, "UA123412341234123412341234123",
                "123456", "1234567", "Григорій Квітка", BigDecimal.valueOf(100.00), 100L);
    }

    @Test
    void testGetPaymentsByClient_WithResults() {
        String itn = "1234567890";
        when(dataService.getPaymentsByClient(itn)).thenReturn(Collections.singletonList(mockPayment));

        ResponseEntity<List<PaymentDto>> response = dataController.getPaymentsByClient(itn);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(mockPayment, response.getBody().getFirst());

        verify(dataService).getPaymentsByClient(itn);
    }

    @Test
    void testGetPaymentsByClient_NoResults() {

        String itn = "1234567890";
        when(dataService.getPaymentsByClient(itn)).thenReturn(Collections.emptyList());

        ResponseEntity<List<PaymentDto>> response = dataController.getPaymentsByClient(itn);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(dataService).getPaymentsByClient(itn);
    }


    @Test
    void testGetPaymentsByReceiver_WithResults() {
        String zkpo = "9876543210";
        when(dataService.getPaymentsByReceiver(zkpo)).thenReturn(Collections.singletonList(mockPayment));

        ResponseEntity<List<PaymentDto>> response = dataController.getPaymentsByReceiver(zkpo);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(mockPayment, response.getBody().getFirst());

        verify(dataService).getPaymentsByReceiver(zkpo);
    }

    @Test
    void testGetPaymentsByReceiver_NoResults() {
        String zkpo = "9876543210";
        when(dataService.getPaymentsByReceiver(zkpo)).thenReturn(Collections.emptyList());

        ResponseEntity<List<PaymentDto>> response = dataController.getPaymentsByReceiver(zkpo);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(dataService).getPaymentsByReceiver(zkpo);
    }


    @Test
    void testGetPaymentHistory_WithResults() {

        UUID paymentId = UUID.randomUUID();
        ChargeDto mockCharge = new ChargeDto(UUID.randomUUID(), paymentId,
                LocalDateTime.now(), BigDecimal.valueOf(100.00), Status.ACTIVE);
        when(dataService.getPaymentHistory(paymentId)).thenReturn(Collections.singletonList(mockCharge));

        ResponseEntity<List<ChargeDto>> response = dataController.getPaymentHistory(paymentId);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(mockCharge, response.getBody().getFirst());

        verify(dataService).getPaymentHistory(paymentId);
    }

    @Test
    void testGetPaymentHistory_NoResults() {
        UUID paymentId = UUID.randomUUID();
        when(dataService.getPaymentHistory(paymentId)).thenReturn(Collections.emptyList());

        ResponseEntity<List<ChargeDto>> response = dataController.getPaymentHistory(paymentId);
        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
        assertNull(response.getBody());

        verify(dataService).getPaymentHistory(paymentId);
    }
}

