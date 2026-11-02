package com.br.fiap.desafio_tech_2.application.mapper;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.domain.entities.ItemCardapio;
import com.br.fiap.desafio_tech_2.domain.entities.Restaurante;
import org.springframework.stereotype.Component;

@Component
public class ItemCardapioMapper {

    public ItemCardapio toEntity(CadastraItemCardapioDTO dto, Restaurante restaurante) {
        if (dto == null) return null;

        ItemCardapio item = new ItemCardapio();
        item.setNome(dto.nome());
        item.setDescricao(dto.descricao());
        item.setPreco(dto.preco());
        item.setApenasNoRestaurante(dto.apenasNoRestaurante());
        item.setCaminhoFoto(dto.caminhoFoto());
        item.setRestaurante(restaurante);

        return item;
    }

    public DetalhaItemCardapioDTO toDTO(ItemCardapio entity) {
        if (entity == null) return null;

        Long restauranteId = entity.getRestaurante() != null ? entity.getRestaurante().getId() : null;

        return new DetalhaItemCardapioDTO(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getPreco(),
                entity.getApenasNoRestaurante(),
                entity.getCaminhoFoto(),
                restauranteId
        );
    }

    public void updateEntityFromDTO(AtualizaItemCardapioDTO dto, ItemCardapio entity) {
        if (dto == null || entity == null) return;

        if (dto.nome() != null && !dto.nome().isBlank()) {
            entity.setNome(dto.nome());
        }
        if (dto.descricao() != null && !dto.descricao().isBlank()) {
            entity.setDescricao(dto.descricao());
        }
        if (dto.preco() != null) {
            entity.setPreco(dto.preco());
        }
        if (dto.apenasNoRestaurante() != null) {
            entity.setApenasNoRestaurante(dto.apenasNoRestaurante());
        }
        if (dto.caminhoFoto() != null) {
            entity.setCaminhoFoto(dto.caminhoFoto());
        }
    }
}