package com.privat.paymentsrest.controller;

import com.privat.paymentsrest.dto.ChargeDto;
import com.privat.paymentsrest.dto.PaymentDto;
import com.privat.paymentsrest.service.DataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<PaymentDto>> getPaymentsByClient(@PathVariable String itn,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "100") int size) {
        List<PaymentDto> payments = dataService.getPaymentsByClient(itn, page, size);
        if (payments == null || payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/by-zkpo/{zkpo}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByReceiver(@PathVariable String zkpo,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "100") int size) {
        List<PaymentDto> payments = dataService.getPaymentsByReceiver(zkpo, page, size);
        if (payments == null || payments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/charge-history/{paymentId}")
    public ResponseEntity<List<ChargeDto>> getPaymentHistory(@PathVariable UUID paymentId) {
        List<ChargeDto> charges = dataService.getPaymentHistory(paymentId);
        if (charges == null ||charges.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(charges);
    }
}
