package com.privat.paymentsrest.service;

import com.privat.paymentsrest.dto.PaymentCreateDto;
import com.privat.paymentsrest.dto.PaymentDto;
import com.privat.paymentsrest.exception.PaymentCreationException;
import com.privat.paymentsrest.exception.PaymentFetchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class PaymentService {

    private final RestTemplate restTemplate;

    @Value("${dao.service.url}")
    public String serviceBaseUrl;

    public PaymentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentDto createPayment(PaymentCreateDto paymentCreateDto) {
        String endpoint = serviceBaseUrl + "/v1/payments-dao/";

        try {
            ResponseEntity<PaymentDto> response = restTemplate.postForEntity(endpoint, paymentCreateDto, PaymentDto.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new PaymentCreationException( "Failed to create payment: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new PaymentCreationException("Error occurred while calling payment API: " + e.getResponseBodyAsString(), e);
        }
    }

    public List<PaymentDto> getAll() {
        String endpoint = serviceBaseUrl + "/v1/payments-dao/all";

        try {
            ResponseEntity<List<PaymentDto>> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PaymentDto>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new PaymentFetchException("Failed to fetch payments: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new PaymentFetchException("Error occurred while calling payment API: " + e.getResponseBodyAsString(), e);
        }
    }
}
