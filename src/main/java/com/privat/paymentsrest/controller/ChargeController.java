package com.privat.paymentsrest.controller;

import com.privat.paymentsrest.dto.ChargeDto;
import com.privat.paymentsrest.dto.ChargeValidation;
import com.privat.paymentsrest.service.ChargeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/charges-rest")
public class ChargeController {

    private final ChargeService chargeService;

    public ChargeController(ChargeService chargeService) {
        this.chargeService = chargeService;
    }

    @PostMapping("/{paymentId}")
    public ResponseEntity<ChargeDto> createCharge(@PathVariable("paymentId") UUID paymentId) {
        ChargeDto charge = chargeService.createCharge(paymentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(charge);
    }

    @GetMapping("/check/{paymentId}")
    public ResponseEntity<ChargeValidation> checkIfNeedCharge(@PathVariable("paymentId") UUID paymentId) {
        boolean needToCharge = chargeService.checkIfNeedCharge(paymentId);
        ChargeValidation chargeValidation = new ChargeValidation(needToCharge);

        if (!needToCharge) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(chargeValidation);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(chargeValidation);
    }

    @DeleteMapping("/{chargeId}")
    public ResponseEntity<Void> reverseCharge(@PathVariable("chargeId") UUID chargeId) {
        chargeService.reverseCharge(chargeId);
        return ResponseEntity.noContent().build();
    }
}
