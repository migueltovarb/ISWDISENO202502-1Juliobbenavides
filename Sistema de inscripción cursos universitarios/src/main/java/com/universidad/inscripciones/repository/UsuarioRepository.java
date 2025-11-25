package com.universidad.inscripciones.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.universidad.inscripciones.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndIdentificacion(String email, String identificacion);
}

