package com.br.fiap.desafio_tech_2.application.mapper;

import com.br.fiap.desafio_tech_2.application.dto.CadastraUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaUsuarioDTO;
import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;
import com.br.fiap.desafio_tech_2.domain.entities.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioMapper {

    private final TipoUsuarioMapper tipoUsuarioMapper;

    public Usuario toEntity(CadastraUsuarioDTO dto, TipoUsuario tipoUsuario){
        if(dto == null) return null;
        var entity = new Usuario();
        entity.setCpf(dto.cpf());
        entity.setNome(dto.nome());
        entity.setEmail(dto.email());
        entity.setLogin(dto.login());
        entity.setSenha(dto.senha());
        entity.setTipoUsuario(tipoUsuario);

        return entity;
    }

    public DetalhaUsuarioDTO toDTO(Usuario entity){
        if(entity == null) return null;
        return new DetalhaUsuarioDTO(
                entity.getId(),
                entity.getCpf(),
                entity.getNome(),
                entity.getEmail(),
                entity.getLogin(),
                tipoUsuarioMapper.toDTO(entity.getTipoUsuario())

        );
    }
}
