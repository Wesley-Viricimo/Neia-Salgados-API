package org.neiasalgados.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailService {

    private final SesClient sesClient;
    private final String senderEmail;

    public EmailService(SesClient sesClient, @Value("${aws.sender-email}") String senderEmail) {
        this.sesClient = sesClient;
        this.senderEmail = senderEmail;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(Destination.builder().toAddresses(to).build())
                    .message(Message.builder()
                            .body(Body.builder()
                                    .html(Content.builder().data(body).build())
                                    .build())
                            .subject(Content.builder().data(subject).build())
                            .build())
                    .source(senderEmail)
                    .build();

            sesClient.sendEmail(request);
        } catch (SesException e) {
            throw new RuntimeException("Erro ao enviar email: " + e.getMessage());
        }
    }
}