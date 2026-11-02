package com.br.fiap.desafio_tech_2.domain.entities;

import com.br.fiap.desafio_tech_2.domain.enums.HorarioFuncionamentoEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_restaurante")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Restaurante implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Embedded
    @Column(nullable = false)
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    @Column(name = "horario_funcionamento", nullable = false, length = 30)
    private HorarioFuncionamentoEnum horarioFuncionamento;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dono_id", nullable = false)
    private Usuario donoDeRestaurante;


    public void atualizaDados( String nome, HorarioFuncionamentoEnum horario) {
        if(nome != null && !nome.isBlank()){
            this.nome = nome;
        }


    }

    public void atualizaDono(Usuario novoDono){
        if(novoDono != null){
            this.donoDeRestaurante = novoDono;
        }
    }
}
