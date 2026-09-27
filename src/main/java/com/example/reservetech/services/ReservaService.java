package com.example.reservetech.services;

import com.example.reservetech.DTO.ItemReservaRequestDTO;
import com.example.reservetech.DTO.ReservaRequestDTO;
import com.example.reservetech.DTO.ReservaResponseDTO;
import com.example.reservetech.DTO.ReservaUpdateDTO;
import com.example.reservetech.exceptions.ConflitoDeHorarioException;
import com.example.reservetech.exceptions.DispositivoNaoEncontradoException;
import com.example.reservetech.exceptions.ReservaNaoEncontradaException;
import com.example.reservetech.model.*;
import com.example.reservetech.repositories.DispositivoRepository;
import com.example.reservetech.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Autowired
    private SalaService salaService;

    public long contarPendentes() {
        return reservaRepository.countByStatus(StatusReserva.PENDENTE);
    }

    public ReservaResponseDTO criar(ReservaRequestDTO dto, Usuario usuarioLogado) {

        for (ItemReservaRequestDTO itemDto : dto.itens()) {
            Dispositivo dispositivo = buscarDispositivo(itemDto.dispositivoId());

            Integer quantidadeJaReservada = reservaRepository.somarQuantidadeReservadaNoHorario(
                    itemDto.dispositivoId(),
                    dto.dataReserva(),
                    dto.horarioInicio(),
                    dto.horarioFim()
            );

            int quantidadeRestante = dispositivo.getQuantidadeDisponivel() - quantidadeJaReservada;

            if (itemDto.quantidadeReservada() > quantidadeRestante) {
                throw new ConflitoDeHorarioException(
                        "O dispositivo '" + dispositivo.getNome() + "' não tem quantidade suficiente disponível nesse horário. " +
                                "Disponível: " + quantidadeRestante + ", solicitado: " + itemDto.quantidadeReservada()
                );
            }
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuarioLogado);
        reserva.setSala(salaService.buscarEntidadePorId(dto.salaId()));
        reserva.setDataReserva(dto.dataReserva());
        reserva.setHorarioInicio(dto.horarioInicio());
        reserva.setHorarioFim(dto.horarioFim());
        reserva.setStatus(StatusReserva.PENDENTE);

        List<ItemReserva> itens = new ArrayList<>();
        for (ItemReservaRequestDTO itemDto : dto.itens()) {
            ItemReserva item = new ItemReserva();
            item.setReserva(reserva);
            item.setDispositivo(buscarDispositivo(itemDto.dispositivoId()));
            item.setQuantidadeReservada(itemDto.quantidadeReservada());
            itens.add(item);
        }
        reserva.setItens(itens);

        reservaRepository.save(reserva);
        return new ReservaResponseDTO(reserva);
    }

    public Page<ReservaResponseDTO> listarMinhasReservas(Long usuarioId, Pageable pageable) {
        return reservaRepository.findByUsuarioId(usuarioId, pageable)
                .map(ReservaResponseDTO::new);
    }

    public Page<ReservaResponseDTO> listarMinhasReservasPorPeriodo(Long usuarioId, String periodo, Pageable pageable) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicio;
        LocalDate dataFim;

        switch (periodo) {
            case "hoje" -> {
                dataInicio = hoje;
                dataFim = hoje;
            }
            case "semana" -> {
                dataInicio = hoje.with(DayOfWeek.MONDAY);
                dataFim = hoje.with(DayOfWeek.SUNDAY);
            }
            case "mes" -> {
                dataInicio = hoje.withDayOfMonth(1);
                dataFim = hoje.withDayOfMonth(hoje.lengthOfMonth());
            }
            case "semestre" -> {
                dataInicio = hoje.minusMonths(6);
                dataFim = hoje;
            }
            default -> {
                return listarMinhasReservas(usuarioId, pageable);
            }
        }

        return reservaRepository.findByUsuarioIdAndDataReservaBetween(usuarioId, dataInicio, dataFim, pageable)
                .map(ReservaResponseDTO::new);
    }

    public Page<ReservaResponseDTO> listarTodas(Pageable pageable) {
        return reservaRepository.findAll(pageable)
                .map(ReservaResponseDTO::new);
    }

    public Page<ReservaResponseDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return reservaRepository.findByUsuarioIdOrderByDataReservaDesc(usuarioId, pageable)
                .map(ReservaResponseDTO::new);
    }

    public Page<ReservaResponseDTO> listarPorStatus(StatusReserva status, Pageable pageable) {
        return reservaRepository.findByStatus(status, pageable)
                .map(ReservaResponseDTO::new);
    }

    public Page<ReservaResponseDTO> listarPorData(LocalDate data, Pageable pageable) {
        return reservaRepository.findByDataReserva(data, pageable)
                .map(ReservaResponseDTO::new);
    }

    public ReservaResponseDTO atualizarStatus(Long id, StatusReserva novoStatus) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNaoEncontradaException("Reserva não encontrada"));

        reserva.setStatus(novoStatus);
        reservaRepository.save(reserva);
        return new ReservaResponseDTO(reserva);
    }

    public ReservaResponseDTO atualizar(Long id, ReservaUpdateDTO dto) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNaoEncontradaException("Reserva não encontrada"));

        for (ItemReservaRequestDTO itemDto : dto.itens()) {
            Integer quantidadeJaReservada = reservaRepository.somarQuantidadeReservadaNoHorario(
                    itemDto.dispositivoId(),
                    dto.dataReserva(),
                    dto.horarioInicio(),
                    dto.horarioFim()
            );

            int quantidadePropria = reserva.getItens().stream()
                    .filter(i -> i.getDispositivo().getId().equals(itemDto.dispositivoId()))
                    .mapToInt(i -> i.getQuantidadeReservada())
                    .sum();

            int quantidadeRestante = buscarDispositivo(itemDto.dispositivoId()).getQuantidadeDisponivel()
                    - (quantidadeJaReservada - quantidadePropria);

            if (itemDto.quantidadeReservada() > quantidadeRestante) {
                Dispositivo dispositivo = buscarDispositivo(itemDto.dispositivoId());
                throw new ConflitoDeHorarioException(
                        "O dispositivo '" + dispositivo.getNome() + "' não tem quantidade suficiente disponível nesse horário."
                );
            }
        }

        reserva.setSala(salaService.buscarEntidadePorId(dto.salaId()));
        reserva.setDataReserva(dto.dataReserva());
        reserva.setHorarioInicio(dto.horarioInicio());
        reserva.setHorarioFim(dto.horarioFim());

        reserva.getItens().clear();
        for (ItemReservaRequestDTO itemDto : dto.itens()) {
            ItemReserva item = new ItemReserva();
            item.setReserva(reserva);
            item.setDispositivo(buscarDispositivo(itemDto.dispositivoId()));
            item.setQuantidadeReservada(itemDto.quantidadeReservada());
            reserva.getItens().add(item);
        }

        reservaRepository.save(reserva);
        return new ReservaResponseDTO(reserva);
    }

    public ReservaResponseDTO cancelarPropriaReserva(Long reservaId, Usuario usuarioLogado) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ReservaNaoEncontradaException("Reserva não encontrada"));

        if (!reserva.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new ConflitoDeHorarioException("Você só pode cancelar suas próprias reservas.");
        }

        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new ConflitoDeHorarioException("Esta reserva já está cancelada.");
        }

        LocalDateTime inicioReserva = LocalDateTime.of(reserva.getDataReserva(), reserva.getHorarioInicio());
        if (LocalDateTime.now().isAfter(inicioReserva)) {
            throw new ConflitoDeHorarioException("Não é possível cancelar uma reserva cujo horário já começou.");
        }

        reserva.setStatus(StatusReserva.CANCELADA);
        reservaRepository.save(reserva);
        return new ReservaResponseDTO(reserva);
    }

    public int expirarReservasPendentes() {
        List<Reserva> pendentes = reservaRepository.findByStatus(StatusReserva.PENDENTE);
        LocalDateTime agora = LocalDateTime.now();
        int canceladas = 0;

        for (Reserva reserva : pendentes) {
            LocalDateTime inicioReserva = LocalDateTime.of(reserva.getDataReserva(), reserva.getHorarioInicio());
            if (agora.isAfter(inicioReserva)) {
                reserva.setStatus(StatusReserva.CANCELADA);
                reservaRepository.save(reserva);
                canceladas++;
            }
        }
        return canceladas;
    }

    private Dispositivo buscarDispositivo(Long id) {
        return dispositivoRepository.findById(id)
                .orElseThrow(() -> new DispositivoNaoEncontradoException("Dispositivo não encontrado"));
    }
}