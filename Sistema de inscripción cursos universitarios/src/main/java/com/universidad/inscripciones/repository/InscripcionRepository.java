package com.universidad.inscripciones.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.universidad.inscripciones.model.Inscripcion;

public interface InscripcionRepository extends MongoRepository<Inscripcion, String> {
    List<Inscripcion> findByEstudianteId(String estudianteId);
}

