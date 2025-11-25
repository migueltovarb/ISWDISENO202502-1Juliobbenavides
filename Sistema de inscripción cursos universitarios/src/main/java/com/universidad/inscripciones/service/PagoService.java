package com.universidad.inscripciones.service;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.universidad.inscripciones.model.Curso;
import com.universidad.inscripciones.model.Inscripcion;
import com.universidad.inscripciones.model.Pago;
import com.universidad.inscripciones.repository.CursoRepository;
import com.universidad.inscripciones.repository.InscripcionRepository;
import com.universidad.inscripciones.repository.PagoRepository;

@Service
public class PagoService {
    private final InscripcionRepository inscripcionRepository;
    private final PagoRepository pagoRepository;
    private final CursoRepository cursoRepository;
    private final MailService mailService;
    private final com.universidad.inscripciones.repository.UsuarioRepository usuarioRepository;
    private final org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    public PagoService(InscripcionRepository inscripcionRepository, PagoRepository pagoRepository, CursoRepository cursoRepository, MailService mailService, com.universidad.inscripciones.repository.UsuarioRepository usuarioRepository, org.springframework.data.mongodb.core.MongoTemplate mongoTemplate) {
        this.inscripcionRepository = inscripcionRepository;
        this.pagoRepository = pagoRepository;
        this.cursoRepository = cursoRepository;
        this.mailService = mailService;
        this.usuarioRepository = usuarioRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public Pago registrarPago(String inscripcionId, String estadoTransaccion) {
        Inscripcion ins = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));
        Curso curso = cursoRepository.findById(ins.getCursoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
        if (!"PENDIENTE_PAGO".equals(ins.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La inscripción no requiere pago");
        }
        Pago pago = new Pago();
        pago.setInscripcionId(inscripcionId);
        pago.setMonto(curso.getCosto() != null ? curso.getCosto() : 0.0);
        pago.setFechaPago(Instant.now());
        pago.setEstado(estadoTransaccion != null ? estadoTransaccion : "APROBADA");
        pago.setReferencia("UNIV-" + inscripcionId + "-" + System.currentTimeMillis());
        final Pago pagoGuardado = pagoRepository.save(pago);

        if ("APROBADA".equalsIgnoreCase(pagoGuardado.getEstado())) {
            ins.setEstado("ACTIVA");
            var userOpt = usuarioRepository.findById(ins.getEstudianteId());
            userOpt.ifPresent(u -> {
                String body = "Pago aprobado para curso " + curso.getNombre() + " (" + curso.getCodigo() + ")\nReferencia: " + pagoGuardado.getReferencia() + "\nMonto: $" + pagoGuardado.getMonto();
                mailService.enviar(u.getEmail(), "Confirmación de pago", body);
            });
        } else if ("RECHAZADA".equalsIgnoreCase(pagoGuardado.getEstado()) || "CANCELADA".equalsIgnoreCase(pagoGuardado.getEstado())) {
            ins.setEstado("NO_CONFIRMADA");
            // Liberar cupo
            var q = org.springframework.data.mongodb.core.query.Query.query(new org.springframework.data.mongodb.core.query.Criteria().andOperator(
                    org.springframework.data.mongodb.core.query.Criteria.where("_id").is(ins.getCursoId()),
                    org.springframework.data.mongodb.core.query.Criteria.where("grupos.codigoGrupo").is(ins.getCodigoGrupo())
            ));
            var udec = new org.springframework.data.mongodb.core.query.Update().inc("grupos.$.inscritosActuales", -1);
            mongoTemplate.updateFirst(q, udec, Curso.class);
        } else {
            ins.setEstado("PENDIENTE_PAGO");
        }
        inscripcionRepository.save(ins);
        return pagoGuardado;
    }
}

