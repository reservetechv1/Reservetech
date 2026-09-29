package com.example.reservetech.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Endpoint leve, sem autenticação e sem tocar no banco, usado só pra um
// serviço externo (cron-job.org, UptimeRobot, etc.) pingar periodicamente
// e evitar que o Render coloque a aplicação pra dormir no plano gratuito.
@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }
}
