package com.ejemplo.vehiculos.repository;

import com.ejemplo.vehiculos.model.Vehiculo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

// Interfaz que nos da los métodos básicos de CRUD
// (findAll, save, deleteById, etc.)
@Repository
public interface VehiculoRepository extends MongoRepository<Vehiculo, String> {
    // No hace falta escribir nada más para el CRUD básico.
}
