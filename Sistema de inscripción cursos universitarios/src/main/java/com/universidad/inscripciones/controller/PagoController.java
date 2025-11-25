package com.universidad.inscripciones.controller;

import java.util.Map;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.inscripciones.model.Pago;
import com.universidad.inscripciones.service.PagoService;
import com.universidad.inscripciones.repository.InscripcionRepository;
import com.universidad.inscripciones.repository.PagoRepository;

@RestController
@RequestMapping(path = "/api/pagos", produces = MediaType.APPLICATION_JSON_VALUE)
public class PagoController {
    private final PagoService pagoService;
    private final InscripcionRepository inscripcionRepository;
    private final PagoRepository pagoRepository;
    public PagoController(PagoService pagoService, InscripcionRepository inscripcionRepository, PagoRepository pagoRepository) {
        this.pagoService = pagoService;
        this.inscripcionRepository = inscripcionRepository;
        this.pagoRepository = pagoRepository;
    }

    @PostMapping("/pagar")
    public Pago pagar(@RequestBody Map<String, String> body) {
        return pagoService.registrarPago(body.get("inscripcionId"), body.getOrDefault("estado", "APROBADO"));
    }

    @PostMapping("/callback")
    public Pago callback(@RequestBody Map<String, String> body) {
        String inscripcionId = body.get("inscripcionId");
        String estado = body.get("estado");
        return pagoService.registrarPago(inscripcionId, estado);
    }

    @GetMapping
    public List<Pago> listar(@RequestParam String estudianteId) {
        var ins = inscripcionRepository.findByEstudianteId(estudianteId);
        var ids = ins.stream().map(i -> i.getId()).toList();
        return pagoRepository.findByInscripcionIdIn(ids);
    }
}

