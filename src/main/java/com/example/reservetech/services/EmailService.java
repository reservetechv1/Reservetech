package com.example.reservetech.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestClient restClient = RestClient.create();

    @Value("${email.api-key}")
    private String apiKey;

    @Value("${email.remetente}")
    private String remetente;

    @Value("${app.url}")
    private String appUrl;

    // Roda em segundo plano (@Async) para não deixar o cadastro do usuário
    // esperando o e-mail sair. Se o envio falhar, só registra no log -
    // nunca deve impedir o cadastro de ser concluído.
    //
    // Usa a API HTTP do Brevo (porta 443) em vez de SMTP (porta 587),
    // porque o Render bloqueia saída SMTP no plano gratuito desde set/2025.
    @Async
    public void enviarBoasVindas(String destinatario, String nome, String senhaTemporaria) {
        try {
            String htmlContent = """
                    <p>Olá, %s!</p>
                    <p>Você foi cadastrado no ReserveTech, o sistema de reservas de salas e equipamentos da Faculdade Santo Antônio.</p>
                    <p><b>Seus dados de acesso:</b><br>
                    E-mail: %s<br>
                    Senha temporária: %s</p>
                    <p>Por segurança, você vai precisar trocar essa senha assim que fizer o primeiro login.</p>
                    <p>Acesse o sistema em: <a href="%s">%s</a></p>
                    <p>Equipe ReserveTech</p>
                    """.formatted(nome, destinatario, senhaTemporaria, appUrl, appUrl);

            BrevoEmailRequest corpo = new BrevoEmailRequest(
                    new Remetente(remetente, "ReserveTech"),
                    List.of(new Destinatario(destinatario, nome)),
                    "Bem-vindo ao ReserveTech",
                    htmlContent
            );

            restClient.post()
                    .uri(BREVO_API_URL)
                    .header("api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corpo)
                    .retrieve()
                    .toBodilessEntity();

            logger.info("E-mail de boas-vindas enviado para {}", destinatario);
        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail de boas-vindas para {}: {}", destinatario, e.getMessage());
        }
    }

    private record Remetente(String email, String name) {}

    private record Destinatario(String email, String name) {}

    private record BrevoEmailRequest(Remetente sender, List<Destinatario> to, String subject, String htmlContent) {}
}