

package com.br.fiap.desafio_tech_2.infrastructure.controllers;



import com.br.fiap.desafio_tech_2.DesafioTech2Application;
import com.br.fiap.desafio_tech_2.application.dto.AtualizaUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.services.UsuarioService;
import org.springframework.test.context.ContextConfiguration;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

@WebMvcTest(controllers = UsuarioController.class)
@ContextConfiguration(classes = DesafioTech2Application.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    private DetalhaUsuarioDTO criarDetalhaUsuarioDTO() {
        return new DetalhaUsuarioDTO(
                1L,
                "12345678901",
                "Maria Silva",
                "maria@email.com",
                "mariasilva",
                new DetalhaTipoUsuarioDTO(1L, "Cliente")
        );
    }

    @Nested
    @DisplayName("Cenários de Cadastro (POST)")
    class CadastrarUsuarios {

        @Test
        @DisplayName("Deve criar um usuário com sucesso ao receber dados válidos")
        void deveCriarUsuarioComSucesso() throws Exception {
            CadastraUsuarioDTO requisicao = new CadastraUsuarioDTO(
                    "12345678901",
                    "Maria Silva",
                    "maria@email.com",
                    "mariasilva",
                    "senha123",
                    1L
            );
            DetalhaUsuarioDTO resposta = criarDetalhaUsuarioDTO();

            given(usuarioService.save(any(CadastraUsuarioDTO.class))).willReturn(resposta);

            mockMvc.perform(post("/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/usuarios/1"))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Maria Silva"))
                    .andExpect(jsonPath("$.email").value("maria@email.com"));
        }

        @Test
        @DisplayName("Deve retornar Bad Request (400) ao enviar DTO com dados inválidos")
        void deveRetornarBadRequestQuandoDadosInvalidos() throws Exception {
            CadastraUsuarioDTO requisicaoInvalida = new CadastraUsuarioDTO(
                    "", "", "email-invalido", "", "123", null
            );

            mockMvc.perform(post("/usuarios")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicaoInvalida)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Cenários de Consulta (GET)")
    class ConsultarUsuarios {

        @Test
        @DisplayName("Deve listar todos os usuários cadastrados")
        void deveListarTodosUsuarios() throws Exception {
            List<DetalhaUsuarioDTO> lista = List.of(criarDetalhaUsuarioDTO());
            given(usuarioService.findAll()).willReturn(lista);

            mockMvc.perform(get("/usuarios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Maria Silva"));
        }

        @Test
        @DisplayName("Deve retornar OK (200) com lista vazia quando não houver usuários")
        void deveRetornarListaVazia() throws Exception {
            given(usuarioService.findAll()).willReturn(Collections.emptyList());

            mockMvc.perform(get("/usuarios"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Deve buscar usuário por ID existente")
        void deveBuscarUsuarioPorIdComSucesso() throws Exception {
            DetalhaUsuarioDTO resposta = criarDetalhaUsuarioDTO();
            given(usuarioService.findById(1L)).willReturn(resposta);

            mockMvc.perform(get("/usuarios/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Maria Silva"));
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) quando o ID informado não existir")
        void deveRetornarNotFoundQuandoIdNaoExiste() throws Exception {
            given(usuarioService.findById(99L))
                    .willThrow(new EntityNotFoundException("Usuário não encontrado para o id 99"));

            mockMvc.perform(get("/usuarios/99"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve buscar usuários por nome")
        void deveBuscarUsuariosPorNome() throws Exception {
            List<DetalhaUsuarioDTO> lista = List.of(criarDetalhaUsuarioDTO());
            given(usuarioService.findByName("Maria")).willReturn(lista);

            mockMvc.perform(get("/usuarios")
                            .param("nome", "Maria"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Maria Silva"));
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização (PUT)")
    class AtualizarUsuarios {

        @Test
        @DisplayName("Deve atualizar dados do usuário com sucesso")
        void deveAtualizarUsuarioComSucesso() throws Exception {
            AtualizaUsuarioDTO requisicao = new AtualizaUsuarioDTO("Maria Souza", "maria.souza@email.com", 1L);
            DetalhaUsuarioDTO resposta = new DetalhaUsuarioDTO(
                    1L,
                    "12345678901",
                    "Maria Souza",
                    "maria.souza@email.com",
                    "mariasilva",
                    new DetalhaTipoUsuarioDTO(1L, "Cliente")
            );

            given(usuarioService.update(eq(1L), any(AtualizaUsuarioDTO.class))).willReturn(resposta);

            mockMvc.perform(put("/usuarios/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome").value("Maria Souza"))
                    .andExpect(jsonPath("$.email").value("maria.souza@email.com"));
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) ao tentar atualizar usuário inexistente")
        void deveRetornarNotFoundAoAtualizarInexistente() throws Exception {
            AtualizaUsuarioDTO requisicao = new AtualizaUsuarioDTO("Maria Souza", "maria.souza@email.com", 1L);

            given(usuarioService.update(eq(99L), any(AtualizaUsuarioDTO.class)))
                    .willThrow(new EntityNotFoundException("Usuário não encontrado"));

            mockMvc.perform(put("/usuarios/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Cenários de Exclusão (DELETE)")
    class DeletarUsuarios {

        @Test
        @DisplayName("Deve excluir usuário com sucesso (204 No Content)")
        void deveExcluirUsuarioComSucesso() throws Exception {
            willDoNothing().given(usuarioService).delete(1L);

            mockMvc.perform(delete("/usuarios/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) ao tentar excluir usuário inexistente")
        void deveRetornarNotFoundAoExcluirInexistente() throws Exception {
            willThrow(new EntityNotFoundException("Usuário não encontrado"))
                    .given(usuarioService).delete(99L);

            mockMvc.perform(delete("/usuarios/99"))
                    .andExpect(status().isNotFound());
        }
    }
}