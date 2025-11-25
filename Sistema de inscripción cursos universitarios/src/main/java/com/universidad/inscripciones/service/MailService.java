package com.universidad.inscripciones.service;

import java.time.Instant;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.universidad.inscripciones.model.Notificacion;
import com.universidad.inscripciones.repository.NotificacionRepository;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final NotificacionRepository notificacionRepository;
    public MailService(JavaMailSender mailSender, NotificacionRepository notificacionRepository) {
        this.mailSender = mailSender;
        this.notificacionRepository = notificacionRepository;
    }

    public void enviar(String to, String subject, String body) {
        Notificacion n = new Notificacion();
        n.setDestinatario(to);
        n.setAsunto(subject);
        n.setCuerpo(body);
        n.setFecha(Instant.now());
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            n.setEstado("ENVIADO");
        } catch (MailException e) {
            n.setEstado("FALLIDO");
        } finally {
            notificacionRepository.save(n);
        }
    }
}

