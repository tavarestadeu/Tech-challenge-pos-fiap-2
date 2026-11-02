package com.br.fiap.desafio_tech_2.domain.entities;


import com.br.fiap.desafio_tech_2.application.dto.AtualizaUsuarioDTO;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of ="id")
public class Usuario implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50, unique = true)
    private String login;

    @Column(nullable = false, length = 255)
    private String senha;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_usuario_id", nullable = false)
    private TipoUsuario tipoUsuario;

    public void atualizaDados(AtualizaUsuarioDTO dados, TipoUsuario novoTipoUsuario) {
        if(dados.nome() != null && !dados.nome().isBlank()){
            this.nome = dados.nome();
        }

        if(dados.email() != null && !dados.email().isBlank()){
            this.email = dados.email();
        }

        if(novoTipoUsuario != null){
            this.tipoUsuario = novoTipoUsuario;
        }
    }
}
