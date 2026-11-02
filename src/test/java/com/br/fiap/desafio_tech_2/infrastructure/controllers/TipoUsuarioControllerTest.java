package com.br.fiap.desafio_tech_2.infrastructure.controllers;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.services.TipoUsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.*;
import tools.jackson.databind.ObjectMapper;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TipoUsuarioController.class)
class TipoUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TipoUsuarioService tipoUsuarioService;

    private DetalhaTipoUsuarioDTO criarDetalhaTipoUsuarioDTO() {
        return new DetalhaTipoUsuarioDTO(1L, "Cliente");
    }

    @Nested
    @DisplayName("Cenários de Cadastro (POST)")
    class CadastrarTipoUsuario {

        @Test
        @DisplayName("Deve criar um tipo de usuário com sucesso ao receber dados válidos")
        void deveCriarTipoUsuarioComSucesso() throws Exception {
            CadastraTipoUsuarioDTO requisicao = new CadastraTipoUsuarioDTO("Cliente");
            DetalhaTipoUsuarioDTO resposta = criarDetalhaTipoUsuarioDTO();

            given(tipoUsuarioService.save(any(CadastraTipoUsuarioDTO.class))).willReturn(resposta);

            mockMvc.perform(post("/tipo-usuario")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/tipo-usuario/1"))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nomeTipo").value("Cliente"));
        }

        @Test
        @DisplayName("Deve retornar Bad Request (400) ao enviar DTO com dados inválidos")
        void deveRetornarBadRequestQuandoDadosInvalidos() throws Exception {
            CadastraTipoUsuarioDTO requisicaoInvalida = new CadastraTipoUsuarioDTO("");

            mockMvc.perform(post("/tipo-usuario")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicaoInvalida)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Cenários de Consulta (GET)")
    class ConsultarTipoUsuario {

        @Test
        @DisplayName("Deve listar todos os tipos de usuário registrados")
        void deveListarTodosTiposUsuario() throws Exception {
            List<DetalhaTipoUsuarioDTO> lista = List.of(
                    criarDetalhaTipoUsuarioDTO(),
                    new DetalhaTipoUsuarioDTO(2L, "Administrador")
            );
            given(tipoUsuarioService.findAll()).willReturn(lista);

            mockMvc.perform(get("/tipo-usuario"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nomeTipo").value("Cliente"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].nomeTipo").value("Administrador"));
        }

        @Test
        @DisplayName("Deve retornar OK (200) com lista vazia se não houver registros")
        void deveRetornarListaVazia() throws Exception {
            given(tipoUsuarioService.findAll()).willReturn(Collections.emptyList());

            mockMvc.perform(get("/tipo-usuario"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Deve buscar tipo de usuário por ID com sucesso")
        void deveBuscarPorIdComSucesso() throws Exception {
            DetalhaTipoUsuarioDTO resposta = criarDetalhaTipoUsuarioDTO();
            given(tipoUsuarioService.findById(1L)).willReturn(resposta);

            mockMvc.perform(get("/tipo-usuario/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nomeTipo").value("Cliente"));
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) quando o ID não for encontrado")
        void deveRetornarNotFoundQuandoIdInexistente() throws Exception {
            given(tipoUsuarioService.findById(99L))
                    .willThrow(new EntityNotFoundException("Tipo de usuário não encontrado para o ID: 99"));

            mockMvc.perform(get("/tipo-usuario/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização (PUT)")
    class AtualizarTipoUsuario {

        @Test
        @DisplayName("Deve atualizar tipo de usuário com sucesso")
        void deveAtualizarComSucesso() throws Exception {
            AtualizaTipoUsuarioDTO requisicao = new AtualizaTipoUsuarioDTO("Administrador");
            DetalhaTipoUsuarioDTO resposta = new DetalhaTipoUsuarioDTO(1L, "Administrador");

            given(tipoUsuarioService.update(eq(1L), any(AtualizaTipoUsuarioDTO.class))).willReturn(resposta);

            mockMvc.perform(put("/tipo-usuario/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nomeTipo").value("Administrador"));
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) ao tentar atualizar ID inexistente")
        void deveRetornarNotFoundAoAtualizarInexistente() throws Exception {
            AtualizaTipoUsuarioDTO requisicao = new AtualizaTipoUsuarioDTO("Administrador");

            given(tipoUsuarioService.update(eq(99L), any(AtualizaTipoUsuarioDTO.class)))
                    .willThrow(new EntityNotFoundException("Tipo de usuário não encontrado"));

            mockMvc.perform(put("/tipo-usuario/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Cenários de Exclusão (DELETE)")
    class DeletarTipoUsuario {

        @Test
        @DisplayName("Deve excluir tipo de usuário com sucesso (204 No Content)")
        void deveExcluirComSucesso() throws Exception {
            willDoNothing().given(tipoUsuarioService).delete(1L);

            mockMvc.perform(delete("/tipo-usuario/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) ao tentar excluir ID inexistente")
        void deveRetornarNotFoundAoExcluirInexistente() throws Exception {
            willThrow(new EntityNotFoundException("Tipo de usuário não encontrado"))
                    .given(tipoUsuarioService).delete(99L);

            mockMvc.perform(delete("/tipo-usuario/99"))
                    .andExpect(status().isNotFound());
        }
    }
}