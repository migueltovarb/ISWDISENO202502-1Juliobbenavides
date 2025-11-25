package com.universidad.inscripciones.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.universidad.inscripciones.model.Pago;

public interface PagoRepository extends MongoRepository<Pago, String> {
    java.util.List<Pago> findByInscripcionIdIn(java.util.List<String> inscripcionIds);
}

