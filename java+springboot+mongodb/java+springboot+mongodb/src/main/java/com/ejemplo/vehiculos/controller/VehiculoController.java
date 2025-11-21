package com.ejemplo.vehiculos.controller;

import com.ejemplo.vehiculos.model.Vehiculo;
import com.ejemplo.vehiculos.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Controlador REST que expone el CRUD de vehiculos.
@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    @Autowired
    private VehiculoRepository vehiculoRepository;

    // LISTAR TODOS
    // GET http://localhost:8080/api/vehiculos
    @GetMapping
    public List<Vehiculo> listarVehiculos() {
        return vehiculoRepository.findAll();
    }

    // BUSCAR POR ID
    // GET http://localhost:8080/api/vehiculos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> obtenerPorId(@PathVariable String id) {
        Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(id);

        if (vehiculoOpt.isPresent()) {
            return ResponseEntity.ok(vehiculoOpt.get());
        } else {
            // Si no existe, devolvemos 404
            return ResponseEntity.notFound().build();
        }
    }

    // CREAR
    // POST http://localhost:8080/api/vehiculos
    @PostMapping
    public ResponseEntity<Vehiculo> crearVehiculo(@RequestBody Vehiculo vehiculo) {
        // Por si viene un id en el JSON lo limpiamos para que Mongo genere uno nuevo
        vehiculo.setId(null);
        Vehiculo guardado = vehiculoRepository.save(vehiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // ACTUALIZAR
    // PUT http://localhost:8080/api/vehiculos/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Vehiculo> actualizarVehiculo(@PathVariable String id,
                                                       @RequestBody Vehiculo datosNuevos) {

        Optional<Vehiculo> vehiculoOpt = vehiculoRepository.findById(id);

        if (vehiculoOpt.isPresent()) {
            Vehiculo existente = vehiculoOpt.get();

            // Actualizamos campo por campo
            existente.setMarca(datosNuevos.getMarca());
            existente.setModelo(datosNuevos.getModelo());
            existente.setAnio(datosNuevos.getAnio());
            existente.setPrecio(datosNuevos.getPrecio());

            Vehiculo actualizado = vehiculoRepository.save(existente);
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ELIMINAR
    // DELETE http://localhost:8080/api/vehiculos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable String id) {
        if (vehiculoRepository.existsById(id)) {
            vehiculoRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.notFound().build();  // 404
        }
    }
}
