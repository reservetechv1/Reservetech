package com.example.reservetech.repositories;

import com.example.reservetech.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByNomeIgnoreCaseAndAndarIgnoreCase(String nome, String andar);

    boolean existsByNomeIgnoreCaseAndAndarIgnoreCaseAndIdNot(String nome, String andar, Long id);
}