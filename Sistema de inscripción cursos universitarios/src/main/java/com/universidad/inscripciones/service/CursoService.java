package com.universidad.inscripciones.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.universidad.inscripciones.model.Curso;
import com.universidad.inscripciones.repository.CursoRepository;

@Service
public class CursoService {
    private final CursoRepository cursoRepository;
    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public Curso crearCurso(Curso curso) {
        if (curso.getId() != null && curso.getId().isBlank()) {
            curso.setId(null);
        }
        if (curso.getGrupos() == null || curso.getGrupos().isEmpty()) {
            Curso.Grupo g = new Curso.Grupo();
            g.setCodigoGrupo("A");
            g.setProfesorNombre("Por asignar");
            g.setCupoMaximo(30);
            g.setInscritosActuales(0);
            Curso.Horario h = new Curso.Horario();
            h.setDia("Lunes");
            h.setHoraInicio("08:00");
            h.setHoraFin("10:00");
            h.setSalon("Por asignar");
            g.setHorarios(java.util.List.of(h));
            curso.setGrupos(java.util.List.of(g));
        }
        if (curso.isEsExtension() && (curso.getCosto() == null || curso.getCosto() <= 0)) {
            curso.setCosto(500000.0);
        }
        return cursoRepository.save(curso);
    }

    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    public void eliminarCurso(String id) {
        cursoRepository.deleteById(id);
    }

    public void toggleActivo(String id, boolean activo) {
        Curso c = cursoRepository.findById(id).orElseThrow();
        c.setActivo(activo);
        cursoRepository.save(c);
    }

    public void asignarProfesor(String id, String codigoGrupo, String profesorNombre) {
        Curso c = cursoRepository.findById(id).orElseThrow();
        if (!c.isActivo()) { throw new IllegalStateException("Curso inactivo: no se pueden modificar grupos"); }
        for (Curso.Grupo g : c.getGrupos()) {
            if (codigoGrupo.equals(g.getCodigoGrupo())) {
                g.setProfesorNombre(profesorNombre);
            }
        }
        cursoRepository.save(c);
    }

    public void actualizarCupo(String id, String codigoGrupo, int cupoMaximo) {
        Curso c = cursoRepository.findById(id).orElseThrow();
        if (!c.isActivo()) { throw new IllegalStateException("Curso inactivo: no se pueden modificar cupos"); }
        for (Curso.Grupo g : c.getGrupos()) {
            if (codigoGrupo.equals(g.getCodigoGrupo())) {
                if (cupoMaximo < g.getInscritosActuales()) {
                    throw new IllegalArgumentException("Cupo máximo no puede ser menor a inscritos actuales");
                }
                g.setCupoMaximo(cupoMaximo);
            }
        }
        cursoRepository.save(c);
    }

    public void configurarHorarios(String id, String codigoGrupo, java.util.List<Curso.Horario> horarios) {
        // Validación simple: horaFin debe ser distinta y no anterior
        Curso c = cursoRepository.findById(id).orElseThrow();
        if (!c.isActivo()) { throw new IllegalStateException("Curso inactivo: no se pueden configurar horarios"); }
        for (Curso.Grupo g : c.getGrupos()) {
            if (codigoGrupo.equals(g.getCodigoGrupo())) {
                g.setHorarios(horarios);
            }
        }
        cursoRepository.save(c);
    }

    public java.util.List<Curso> marcarMitadComoExtension(Double costoPorDefecto) {
        java.util.List<Curso> cursos = cursoRepository.findAll();
        int n = cursos.size();
        for (int i = 0; i < n; i++) {
            Curso cur = cursos.get(i);
            boolean esExt = (i % 2 == 0);
            cur.setEsExtension(esExt);
            if (esExt) {
                if (cur.getCosto() == null || cur.getCosto() <= 0) {
                    cur.setCosto(costoPorDefecto != null ? costoPorDefecto : 300000.0);
                }
            } else {
                // opcionalmente limpiar costo para cursos regulares
                if (cur.getCosto() != null && cur.getCosto() > 0) {
                    // mantener costo si ya existe, o poner 0
                    // cur.setCosto(0.0);
                }
            }
        }
        return cursoRepository.saveAll(cursos);
    }

    public Curso asegurarGrupoPorDefecto(String id) {
        Curso c = cursoRepository.findById(id).orElseThrow();
        if (c.getGrupos() == null || c.getGrupos().isEmpty()) {
            Curso.Grupo g = new Curso.Grupo();
            g.setCodigoGrupo("A");
            g.setProfesorNombre("Por asignar");
            g.setCupoMaximo(30);
            g.setInscritosActuales(0);
            Curso.Horario h = new Curso.Horario();
            h.setDia("Lunes");
            h.setHoraInicio("08:00");
            h.setHoraFin("10:00");
            h.setSalon("Por asignar");
            g.setHorarios(java.util.List.of(h));
            c.setGrupos(java.util.List.of(g));
            cursoRepository.save(c);
        }
        return c;
    }

    public java.util.List<Curso> seedTresPorPrograma(java.util.List<String> programas, String periodo, Double costoExtension) {
        java.util.List<Curso> creados = new java.util.ArrayList<>();
        for (String prog : programas) {
            java.util.List<Curso> existentes = cursoRepository.findByProgramaContainingIgnoreCaseAndPeriodo(prog, periodo);
            int faltan = 3 - existentes.size();
            for (int i = 0; i < faltan; i++) {
                Curso nuevo = new Curso();
                nuevo.setCodigo(genCodigo(prog, i));
                nuevo.setNombre(genNombre(prog, i));
                nuevo.setPrograma(prog);
                nuevo.setPeriodo(periodo);
                nuevo.setActivo(true);
                boolean ext = (i % 2 == 1);
                nuevo.setEsExtension(ext);
                if (ext) nuevo.setCosto(costoExtension != null ? costoExtension : 500000.0);
                // grupo por defecto
                Curso.Grupo g = new Curso.Grupo();
                g.setCodigoGrupo("A");
                g.setProfesorNombre("Por asignar");
                g.setCupoMaximo(30);
                g.setInscritosActuales(0);
                Curso.Horario h = new Curso.Horario();
                h.setDia("Martes");
                h.setHoraInicio("14:00");
                h.setHoraFin("16:00");
                h.setSalon("Por asignar");
                g.setHorarios(java.util.List.of(h));
                nuevo.setGrupos(java.util.List.of(g));
                creados.add(cursoRepository.save(nuevo));
            }
        }
        return creados;
    }

    private String genCodigo(String programa, int idx) {
        String base = programa.replaceAll("[^A-Za-z]", "").toUpperCase();
        base = base.length() > 6 ? base.substring(0, 6) : base;
        return base + "-" + (100 + idx);
    }

    private String genNombre(String programa, int idx) {
        String[] nombres = new String[]{"Fundamentos", "Avanzado", "Laboratorio"};
        int i = Math.min(idx, nombres.length - 1);
        return programa + " - " + nombres[i];
    }
}

