package com.universidad.inscripciones.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "inscripciones")
@org.springframework.data.mongodb.core.index.CompoundIndex(name = "uq_est_curso_grupo", def = "{'estudianteId':1,'cursoId':1,'codigoGrupo':1}", unique = true)
public class Inscripcion {
    @Id
    private String id;
    private String estudianteId;
    private String cursoId;
    private String codigoGrupo;
    private Instant fechaInscripcion;
    private String estado;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEstudianteId() { return estudianteId; }
    public void setEstudianteId(String estudianteId) { this.estudianteId = estudianteId; }
    public String getCursoId() { return cursoId; }
    public void setCursoId(String cursoId) { this.cursoId = cursoId; }
    public String getCodigoGrupo() { return codigoGrupo; }
    public void setCodigoGrupo(String codigoGrupo) { this.codigoGrupo = codigoGrupo; }
    public Instant getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(Instant fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

