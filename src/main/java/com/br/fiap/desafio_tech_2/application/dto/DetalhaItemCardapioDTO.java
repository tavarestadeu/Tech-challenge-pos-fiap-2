package com.br.fiap.desafio_tech_2.application.dto;

import java.math.BigDecimal;

public record DetalhaItemCardapioDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        Boolean apenasNoRestaurante,
        String caminhoFoto,
        Long restauranteId
) {}