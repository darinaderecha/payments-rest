package com.privat.paymentsrest.controller;


import com.privat.paymentsrest.dto.ChargeDto;
import com.privat.paymentsrest.dto.ChargeValidation;
import com.privat.paymentsrest.dto.Status;
import com.privat.paymentsrest.service.ChargeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ChargeControllerTest {

    private ChargeService chargeService;
    private ChargeController chargeController;

    @BeforeEach
    void setUp() {
        chargeService = mock(ChargeService.class);
        chargeController = new ChargeController(chargeService);
    }

    @Test
    void testCreateCharge_Success() {
        UUID paymentId = UUID.randomUUID();
        ChargeDto mockChargeDto = new ChargeDto(UUID.randomUUID(), paymentId, null, null, Status.ACTIVE);
        when(chargeService.createCharge(paymentId)).thenReturn(mockChargeDto);

        ResponseEntity<ChargeDto> response = chargeController.createCharge(paymentId);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockChargeDto, response.getBody());

        verify(chargeService).createCharge(paymentId);
    }

    @Test
    void testCheckIfNeedCharge_ChargeNeeded() {
        UUID paymentId = UUID.randomUUID();
        when(chargeService.checkIfNeedCharge(paymentId)).thenReturn(true);
        ResponseEntity<ChargeValidation> response = chargeController.checkIfNeedCharge(paymentId);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isNeedToCharge());

        verify(chargeService).checkIfNeedCharge(paymentId);
    }


    @Test
    void testCheckIfNeedCharge_NoChargeNeeded() {
        UUID paymentId = UUID.randomUUID();
        when(chargeService.checkIfNeedCharge(paymentId)).thenReturn(false);
        ResponseEntity<ChargeValidation> response = chargeController.checkIfNeedCharge(paymentId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isNeedToCharge());

        verify(chargeService).checkIfNeedCharge(paymentId);
    }

    @Test
    void testReverseCharge_Success() {
        UUID chargeId = UUID.randomUUID();
        doNothing().when(chargeService).reverseCharge(chargeId);

        ResponseEntity<Void> response = chargeController.reverseCharge(chargeId);
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(chargeService).reverseCharge(chargeId);
    }
}

