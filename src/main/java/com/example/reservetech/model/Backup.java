package com.example.reservetech.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// Guarda uma "foto" dos dados do sistema num momento, em formato JSON.
// Backups antigos são apagados automaticamente (ver BackupService).
@Entity
@Table(name = "backups")
@Getter
@Setter
@NoArgsConstructor
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudoJson;

    @Column(nullable = false)
    private Integer tamanhoBytes;
}
