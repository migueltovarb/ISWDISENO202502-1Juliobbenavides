package com.universidad.inscripciones.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "cursos")
public class Curso {
    @Id
    private String id;
    @Indexed
    private String codigo;
    @Indexed
    private String nombre;
    @Indexed
    private String programa;
    @Indexed
    private String periodo;
    private String descripcion;
    private Integer creditos;
    private boolean activo;
    private boolean esExtension;
    private Double costo;
    private java.time.Instant inscripcionInicio;
    private java.time.Instant inscripcionFin;
    private List<Grupo> grupos;

    public static class Horario {
        private String dia;
        private String horaInicio;
        private String horaFin;
        private String salon;
        public String getDia() { return dia; }
        public void setDia(String dia) { this.dia = dia; }
        public String getHoraInicio() { return horaInicio; }
        public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }
        public String getHoraFin() { return horaFin; }
        public void setHoraFin(String horaFin) { this.horaFin = horaFin; }
        public String getSalon() { return salon; }
        public void setSalon(String salon) { this.salon = salon; }
    }

    public static class Grupo {
        private String codigoGrupo;
        private String profesorNombre;
        private int cupoMaximo;
        private int inscritosActuales;
        private List<Horario> horarios;
        public String getCodigoGrupo() { return codigoGrupo; }
        public void setCodigoGrupo(String codigoGrupo) { this.codigoGrupo = codigoGrupo; }
        public String getProfesorNombre() { return profesorNombre; }
        public void setProfesorNombre(String profesorNombre) { this.profesorNombre = profesorNombre; }
        public int getCupoMaximo() { return cupoMaximo; }
        public void setCupoMaximo(int cupoMaximo) { this.cupoMaximo = cupoMaximo; }
        public int getInscritosActuales() { return inscritosActuales; }
        public void setInscritosActuales(int inscritosActuales) { this.inscritosActuales = inscritosActuales; }
        public List<Horario> getHorarios() { return horarios; }
        public void setHorarios(List<Horario> horarios) { this.horarios = horarios; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPrograma() { return programa; }
    public void setPrograma(String programa) { this.programa = programa; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getCreditos() { return creditos; }
    public void setCreditos(Integer creditos) { this.creditos = creditos; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public boolean isEsExtension() { return esExtension; }
    public void setEsExtension(boolean esExtension) { this.esExtension = esExtension; }
    public Double getCosto() { return costo; }
    public void setCosto(Double costo) { this.costo = costo; }
    public java.time.Instant getInscripcionInicio() { return inscripcionInicio; }
    public void setInscripcionInicio(java.time.Instant inscripcionInicio) { this.inscripcionInicio = inscripcionInicio; }
    public java.time.Instant getInscripcionFin() { return inscripcionFin; }
    public void setInscripcionFin(java.time.Instant inscripcionFin) { this.inscripcionFin = inscripcionFin; }
    public List<Grupo> getGrupos() { return grupos; }
    public void setGrupos(List<Grupo> grupos) { this.grupos = grupos; }
}

