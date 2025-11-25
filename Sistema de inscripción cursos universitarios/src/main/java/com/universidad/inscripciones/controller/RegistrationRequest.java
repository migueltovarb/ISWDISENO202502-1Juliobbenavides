package com.universidad.inscripciones.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistrationRequest {
    @NotBlank public String nombre;
    @NotBlank public String apellido;
    @NotBlank public String identificacion;
    @NotBlank @Email public String email;
    @NotBlank public String password;
    @NotBlank public String confirmPassword;
    @NotBlank public String rol;
    @NotBlank public String programa;
    @NotNull public Integer semestre;
}

