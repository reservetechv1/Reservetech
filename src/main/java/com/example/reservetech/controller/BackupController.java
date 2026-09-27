package com.example.reservetech.controller;

import com.example.reservetech.DTO.BackupResumoDTO;
import com.example.reservetech.model.Backup;
import com.example.reservetech.services.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/backups")
public class BackupController {

    @Autowired
    private BackupService backupService;

    // Chave secreta usada só pelo cron externo (não é JWT, não expira).
    // Configurada via variável de ambiente BACKUP_SECRET no Render.
    @Value("${backup.secret}")
    private String backupSecret;

    @GetMapping
    public ResponseEntity<List<BackupResumoDTO>> listar() {
        return ResponseEntity.ok(backupService.listar());
    }

    // Gera um backup agora, sob demanda (ex: o TI quer garantir um backup antes de uma mudança grande)
    @PostMapping("/gerar")
    public ResponseEntity<BackupResumoDTO> gerarManualmente() {
        return ResponseEntity.status(201).body(backupService.gerarBackup());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<String> baixar(@PathVariable Long id) {
        Backup backup = backupService.buscarPorId(id);
        String nomeArquivo = "backup-reservetech-" + backup.getDataCriacao().toLocalDate() + ".json";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_JSON)
                .body(backup.getConteudoJson());
    }

    // Rota pública (sem login), protegida por uma chave secreta na URL.
    // Feita para ser chamada por um cron externo gratuito (ex: cron-job.org),
    // garantindo que o backup rode mesmo com o servidor "dormindo" no Render.
    @PostMapping("/gerar-automatico")
    public ResponseEntity<Void> gerarViaCronExterno(@RequestParam String chave) {
        if (!backupSecret.equals(chave)) {
            return ResponseEntity.status(403).build();
        }
        backupService.gerarBackup();
        return ResponseEntity.noContent().build();
    }
}
