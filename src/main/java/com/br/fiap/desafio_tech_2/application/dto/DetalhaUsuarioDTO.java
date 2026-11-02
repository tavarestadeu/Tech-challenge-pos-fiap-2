package com.br.fiap.desafio_tech_2.application.dto;

public record DetalhaUsuarioDTO(
        Long id,
        String cpf,
        String nome,
        String email,
        String login,
        DetalhaTipoUsuarioDTO tipoUsuario
) {}