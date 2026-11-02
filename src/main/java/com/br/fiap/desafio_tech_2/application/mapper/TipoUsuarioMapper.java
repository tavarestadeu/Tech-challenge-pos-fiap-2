package com.br.fiap.desafio_tech_2.application.mapper;

import ch.qos.logback.classic.util.LogbackMDCAdapterSimple;
import com.br.fiap.desafio_tech_2.application.dto.CadastraTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;
import org.springframework.stereotype.Component;

@Component
public class TipoUsuarioMapper {

    public TipoUsuario toEntity(CadastraTipoUsuarioDTO dto){
        if(dto == null) return  null;
        TipoUsuario entity = new TipoUsuario();
        entity.setNomeTipo(dto.nomeTipo());
        return entity;
    }

    public DetalhaTipoUsuarioDTO toDTO(TipoUsuario entity){
        if(entity == null) return null;
        return new DetalhaTipoUsuarioDTO(entity.getId(), entity.getNomeTipo());
    }
}
