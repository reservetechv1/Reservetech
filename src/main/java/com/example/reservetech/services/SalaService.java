package com.example.reservetech.services;


import com.example.reservetech.DTO.SalaRequestDTO;
import com.example.reservetech.DTO.SalaResponseDTO;
import com.example.reservetech.exceptions.SalaJaExisteException;
import com.example.reservetech.exceptions.SalaNaoEncontradaException;
import com.example.reservetech.model.Sala;
import com.example.reservetech.repositories.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SalaService {

    @Autowired
    private SalaRepository salaRepository;

    public SalaResponseDTO criar(SalaRequestDTO dto) {
        if (salaRepository.existsByNomeIgnoreCaseAndAndarIgnoreCase(dto.nome(), dto.andar())) {
            throw new SalaJaExisteException(
                    "Já existe uma sala com esse nome nesse andar."
            );
        }

        Sala sala = new Sala();
        sala.setNome(dto.nome());
        sala.setAndar(dto.andar());

        salaRepository.save(sala);
        return new SalaResponseDTO(sala);
    }

    public Page<SalaResponseDTO> listarTodas(Pageable pageable) {
        return salaRepository.findAll(pageable)
                .map(SalaResponseDTO::new);
    }

    public SalaResponseDTO buscarPorId(Long id) {
        return new SalaResponseDTO(buscarEntidadePorId(id));
    }

    public SalaResponseDTO atualizar(Long id, SalaRequestDTO dto) {
        Sala sala = buscarEntidadePorId(id);

        if (salaRepository.existsByNomeIgnoreCaseAndAndarIgnoreCaseAndIdNot(dto.nome(), dto.andar(), id)) {
            throw new SalaJaExisteException(
                    "Já existe uma sala com esse nome nesse andar."
            );
        }

        sala.setNome(dto.nome());
        sala.setAndar(dto.andar());

        salaRepository.save(sala);
        return new SalaResponseDTO(sala);
    }

    public void deletar(Long id) {
        salaRepository.delete(buscarEntidadePorId(id));
    }

    Sala buscarEntidadePorId(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new SalaNaoEncontradaException("Sala não encontrada"));
    }
}