package com.privat.paymentsrest.controller;

import com.privat.paymentsrest.dto.PaymentCreateDto;
import com.privat.paymentsrest.dto.PaymentDto;
import com.privat.paymentsrest.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/payments-rest")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentDto> addPayment(@RequestBody PaymentCreateDto paymentCreateDto) {
        PaymentDto savedPayment = paymentService.createPayment(paymentCreateDto);
        return ResponseEntity.ok(savedPayment);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaymentDto>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size){
       List<PaymentDto> payments = paymentService.getAll(page, size);
        return ResponseEntity.ok(payments);
    }
}

