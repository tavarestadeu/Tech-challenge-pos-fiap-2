package com.br.fiap.desafio_tech_2.application.mapper;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaEnderecoDTO;
import com.br.fiap.desafio_tech_2.application.dto.EnderecoDTO;
import com.br.fiap.desafio_tech_2.domain.entities.Endereco;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {



    public Endereco toEntity(EnderecoDTO dto) {
        if (dto == null) return null;

        return new Endereco(
                dto.logradouro(),
                dto.numero(),
                dto.cep(),
                dto.bairro(),
                dto.cidade(),
                dto.uf(),
                dto.complemento()
        );
    }

    public EnderecoDTO toDTO(Endereco entity) {
        if (entity == null) return null;

        return new EnderecoDTO(
                entity.getLogradouro(),
                entity.getNumero(),
                entity.getCep(),
                entity.getBairro(),
                entity.getCidade(),
                entity.getUf(),
                entity.getComplemento()
        );
    }

        public void updateEntityFromDTO(AtualizaEnderecoDTO dto, Endereco entity) {
        if (dto == null || entity == null) return;

        entity.atualizaDados(
                dto.logradouro(),
                dto.numero(),
                dto.cep(),
                dto.bairro(),
                dto.cidade(),
                dto.uf(),
                dto.complemento()
        );
    }
}