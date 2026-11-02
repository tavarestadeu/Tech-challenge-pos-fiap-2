package com.br.fiap.desafio_tech_2.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AtualizaUsuarioDTO(

        String nome,
        String email,
        Long tipoUsuarioId
) {}