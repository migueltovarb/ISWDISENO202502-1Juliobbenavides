package com.universidad.inscripciones.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.universidad.inscripciones.model.Curso;
import com.universidad.inscripciones.repository.CursoRepository;
import com.universidad.inscripciones.service.CursoService;

@RestController
@RequestMapping(path = "/api/cursos", produces = MediaType.APPLICATION_JSON_VALUE)
public class CursoController {
    private final CursoService cursoService;
    private final CursoRepository cursoRepository;
    private final MongoTemplate mongoTemplate;
    public CursoController(CursoService cursoService, CursoRepository cursoRepository, MongoTemplate mongoTemplate) {
        this.cursoService = cursoService;
        this.cursoRepository = cursoRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping
    public List<Curso> listar(@RequestParam(required = false) String programa,
                              @RequestParam(required = false) String periodo,
                              @RequestParam(required = false) String q,
                              @RequestParam(required = false) String tipo) {
        if (programa == null && periodo == null && q == null && tipo == null) {
            return cursoService.listarCursos();
        }
        Query query = new Query();
        if (programa != null && !programa.isBlank()) {
            query.addCriteria(Criteria.where("programa").regex(programa, "i"));
        }
        if (periodo != null && !periodo.isBlank()) {
            query.addCriteria(Criteria.where("periodo").is(periodo));
        }
        if (q != null && !q.isBlank()) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("codigo").regex(q, "i"),
                    Criteria.where("nombre").regex(q, "i")
            ));
        }
        if (tipo != null) {
            if ("extension".equalsIgnoreCase(tipo)) query.addCriteria(Criteria.where("esExtension").is(true));
            if ("regular".equalsIgnoreCase(tipo)) query.addCriteria(Criteria.where("esExtension").is(false));
        }
        return mongoTemplate.find(query, Curso.class);
    }

    @PostMapping
    public Curso crear(@RequestBody Curso curso) {
        return cursoService.crearCurso(curso);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String id) {
        cursoService.eliminarCurso(id);
    }

    @PutMapping("/{id}")
    public Curso actualizar(@PathVariable String id, @RequestBody Curso curso) {
        curso.setId(id);
        return cursoService.crearCurso(curso);
    }

    @PatchMapping("/{id}/activar")
    public void activar(@PathVariable String id) {
        cursoService.toggleActivo(id, true);
    }

    @PatchMapping("/{id}/desactivar")
    public void desactivar(@PathVariable String id) {
        cursoService.toggleActivo(id, false);
    }

    @PatchMapping("/{id}/grupos/{codigoGrupo}/profesor")
    public void asignarProfesor(@PathVariable String id, @PathVariable String codigoGrupo, @RequestBody java.util.Map<String, String> body) {
        cursoService.asignarProfesor(id, codigoGrupo, body.get("profesorNombre"));
    }

    @PatchMapping("/{id}/grupos/{codigoGrupo}/cupo")
    public void actualizarCupo(@PathVariable String id, @PathVariable String codigoGrupo, @RequestBody java.util.Map<String, Integer> body) {
        cursoService.actualizarCupo(id, codigoGrupo, body.get("cupoMaximo"));
    }

    @PostMapping("/{id}/grupos/{codigoGrupo}/horarios")
    public void configurarHorarios(@PathVariable String id, @PathVariable String codigoGrupo, @RequestBody java.util.List<com.universidad.inscripciones.model.Curso.Horario> horarios) {
        cursoService.configurarHorarios(id, codigoGrupo, horarios);
    }

    @PatchMapping("/extension/half")
    public java.util.List<Curso> marcarMitadExtension(@RequestParam(required = false) Double costo) {
        return cursoService.marcarMitadComoExtension(costo);
    }

    @PatchMapping("/{id}/ensure-group")
    public com.universidad.inscripciones.model.Curso asegurarGrupo(@PathVariable String id) {
        return cursoService.asegurarGrupoPorDefecto(id);
    }

    @PostMapping("/seed")
    public java.util.List<Curso> seed(@RequestParam(required = false) String programas,
                                      @RequestParam(required = false) String periodo,
                                      @RequestParam(required = false) Double costo) {
        java.util.List<String> list = new java.util.ArrayList<>();
        if (programas == null || programas.isBlank()) {
            list = java.util.List.of("Ingeniería de Software", "Ingeniería de Sistemas", "Ingeniería Electrónica");
        } else {
            for (String p : programas.split(",")) list.add(p.trim());
        }
        String per = (periodo == null || periodo.isBlank()) ? "2025-1" : periodo;
        return cursoService.seedTresPorPrograma(list, per, costo);
    }
}

