package com.br.fiap.desafio_tech_2.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EnderecoDTO(

        @NotBlank(message = "O logradouro é obrigatório")
        @Size(max = 150, message = "O logradouro deve ter no máximo 150 caracteres")
        String logradouro,

        @NotBlank(message = "O número é obrigatório")
        @Size(max = 20, message = "O número deve ter no máximo 20 caracteres")
        String numero,

        @NotBlank(message = "O CEP é obrigatório")
        @Pattern(regexp = "\\d{5}-?\\d{3}", message = "O CEP deve estar no formato 12345-678 ou 12345678")
        String cep,

        @NotBlank(message = "O bairro é obrigatório")
        @Size(max = 100, message = "O bairro deve ter no máximo 100 caracteres")
        String bairro,

        @NotBlank(message = "A cidade é obrigatória")
        @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
        String cidade,

        @NotBlank(message = "O UF é obrigatório")
        @Size(min = 2, max = 2, message = "O UF deve conter exatamente 2 letras")
        String uf,

        @Size(max = 100, message = "O complemento deve ter no máximo 100 caracteres")
        String complemento




) {}