package com.br.fiap.desafio_tech_2.application.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtualizaItemCardapioDTO(

        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String descricao,

        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal preco,

        Boolean apenasNoRestaurante,

        String caminhoFoto
) {}