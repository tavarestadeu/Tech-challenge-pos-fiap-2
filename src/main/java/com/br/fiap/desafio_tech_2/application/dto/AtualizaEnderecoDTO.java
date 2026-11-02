package com.br.fiap.desafio_tech_2.application.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AtualizaEnderecoDTO(

        @Size(max = 150, message = "O logradouro deve ter no máximo 150 caracteres")
        String logradouro,

        @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
        String numero,

        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 12345-678 ou 12345678")
        String cep,

        @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
        String bairro,

        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
        String cidade,

        @Size(min = 2, max = 2, message = "O UF deve conter exatamente 2 letras")
        String uf,

        @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
        String complemento
) {}