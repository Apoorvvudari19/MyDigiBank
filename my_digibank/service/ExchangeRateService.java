package com.mydigibank.my_digibank.service;

import com.mydigibank.my_digibank.dto.ExchangeRateResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExchangeRateService {

    @Value("${exchange.api.key}")
    private String apiKey;

    @Value("${exchange.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public double getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            String url = apiUrl + "/" + apiKey + "/latest/" + fromCurrency;
            ExchangeRateResponseDto response = restTemplate.getForObject(url, ExchangeRateResponseDto.class);

            if (response == null || response.getConversion_rates() == null) {
                throw new RuntimeException("Invalid exchange rate response");
            }

            Double rate = response.getConversion_rates().get(toCurrency);
            if (rate == null) {
                throw new RuntimeException("Unsupported currency: " + toCurrency);
            }

            return rate;
        } catch (Exception e) {
            throw new RuntimeException("Exchange rate fetch failed: " + e.getMessage());
        }
    }

    public double convert(String fromCurrency, String toCurrency, double amount) {
        double rate = getExchangeRate(fromCurrency, toCurrency);
        System.out.printf("Converting %.2f from %s to %s using rate %.4f → %.2f%n",
            amount, fromCurrency, toCurrency, rate, amount * rate);  // ✅ Add this for debug
        return amount * rate;
    }
}
