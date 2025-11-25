package com.universidad.inscripciones.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.inscripciones.model.Inscripcion;
import com.universidad.inscripciones.repository.InscripcionRepository;
import com.universidad.inscripciones.service.InscripcionService;

@RestController
@RequestMapping(path = "/api/inscripciones", produces = MediaType.APPLICATION_JSON_VALUE)
public class InscripcionController {
    private final InscripcionService inscripcionService;
    private final InscripcionRepository inscripcionRepository;
    public InscripcionController(InscripcionService inscripcionService, InscripcionRepository inscripcionRepository) {
        this.inscripcionService = inscripcionService;
        this.inscripcionRepository = inscripcionRepository;
    }

    @PostMapping
    public Inscripcion inscribir(@RequestBody Map<String, String> body) {
        return inscripcionService.inscribir(body.get("usuarioId"), body.get("cursoId"), body.get("codigoGrupo"));
    }

    @GetMapping
    public List<Inscripcion> misInscripciones(@RequestParam String estudianteId) {
        return inscripcionRepository.findByEstudianteId(estudianteId);
    }

    @PostMapping("/cancelar")
    public Inscripcion cancelar(@RequestBody Map<String, String> body) {
        return inscripcionService.cancelar(body.get("inscripcionId"));
    }

    @GetMapping("/count")
    public java.util.Map<String, Long> count() {
        return java.util.Map.of("total", inscripcionRepository.count());
    }
}

