package com.example.DocBot.controller;

import com.example.DocBot.dto.request.ProviderSearchRequest;
import com.example.DocBot.dto.response.ProviderResponse;
import com.example.DocBot.service.ProviderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    @PostMapping("/search")
    public ResponseEntity<ProviderResponse> searchProviders(
            @Valid @RequestBody ProviderSearchRequest request) {
        ProviderResponse response = providerService.searchProviders(request);
        return ResponseEntity.ok(response);
    }
}
