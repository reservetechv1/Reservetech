package com.example.reservetech.services;

import com.example.reservetech.DTO.BackupResumoDTO;
import com.example.reservetech.DTO.DispositivoResponseDTO;
import com.example.reservetech.DTO.ReservaResponseDTO;
import com.example.reservetech.DTO.SalaResponseDTO;
import com.example.reservetech.DTO.UsuarioResponseDTO;
import com.example.reservetech.exceptions.BackupNaoEncontradoException;
import com.example.reservetech.model.Backup;
import com.example.reservetech.repositories.BackupRepository;
import com.example.reservetech.repositories.DispositivoRepository;
import com.example.reservetech.repositories.ReservaRepository;
import com.example.reservetech.repositories.SalaRepository;
import com.example.reservetech.repositories.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BackupService {

    // Quantos backups ficam guardados; os mais antigos são apagados sozinhos
    private static final int MAXIMO_BACKUPS_MANTIDOS = 8;

    @Autowired
    private BackupRepository backupRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private DispositivoRepository dispositivoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public List<BackupResumoDTO> listar() {
        return backupRepository.findAllByOrderByDataCriacaoDesc().stream()
                .map(BackupResumoDTO::new)
                .toList();
    }

    public Backup buscarPorId(Long id) {
        return backupRepository.findById(id)
                .orElseThrow(() -> new BackupNaoEncontradoException("Backup não encontrado."));
    }

    @Transactional
    public BackupResumoDTO gerarBackup() {
        Map<String, Object> dados = new LinkedHashMap<>();
        dados.put("geradoEm", LocalDateTime.now().toString());
        dados.put("usuarios", usuarioRepository.findAll().stream().map(UsuarioResponseDTO::new).toList());
        dados.put("salas", salaRepository.findAll().stream().map(SalaResponseDTO::new).toList());
        dados.put("dispositivos", dispositivoRepository.findAll().stream().map(DispositivoResponseDTO::new).toList());
        dados.put("reservas", reservaRepository.findAll().stream().map(ReservaResponseDTO::new).toList());

        String json;
        try {
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dados);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro ao gerar backup: " + e.getMessage(), e);
        }

        Backup backup = new Backup();
        backup.setDataCriacao(LocalDateTime.now());
        backup.setConteudoJson(json);
        backup.setTamanhoBytes(json.getBytes(StandardCharsets.UTF_8).length);
        backupRepository.save(backup);

        limparBackupsAntigos();

        return new BackupResumoDTO(backup);
    }

    private void limparBackupsAntigos() {
        List<Backup> todos = backupRepository.findAllByOrderByDataCriacaoDesc();
        if (todos.size() > MAXIMO_BACKUPS_MANTIDOS) {
            backupRepository.deleteAll(todos.subList(MAXIMO_BACKUPS_MANTIDOS, todos.size()));
        }
    }

    // Roda sozinho toda segunda-feira às 3h. Só dispara de fato se o servidor
    // estiver "acordado" nesse horário - por isso também existe o endpoint
    // POST /backups/gerar-automatico, feito para ser acionado por um cron
    // externo gratuito (ex: cron-job.org), garantindo que rode mesmo se o
    // Render tiver colocado o servidor pra dormir.
    @Scheduled(cron = "0 0 3 * * MON")
    public void backupAgendado() {
        gerarBackup();
    }
}
