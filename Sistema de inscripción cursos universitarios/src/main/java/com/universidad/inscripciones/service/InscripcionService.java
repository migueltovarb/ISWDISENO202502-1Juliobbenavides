package com.universidad.inscripciones.service;

import java.time.Instant;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.universidad.inscripciones.model.Curso;
import com.universidad.inscripciones.model.Inscripcion;
import com.universidad.inscripciones.repository.CursoRepository;
import com.universidad.inscripciones.repository.InscripcionRepository;

@Service
public class InscripcionService {
    private final MongoTemplate mongoTemplate;
    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final com.universidad.inscripciones.repository.UsuarioRepository usuarioRepository;
    private final MailService mailService;

    public InscripcionService(MongoTemplate mongoTemplate, CursoRepository cursoRepository, InscripcionRepository inscripcionRepository, com.universidad.inscripciones.repository.UsuarioRepository usuarioRepository, MailService mailService) {
        this.mongoTemplate = mongoTemplate;
        this.cursoRepository = cursoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.usuarioRepository = usuarioRepository;
        this.mailService = mailService;
    }

    public Inscripcion inscribir(String usuarioId, String cursoId, String grupoId) {
        List<Inscripcion> existentes = inscripcionRepository.findByEstudianteId(usuarioId);
        for (Inscripcion i : existentes) {
            if (cursoId.equals(i.getCursoId()) && grupoId.equals(i.getCodigoGrupo())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe inscripción al mismo grupo");
            }
        }

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
        if (!curso.isActivo()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Periodo de inscripción finalizado");
        }
        var now = java.time.Instant.now();
        if (curso.getInscripcionInicio() != null && curso.getInscripcionFin() != null) {
            if (now.isBefore(curso.getInscripcionInicio()) || now.isAfter(curso.getInscripcionFin())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Fuera de ventana de inscripción");
            }
        }

        int cupoMaximo = curso.getGrupos().stream()
                .filter(g -> grupoId.equals(g.getCodigoGrupo()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grupo no encontrado"))
                .getCupoMaximo();

        Query q = Query.query(new Criteria().andOperator(
                Criteria.where("_id").is(cursoId),
                Criteria.where("grupos").elemMatch(Criteria.where("codigoGrupo").is(grupoId).and("inscritosActuales").lt(cupoMaximo))
        ));
        Update upd = new Update().inc("grupos.$.inscritosActuales", 1);
        var res = mongoTemplate.updateFirst(q, upd, Curso.class);
        if (res.getModifiedCount() == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cupo lleno");
        }

        Inscripcion ins = new Inscripcion();
        ins.setEstudianteId(usuarioId);
        ins.setCursoId(cursoId);
        ins.setCodigoGrupo(grupoId);
        ins.setFechaInscripcion(Instant.now());
        ins.setEstado(curso.isEsExtension() ? "PENDIENTE_PAGO" : "ACTIVA");
        try {
            Inscripcion saved = inscripcionRepository.save(ins);
            var userOpt = usuarioRepository.findById(usuarioId);
            userOpt.ifPresent(usr -> {
                String subject;
                String body;
                if ("PENDIENTE_PAGO".equals(saved.getEstado())) {
                    subject = "Pago pendiente de inscripción";
                    String montoTxt = curso.getCosto() != null ? ("$" + String.format("%,.0f", curso.getCosto())) : "(monto por definir)";
                    body = "Has inscrito el curso de extensión: " + curso.getNombre() + " (" + curso.getCodigo() + ")" +
                           "\nGrupo: " + grupoId +
                           "\nEstado: PENDIENTE_PAGO" +
                           "\nDebes realizar el pago. Monto: " + montoTxt;
                } else {
                    subject = "Inscripción confirmada";
                    body = "Te has inscrito en: " + curso.getNombre() + " (" + curso.getCodigo() + ")" +
                           "\nGrupo: " + grupoId +
                           "\nEstado: " + saved.getEstado();
                }
                mailService.enviar(usr.getEmail(), subject, body);
            });
            return saved;
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inscripción duplicada");
        }
    }

    public Inscripcion cancelar(String inscripcionId) {
        Inscripcion ins = inscripcionRepository.findById(inscripcionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));
        if (!"ACTIVA".equals(ins.getEstado()) && !"PENDIENTE_PAGO".equals(ins.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La inscripción no puede cancelarse");
        }
        ins.setEstado("CANCELADA");
        inscripcionRepository.save(ins);

        Query q = Query.query(new Criteria().andOperator(
                Criteria.where("_id").is(ins.getCursoId()),
                Criteria.where("grupos.codigoGrupo").is(ins.getCodigoGrupo())
        ));
        Update udec = new Update().inc("grupos.$.inscritosActuales", -1);
        mongoTemplate.updateFirst(q, udec, Curso.class);
        return ins;
    }
}

