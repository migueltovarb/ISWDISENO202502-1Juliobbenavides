package com.universidad.inscripciones.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.universidad.inscripciones.model.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.universidad.inscripciones.repository.UsuarioRepository;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario registrar(Usuario usuario) {
        if (usuario.getPrograma() == null || usuario.getPrograma().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Programa requerido");
        }
        if (usuario.getSemestre() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Semestre requerido");
        }
        Optional<Usuario> existente = usuarioRepository.findByEmail(usuario.getEmail());
        if (existente.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email ya registrado");
        }
        if (usuarioRepository.findByEmailAndIdentificacion(usuario.getEmail(), usuario.getIdentificacion()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Identificación ya registrada");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Usuario login(String email, String identificacion, String password) {
        Usuario u = usuarioRepository.findByEmailAndIdentificacion(email, identificacion)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        if (!passwordEncoder.matches(password, u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        return u;
    }

    public Usuario findById(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
    }
}

