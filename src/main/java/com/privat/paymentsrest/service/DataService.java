package com.privat.paymentsrest.service;

import com.privat.paymentsrest.dto.ChargeDto;
import com.privat.paymentsrest.dto.PaymentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Service
public class DataService {

    private final RestTemplate restTemplate;

    @Value("${dao.service.url}")
    public String serviceBaseUrl;

    public DataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PaymentDto> getPaymentsByClient(String itn){
        String endpoint = serviceBaseUrl + "/v1/payments-dao/by-itn/" + itn;
        try {
            ResponseEntity<List<PaymentDto>> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PaymentDto>>() {}
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch payments for ITN: " + itn + ". Error: " + e.getResponseBodyAsString(), e);
        }
    }

    public List<PaymentDto> getPaymentsByReceiver(String zkpo){
        String endpoint = serviceBaseUrl + "/v1/payments-dao/by-zkpo/" + zkpo;
        try {
            ResponseEntity<List<PaymentDto>> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PaymentDto>>() {}
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch payments for zkpo: " + zkpo + ". Error: " + e.getResponseBodyAsString(), e);
        }
    }

    public List<ChargeDto> getPaymentHistory(UUID paymentId){
        String endpoint = serviceBaseUrl + "/v1/charges-dao/payment/" + paymentId;
        try {
            ResponseEntity<List<ChargeDto>> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ChargeDto>>() {}
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to fetch payments for paymentId: " +paymentId + ". Error: " + e.getResponseBodyAsString(), e);
        }
    }
}
