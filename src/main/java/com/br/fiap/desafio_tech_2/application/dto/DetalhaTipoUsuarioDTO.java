package com.br.fiap.desafio_tech_2.application.dto;

import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;

import java.util.UUID;

public record DetalhaTipoUsuarioDTO(
    Long id,
    String nomeTipo
) {

    public DetalhaTipoUsuarioDTO(TipoUsuario tipoUsuario){
        this(
                tipoUsuario.getId(),
                tipoUsuario.getNomeTipo()
        );
    }
}
