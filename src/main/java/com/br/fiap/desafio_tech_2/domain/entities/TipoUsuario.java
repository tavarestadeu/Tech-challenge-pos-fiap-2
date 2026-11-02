package com.br.fiap.desafio_tech_2.domain.entities;

import com.br.fiap.desafio_tech_2.application.dto.CadastraTipoUsuarioDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_tipo_usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class TipoUsuario implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, name = "nome_tipo", unique=true, length = 50)
    private String nomeTipo;

    public TipoUsuario(CadastraTipoUsuarioDTO dados) {
        this.nomeTipo = dados.nomeTipo();
    }

    @Override
    public String toString() {
        return nomeTipo != null? nomeTipo : "";
    }


    public void atualizaDados(@NotBlank String nomeTipo) {
        this.nomeTipo = nomeTipo;
    }
}
