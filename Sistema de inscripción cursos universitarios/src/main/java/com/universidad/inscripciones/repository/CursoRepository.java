package com.universidad.inscripciones.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.universidad.inscripciones.model.Curso;

public interface CursoRepository extends MongoRepository<Curso, String> {
    List<Curso> findByProgramaContainingIgnoreCaseAndPeriodo(String programa, String periodo);
}

