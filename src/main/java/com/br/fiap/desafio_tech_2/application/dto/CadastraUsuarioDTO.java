package com.br.fiap.desafio_tech_2.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CPF;

public record CadastraUsuarioDTO(

        @NotBlank(message = "O CPF é obrigatório")
        String cpf,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        @Size(max = 50, message = "O e-mail deve ter no máximo 50 caracteres")
        String email,

        @NotBlank(message = "O login é obrigatório")
        @Size(max = 50, message = "O login deve ter no máximo 50 caracteres")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, max = 255, message = "A senha deve ter entre 6 e 255 caracteres")
        String senha,

        @NotNull(message = "O ID do tipo de usuário é obrigatório")
        Long tipoUsuarioId
) {}