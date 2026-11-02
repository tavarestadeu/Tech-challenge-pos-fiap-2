package com.br.fiap.desafio_tech_2.application.dto;

import com.br.fiap.desafio_tech_2.domain.enums.HorarioFuncionamentoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record AtualizaRestauranteDTO(

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Valid
        AtualizaEnderecoDTO endereco,

        HorarioFuncionamentoEnum horarioFuncionamento,

        Long donoId
) {}