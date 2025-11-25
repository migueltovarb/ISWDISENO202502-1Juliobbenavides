package com.universidad.inscripciones.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import com.universidad.inscripciones.model.Usuario;
import com.universidad.inscripciones.service.AuthService;
import com.universidad.inscripciones.config.JwtUtil;
import com.universidad.inscripciones.controller.RegistrationRequest;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public Usuario register(@Valid @RequestBody RegistrationRequest req) {
        if (!req.password.equals(req.confirmPassword)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Las contraseñas no coinciden");
        }
        Usuario u = new Usuario();
        u.setNombre(req.nombre);
        u.setApellido(req.apellido);
        u.setIdentificacion(req.identificacion);
        u.setEmail(req.email);
        u.setPassword(req.password);
        u.setRol(req.rol);
        u.setPrograma(req.programa);
        u.setSemestre(req.semestre);
        return authService.registrar(u);
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Map<String, String> body) {
        Usuario u = authService.login(body.get("email"), body.get("identificacion"), body.get("password"));
        String token = jwtUtil.generate(u.getId(), 60L * 60L * 8L);
        ResponseCookie cookie = ResponseCookie.from("SESSION", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60L * 60L * 8L)
                .build();
        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).body(u);
    }

    @GetMapping("/me")
    public Usuario me(@CookieValue(name = "SESSION", required = false) String token) {
        if (token == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Sin sesión");
        String uid = jwtUtil.getSubjectIfValid(token);
        if (uid == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Sesión inválida");
        return authService.findById(uid);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("SESSION", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok().header("Set-Cookie", cookie.toString()).build();
    }
}

