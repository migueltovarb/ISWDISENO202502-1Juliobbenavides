package com.universidad.inscripciones.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.universidad.inscripciones.model.Notificacion;

public interface NotificacionRepository extends MongoRepository<Notificacion, String> {
    java.util.List<Notificacion> findByDestinatario(String destinatario);
}

