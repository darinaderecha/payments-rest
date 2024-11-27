package com.privat.paymentsrest.controller;

import com.privat.paymentsrest.dto.ChargeDto;
import com.privat.paymentsrest.dto.PaymentDto;
import com.privat.paymentsrest.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/data-rest")
public class DataController {

    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/by-itn/{itn}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByClient(@PathVariable String itn) {
        List<PaymentDto> payments = dataService.getPaymentsByClient(itn);
        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/by-zkpo/{zkpo}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByReceiver(@PathVariable String zkpo) {
        List<PaymentDto> payments = dataService.getPaymentsByReceiver(zkpo);
        if (payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/charge-history/{paymentId}")
    public ResponseEntity<List<ChargeDto>> getPaymentHistory(@PathVariable UUID paymentId) {
        List<ChargeDto> charges = dataService.getPaymentHistory(paymentId);
        if (charges.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(charges);
    }
}
