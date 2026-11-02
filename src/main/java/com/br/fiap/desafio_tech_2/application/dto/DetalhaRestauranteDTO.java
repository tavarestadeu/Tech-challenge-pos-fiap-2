package com.br.fiap.desafio_tech_2.application.dto;

import com.br.fiap.desafio_tech_2.domain.enums.HorarioFuncionamentoEnum;

public record DetalhaRestauranteDTO(
        Long id,
        String nome,
        EnderecoDTO endereco,
        HorarioFuncionamentoEnum horarioFuncionamento,
        DetalhaUsuarioDTO donoDeRestaurante
) {}