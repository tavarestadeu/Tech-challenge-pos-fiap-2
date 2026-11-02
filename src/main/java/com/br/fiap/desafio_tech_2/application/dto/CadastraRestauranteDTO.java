package com.br.fiap.desafio_tech_2.application.dto;

import com.br.fiap.desafio_tech_2.domain.enums.HorarioFuncionamentoEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastraRestauranteDTO(

        @NotBlank(message = "O nome do restaurante é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotNull(message = "Os dados de endereço são obrigatórios")
        @Valid
        EnderecoDTO endereco,

        @NotNull(message = "O horário de funcionamento é obrigatório")
        HorarioFuncionamentoEnum horarioFuncionamento,

        @NotNull(message = "O ID do dono do restaurante é obrigatório")
        Long donoId
) {}