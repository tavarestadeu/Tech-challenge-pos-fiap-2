package com.br.fiap.desafio_tech_2.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CadastraItemCardapioDTO(

        @NotBlank(message = "O nome do item é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal preco,

        @NotNull(message = "Informe se o item é servido apenas no restaurante")
        Boolean apenasNoRestaurante,

        String caminhoFoto,

        @NotNull(message = "O ID do restaurante é obrigatório")
        Long restauranteId
) {}