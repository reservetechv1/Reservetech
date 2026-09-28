package com.example.reservetech.repositories;

import com.example.reservetech.model.Reserva;
import com.example.reservetech.model.StatusReserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    Page<Reserva> findByUsuarioIdOrderByDataReservaDesc(Long usuarioId, Pageable pageable);

    Page<Reserva> findByUsuarioId(Long usuarioId, Pageable pageable);

    long countByUsuarioId(Long usuarioId);

    Page<Reserva> findByUsuarioIdAndDataReservaBetween(
            Long usuarioId, LocalDate dataInicio, LocalDate dataFim, Pageable pageable
    );

    Page<Reserva> findByStatus(StatusReserva status, Pageable pageable);

    long countByStatus(StatusReserva status);

    java.util.List<Reserva> findByStatus(StatusReserva status);

    Page<Reserva> findByDataReserva(LocalDate data, Pageable pageable);

    @Query("""
        SELECT COALESCE(SUM(i.quantidadeReservada), 0) FROM ItemReserva i
        WHERE i.dispositivo.id = :dispositivoId
        AND i.reserva.dataReserva = :data
        AND i.reserva.status <> 'CANCELADA'
        AND i.reserva.horarioInicio < :horarioFim
        AND i.reserva.horarioFim > :horarioInicio
        """)
    Integer somarQuantidadeReservadaNoHorario(
            @Param("dispositivoId") Long dispositivoId,
            @Param("data") LocalDate data,
            @Param("horarioInicio") LocalTime horarioInicio,
            @Param("horarioFim") LocalTime horarioFim
    );
}