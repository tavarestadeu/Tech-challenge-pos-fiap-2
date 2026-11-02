package com.br.fiap.desafio_tech_2.application.mapper;

import com.br.fiap.desafio_tech_2.application.dto.CadastraRestauranteDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaRestauranteDTO;
import com.br.fiap.desafio_tech_2.domain.entities.Restaurante;
import com.br.fiap.desafio_tech_2.domain.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestauranteMapper {

    private final UsuarioMapper usuarioMapper;
    private final EnderecoMapper enderecoMapper;

    public Restaurante toEntity(CadastraRestauranteDTO dados, Usuario dono) {
        if(dados == null || dono == null) return null;
        Restaurante entity = new Restaurante();
        entity.setNome(dados.nome());
        entity.setEndereco(enderecoMapper.toEntity(dados.endereco()));
        entity.setHorarioFuncionamento(dados.horarioFuncionamento());
        entity.setDonoDeRestaurante(dono);

        return entity;

    }

    public DetalhaRestauranteDTO toDTO(Restaurante entity){
        if(entity == null) return null;

        return new DetalhaRestauranteDTO(
                entity.getId(),
                entity.getNome(),
                enderecoMapper.toDTO(entity.getEndereco()),
                entity.getHorarioFuncionamento(),
                usuarioMapper.toDTO(entity.getDonoDeRestaurante())

        );
    }
}
