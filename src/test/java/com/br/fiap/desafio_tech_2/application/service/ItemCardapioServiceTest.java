package com.br.fiap.desafio_tech_2.application.service;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.mapper.ItemCardapioMapper;
import com.br.fiap.desafio_tech_2.application.services.ItemCardapioService;
import com.br.fiap.desafio_tech_2.domain.entities.ItemCardapio;
import com.br.fiap.desafio_tech_2.domain.entities.Restaurante;
import com.br.fiap.desafio_tech_2.domain.repositories.ItemCardapioRepository;
import com.br.fiap.desafio_tech_2.domain.repositories.RestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemCardapioServiceTest {

    @Mock
    private ItemCardapioRepository itemCardapioRepository;

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private ItemCardapioMapper itemCardapioMapper;

    @InjectMocks
    private ItemCardapioService itemCardapioService;

    @Nested
    @DisplayName("Testes do método save")
    class SaveTests {

        @Test
        @DisplayName("Deve salvar item do cardápio com sucesso quando o restaurante existir")
        void deveSalvarItemCardapioComSucesso() {
            // ARRANGE
            Long restauranteId = 10L;
            CadastraItemCardapioDTO dto = criarCadastraItemCardapioDTO(restauranteId);

            Restaurante restaurante = new Restaurante();
            restaurante.setId(restauranteId);

            ItemCardapio item = new ItemCardapio();
            item.setId(1L);
            item.setNome(dto.nome());
            item.setRestaurante(restaurante);

            DetalhaItemCardapioDTO dtoEsperado = new DetalhaItemCardapioDTO(
                    1L, dto.nome(), dto.descricao(), dto.preco(), dto.apenasNoRestaurante(), dto.caminhoFoto(), restauranteId
            );

            when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));
            when(itemCardapioMapper.toEntity(dto, restaurante)).thenReturn(item);
            when(itemCardapioRepository.save(item)).thenReturn(item);
            when(itemCardapioMapper.toDTO(item)).thenReturn(dtoEsperado);

            // ACT
            DetalhaItemCardapioDTO resultado = itemCardapioService.save(dto);

            // ASSERT
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.nome()).isEqualTo(dto.nome());

            verify(restauranteRepository, times(1)).findById(restauranteId);
            verify(itemCardapioRepository, times(1)).save(item);
            verify(itemCardapioMapper, times(1)).toDTO(item);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o restaurante não existir")
        void deveLancarExcecaoQuandoRestauranteNaoEncontrado() {
            // ARRANGE
            Long restauranteId = 99L;
            CadastraItemCardapioDTO dto = criarCadastraItemCardapioDTO(restauranteId);

            when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> itemCardapioService.save(dto))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Restaurante não encontrado");

            verify(restauranteRepository, times(1)).findById(restauranteId);
            verify(itemCardapioRepository, never()).save(any());
        }
    }



    @Nested
    @DisplayName("Testes do método update")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar item do cardápio com sucesso")
        void deveAtualizarItemCardapioComSucesso() {
            // ARRANGE
            Long itemId = 1L;
            Long restauranteId = 10L;
            AtualizaItemCardapioDTO dto = criarAtualizaItemCardapioDTO();

            ItemCardapio itemExistente = new ItemCardapio();
            itemExistente.setId(itemId);

            Restaurante restaurante = new Restaurante();
            restaurante.setId(restauranteId);

            DetalhaItemCardapioDTO dtoEsperado = criarDetalhaItemCardapioDTO(itemId, restauranteId);

            when(itemCardapioRepository.findById(itemId)).thenReturn(Optional.of(itemExistente));
            when(itemCardapioMapper.toDTO(itemExistente)).thenReturn(dtoEsperado);

            // ACT
            DetalhaItemCardapioDTO resultado = itemCardapioService.update(itemId, dto);

            // ASSERT
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(itemId);

            verify(itemCardapioRepository, times(1)).findById(itemId);
            verify(itemCardapioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método delete")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar item do cardápio com sucesso")
        void deveDeletarItemCardapioComSucesso() {
            // ARRANGE
            Long itemId = 1L;
            ItemCardapio item = new ItemCardapio();
            item.setId(itemId);

            when(itemCardapioRepository.findById(itemId)).thenReturn(Optional.of(item));
            doNothing().when(itemCardapioRepository).delete(item);

            // ACT
            itemCardapioService.delete(itemId);

            // ASSERT
            verify(itemCardapioRepository, times(1)).findById(itemId);
            verify(itemCardapioRepository, times(1)).delete(item);
        }
    }

    // MÉTODOS AUXILIARES
    private CadastraItemCardapioDTO criarCadastraItemCardapioDTO(Long restauranteId) {
        return new CadastraItemCardapioDTO(
                "Hambúrguer Gourmet",
                "Pão brioche, carne 180g e queijo cheddar",
                new BigDecimal("35.00"),
                false,
                "/imagens/hamburguer.png",
                restauranteId
        );
    }

    private DetalhaItemCardapioDTO criarDetalhaItemCardapioDTO(Long itemId, Long restauranteId) {
        return new DetalhaItemCardapioDTO(
                itemId,
                "Hambúrguer Gourmet",
                "Pão brioche, carne 180g e queijo",
                new BigDecimal("35.00"),
                false,
                "/imagens/burguer.png",
                restauranteId
        );
    }

    private AtualizaItemCardapioDTO criarAtualizaItemCardapioDTO() {
        return new AtualizaItemCardapioDTO(
                "Hambúrguer Gourmet",
                "Pão brioche, carne 180g e queijo",
                new BigDecimal("35.00"),
                false,
                "/imagens/burguer.png"
        );
    }
}