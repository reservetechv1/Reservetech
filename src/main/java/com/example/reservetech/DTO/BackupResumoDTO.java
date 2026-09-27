package com.example.reservetech.DTO;

import com.example.reservetech.model.Backup;

import java.time.LocalDateTime;

public record BackupResumoDTO(
        Long id,
        LocalDateTime dataCriacao,
        Integer tamanhoBytes
) {
    public BackupResumoDTO(Backup backup) {
        this(backup.getId(), backup.getDataCriacao(), backup.getTamanhoBytes());
    }
}
