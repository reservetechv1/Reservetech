package com.example.reservetech.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${email.remetente}")
    private String remetente;

    @Value("${app.url}")
    private String appUrl;

    // Roda em segundo plano (@Async) para não deixar o cadastro do usuário
    // esperando o e-mail sair. Se o envio falhar, só registra no log -
    // nunca deve impedir o cadastro de ser concluído.
    @Async
    public void enviarBoasVindas(String destinatario, String nome, String senhaTemporaria) {
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setFrom(remetente);
            mensagem.setTo(destinatario);
            mensagem.setSubject("Bem-vindo ao ReserveTech");
            mensagem.setText(
                    "Olá, " + nome + "!\n\n" +
                            "Você foi cadastrado no ReserveTech, o sistema de reservas de salas e equipamentos " +
                            "da Faculdade Santo Antônio.\n\n" +
                            "Seus dados de acesso:\n" +
                            "E-mail: " + destinatario + "\n" +
                            "Senha temporária: " + senhaTemporaria + "\n\n" +
                            "Por segurança, você vai precisar trocar essa senha assim que fizer o primeiro login.\n\n" +
                            "Acesse o sistema em: " + appUrl + "\n\n" +
                            "Equipe ReserveTech"
            );
            mailSender.send(mensagem);
            logger.info("E-mail de boas-vindas enviado para {}", destinatario);
        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail de boas-vindas para {}: {}", destinatario, e.getMessage());
        }
    }
}
