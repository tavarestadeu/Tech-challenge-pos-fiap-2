package com.br.fiap.desafio_tech_2.application.dto;


import jakarta.validation.constraints.NotBlank;

public record CadastraTipoUsuarioDTO(

        @NotBlank
        String nomeTipo
){

}
