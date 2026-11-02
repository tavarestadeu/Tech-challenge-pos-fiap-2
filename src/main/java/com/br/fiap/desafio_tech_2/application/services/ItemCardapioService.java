package com.br.fiap.desafio_tech_2.application.services;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.mapper.ItemCardapioMapper;
import com.br.fiap.desafio_tech_2.domain.entities.ItemCardapio;
import com.br.fiap.desafio_tech_2.domain.entities.Restaurante;
import com.br.fiap.desafio_tech_2.domain.repositories.ItemCardapioRepository;
import com.br.fiap.desafio_tech_2.domain.repositories.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemCardapioService {

    private final ItemCardapioRepository itemCardapioRepository;
    private final RestauranteRepository restauranteRepository;
    private final ItemCardapioMapper itemCardapioMapper;

    @Transactional
    public DetalhaItemCardapioDTO save(CadastraItemCardapioDTO dados){
        Restaurante restaurante = restauranteRepository.findById(dados.restauranteId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado para o ID: " + dados.restauranteId()));

        ItemCardapio itemCardapio = itemCardapioMapper.toEntity(dados, restaurante);
        itemCardapioRepository.save(itemCardapio);

        return itemCardapioMapper.toDTO(itemCardapio);
    }


    public List<DetalhaItemCardapioDTO> findAll(){
        List<DetalhaItemCardapioDTO> listaItemCardapio = itemCardapioRepository.findAll()
                .stream().map(itemCardapioMapper::toDTO).toList();

        return listaItemCardapio;
    }

    public List<DetalhaItemCardapioDTO> findByName(String name){
        List<DetalhaItemCardapioDTO> listaItemCardapio = itemCardapioRepository.findByNomeContainingIgnoreCase(name)
                .stream().map(itemCardapioMapper::toDTO).toList();

        return listaItemCardapio;
    }

    public DetalhaItemCardapioDTO findById(Long id){
        return itemCardapioMapper.toDTO(buscaPorId(id));
    }

    @Transactional
    public DetalhaItemCardapioDTO update(Long id, AtualizaItemCardapioDTO dados){
        ItemCardapio itemCardapio = buscaPorId(id);

        itemCardapioMapper.updateEntityFromDTO(dados, itemCardapio);

        return itemCardapioMapper.toDTO(itemCardapio);
    }

    @Transactional
    public void delete(Long id){
        itemCardapioRepository.delete(buscaPorId(id));
    }


    private ItemCardapio buscaPorId(Long id){
        return itemCardapioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item-cardápio não encontrado para o ID: " + id));
    }


}
