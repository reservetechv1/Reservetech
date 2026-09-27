package com.example.reservetech.repositories;

import com.example.reservetech.model.Backup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BackupRepository extends JpaRepository<Backup, Long> {
    List<Backup> findAllByOrderByDataCriacaoDesc();
}
