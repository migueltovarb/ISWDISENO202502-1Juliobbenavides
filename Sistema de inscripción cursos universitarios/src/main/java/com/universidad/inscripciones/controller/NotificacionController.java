package com.universidad.inscripciones.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.inscripciones.model.Notificacion;
import com.universidad.inscripciones.repository.NotificacionRepository;

@RestController
@RequestMapping(path = "/api/notificaciones", produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificacionController {
    private final NotificacionRepository repo;
    public NotificacionController(NotificacionRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Notificacion> listar(@RequestParam String email) {
        return repo.findByDestinatario(email);
    }
}

