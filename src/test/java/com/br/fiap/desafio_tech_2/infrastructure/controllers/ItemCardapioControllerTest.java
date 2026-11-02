package com.br.fiap.desafio_tech_2.infrastructure.controllers;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.services.ItemCardapioService;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemCardapioController.class)
class ItemCardapioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ItemCardapioService itemCardapioService;

    private DetalhaItemCardapioDTO criarDetalhaItemCardapioDTO() {
        return new DetalhaItemCardapioDTO(
                1L,
                "Lasanha Bolognesa",
                "Massa artesanal com molho de carne e queijo",
                new BigDecimal("45.00"),
                false,
                "/imagens/lasanha.jpg",
                1L
        );
    }

    @Nested
    @DisplayName("Cenários de Cadastro (POST)")
    class CadastrarItemCardapio {

        @Test
        @DisplayName("Deve criar um item de cardápio com sucesso ao receber dados válidos")
        void deveCriarItemCardapioComSucesso() throws Exception {
            CadastraItemCardapioDTO requisicao = new CadastraItemCardapioDTO(
                    "Lasanha Bolognesa",
                    "Massa artesanal com molho de carne e queijo",
                    new BigDecimal("45.00"),
                    false,
                    "/imagens/lasanha.jpg",
                    1L
            );
            DetalhaItemCardapioDTO resposta = criarDetalhaItemCardapioDTO();

            given(itemCardapioService.save(any(CadastraItemCardapioDTO.class))).willReturn(resposta);

            mockMvc.perform(post("/item-cardapio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/item-cardadio/1"))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Lasanha Bolognesa"))
                    .andExpect(jsonPath("$.preco").value(45.00))
                    .andExpect(jsonPath("$.apenasNoRestaurante").value(false))
                    .andExpect(jsonPath("$.restauranteId").value(1));
        }

        @Test
        @DisplayName("Deve retornar Bad Request (400) ao enviar DTO com dados inválidos")
        void deveRetornarBadRequestQuandoDadosInvalidos() throws Exception {
            CadastraItemCardapioDTO requisicaoInvalida = new CadastraItemCardapioDTO(
                    "",
                    "",
                    null,
                    null,
                    "",
                    null
            );

            mockMvc.perform(post("/item-cardapio")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicaoInvalida)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Cenários de Consulta (GET)")
    class ConsultarItemCardapio {

        @Test
        @DisplayName("Deve listar todos os itens de cardápio quando nenhum nome for informado")
        void deveListarTodosItensCardapio() throws Exception {
            List<DetalhaItemCardapioDTO> lista = List.of(criarDetalhaItemCardapioDTO());
            given(itemCardapioService.findAll()).willReturn(lista);

            mockMvc.perform(get("/item-cardapio"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Lasanha Bolognesa"));
        }

        @Test
        @DisplayName("Deve buscar itens de cardápio por nome quando o parâmetro for informado")
        void deveBuscarItensPorNome() throws Exception {
            List<DetalhaItemCardapioDTO> lista = List.of(criarDetalhaItemCardapioDTO());
            given(itemCardapioService.findByName("Lasanha")).willReturn(lista);

            mockMvc.perform(get("/item-cardapio")
                            .param("nome", "Lasanha"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Lasanha Bolognesa"));
        }

        @Test
        @DisplayName("Deve retornar OK (200) com lista vazia quando não houver registros")
        void deveRetornarListaVazia() throws Exception {
            given(itemCardapioService.findAll()).willReturn(Collections.emptyList());

            mockMvc.perform(get("/item-cardapio"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Deve buscar item de cardápio por ID com sucesso")
        void deveBuscarPorIdComSucesso() throws Exception {
            DetalhaItemCardapioDTO resposta = criarDetalhaItemCardapioDTO();
            given(itemCardapioService.findById(1L)).willReturn(resposta);

            mockMvc.perform(get("/item-cardapio/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Lasanha Bolognesa"))
                    .andExpect(jsonPath("$.preco").value(45.00));
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) quando o ID não for encontrado")
        void deveRetornarNotFoundQuandoIdInexistente() throws Exception {
            given(itemCardapioService.findById(99L))
                    .willThrow(new EntityNotFoundException("Item de cardápio não encontrado para o ID: 99"));

            mockMvc.perform(get("/item-cardapio/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização (PUT)")
    class AtualizarItemCardapio {

        @Test
        @DisplayName("Deve atualizar item de cardápio com sucesso")
        void deveAtualizarComSucesso() throws Exception {
            AtualizaItemCardapioDTO requisicao = new AtualizaItemCardapioDTO(
                    "Lasanha Especial",
                    "Massa artesanal com molho de carne e 4 queijos",
                    new BigDecimal("50.00"),
                    true,
                    "/imagens/lasanha_especial.jpg"
            );
            DetalhaItemCardapioDTO resposta = new DetalhaItemCardapioDTO(
                    1L,
                    "Lasanha Especial",
                    "Massa artesanal com molho de carne e 4 queijos",
                    new BigDecimal("50.00"),
                    true,
                    "/imagens/lasanha_especial.jpg",
                    1L
            );

            given(itemCardapioService.update(eq(1L), any(AtualizaItemCardapioDTO.class))).willReturn(resposta);

            mockMvc.perform(put("/item-cardapio/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Lasanha Especial"))
                    .andExpect(jsonPath("$.preco").value(50.00))
                    .andExpect(jsonPath("$.apenasNoRestaurante").value(true));
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) ao tentar atualizar ID inexistente")
        void deveRetornarNotFoundAoAtualizarInexistente() throws Exception {
            AtualizaItemCardapioDTO requisicao = new AtualizaItemCardapioDTO(
                    "Lasanha Especial",
                    "Descrição",
                    new BigDecimal("50.00"),
                    true,
                    "/imagens/foto.jpg"
            );

            given(itemCardapioService.update(eq(99L), any(AtualizaItemCardapioDTO.class)))
                    .willThrow(new EntityNotFoundException("Item de cardápio não encontrado"));

            mockMvc.perform(put("/item-cardapio/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Cenários de Exclusão (DELETE)")
    class DeletarItemCardapio {

        @Test
        @DisplayName("Deve excluir item de cardápio com sucesso (204 No Content)")
        void deveExcluirComSucesso() throws Exception {
            willDoNothing().given(itemCardapioService).delete(1L);

            mockMvc.perform(delete("/item-cardapio/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) ao tentar excluir ID inexistente")
        void deveRetornarNotFoundAoExcluirInexistente() throws Exception {
            willThrow(new EntityNotFoundException("Item de cardápio não encontrado"))
                    .given(itemCardapioService).delete(99L);

            mockMvc.perform(delete("/item-cardapio/99"))
                    .andExpect(status().isNotFound());
        }
    }
}