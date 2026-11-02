package com.br.fiap.desafio_tech_2.infrastructure.controllers;

import com.br.fiap.desafio_tech_2.application.dto.*;
import com.br.fiap.desafio_tech_2.application.services.RestauranteService;
import com.br.fiap.desafio_tech_2.domain.enums.HorarioFuncionamentoEnum;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RestauranteController.class)
class RestauranteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RestauranteService restauranteService;

    private EnderecoDTO criarEnderecoDTO(){
        return new EnderecoDTO(
                "Rua das Flores",
                "123",
                "01001-000",
                "Centro",
                "São Paulo",
                "SP",
                "Apt 101"
        );

    }

    private AtualizaEnderecoDTO criarAtualizaEnderecoDTO(){
        return new AtualizaEnderecoDTO(
                "Rua das Rosas",
                "123",
                "01001-000",
                "Centro",
                "Rio de Janeiro",
                "RJ",
                "Apt 101"
        );
    }

    private EnderecoDTO atualizarEnderecoDTO(AtualizaEnderecoDTO endereco){
        return new EnderecoDTO(
            endereco.logradouro(),
                endereco.numero(),
                endereco.cep(),
                endereco.bairro(),
                endereco.cidade(),
                endereco.uf(),
                endereco.complemento()
        );
    }

    private DetalhaUsuarioDTO criarDetalhaUsuarioDTO(){
        return new DetalhaUsuarioDTO(
                1L,
                "12345678901",
                "Maria Silva",
                "maria@email.com",
                "mariasilva",
                new DetalhaTipoUsuarioDTO(1L, "Dono de Restaurante")
        );
    }

    private DetalhaRestauranteDTO criarDetalhaRestauranteDTO() {

        EnderecoDTO endereco = criarEnderecoDTO();
        DetalhaUsuarioDTO dono = criarDetalhaUsuarioDTO();


        return new DetalhaRestauranteDTO(
                1L,
                "Sabor & Arte",
                endereco,
                HorarioFuncionamentoEnum.AMBOS, // Ajuste para a constante do seu Enum
                dono
        );
    }

    @Nested
    @DisplayName("Cenários de Cadastro (POST)")
    class CadastrarRestaurante {

        @Test
        @DisplayName("Deve criar um restaurante com sucesso ao receber dados válidos")
        void deveCriarRestauranteComSucesso() throws Exception {
            EnderecoDTO enderecoDTO = criarEnderecoDTO();
            CadastraRestauranteDTO requisicao = new CadastraRestauranteDTO(
                    "Sabor & Arte",
                    enderecoDTO,
                    HorarioFuncionamentoEnum.AMBOS,
                    1L
            );
            DetalhaRestauranteDTO resposta = criarDetalhaRestauranteDTO();

            given(restauranteService.save(any(CadastraRestauranteDTO.class))).willReturn(resposta);

            mockMvc.perform(post("/restaurantes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/restaurantes/1"))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Sabor & Arte"))
                    .andExpect(jsonPath("$.horarioFuncionamento").value("Almoço e jantar"))
                    .andExpect(jsonPath("$.donoDeRestaurante.id").value(1L))
                    .andExpect(jsonPath("$.endereco.logradouro").value(resposta.endereco().logradouro()));
        }

        @Test
        @DisplayName("Deve retornar Bad Request (400) ao enviar DTO com dados inválidos")
        void deveRetornarBadRequestQuandoDadosInvalidos() throws Exception {
            CadastraRestauranteDTO requisicaoInvalida = new CadastraRestauranteDTO("", criarEnderecoDTO(),
                    HorarioFuncionamentoEnum.DIURNO, 1L);

            mockMvc.perform(post("/restaurantes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicaoInvalida)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Cenários de Consulta (GET)")
    class ConsultarRestaurante {

        @Test
        @DisplayName("Deve listar todos os restaurantes cadastrados")
        void deveListarTodosRestaurantes() throws Exception {
            List<DetalhaRestauranteDTO> lista = List.of(criarDetalhaRestauranteDTO());
            given(restauranteService.findAll()).willReturn(lista);

            mockMvc.perform(get("/restaurantes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Sabor & Arte"));
        }

        @Test
        @DisplayName("Deve retornar OK (200) com lista vazia quando não houver restaurantes")
        void deveRetornarListaVazia() throws Exception {
            given(restauranteService.findAll()).willReturn(Collections.emptyList());

            mockMvc.perform(get("/restaurantes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Deve buscar restaurante por ID com sucesso")
        void deveBuscarPorIdComSucesso() throws Exception {
            DetalhaRestauranteDTO resposta = criarDetalhaRestauranteDTO();
            given(restauranteService.findById(1L)).willReturn(resposta);

            mockMvc.perform(get("/restaurantes/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Sabor & Arte"));
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) quando o ID não for encontrado")
        void deveRetornarNotFoundQuandoIdInexistente() throws Exception {
            given(restauranteService.findById(99L))
                    .willThrow(new EntityNotFoundException("Restaurante não encontrado para o ID: 99"));

            mockMvc.perform(get("/restaurantes/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Cenários de Atualização (PUT)")
    class AtualizarRestaurante {

        @Test
        @DisplayName("Deve atualizar restaurante com sucesso")
        void deveAtualizarComSucesso() throws Exception {
            AtualizaEnderecoDTO enderecoDTO = criarAtualizaEnderecoDTO();
            AtualizaRestauranteDTO requisicao = new AtualizaRestauranteDTO(
                    "Sabor & Arte Renovado",
                    enderecoDTO,
                    HorarioFuncionamentoEnum.AMBOS,
                    1L
            );

            EnderecoDTO enderecoAtualizadoDTO = atualizarEnderecoDTO(enderecoDTO);
            DetalhaUsuarioDTO dono = criarDetalhaUsuarioDTO();

            DetalhaRestauranteDTO resposta = new DetalhaRestauranteDTO(
                    1L,
                    "Sabor & Arte Renovado",
                    enderecoAtualizadoDTO,
                    HorarioFuncionamentoEnum.AMBOS,
                    dono
            );

            given(restauranteService.update(eq(1L), any(AtualizaRestauranteDTO.class))).willReturn(resposta);

            mockMvc.perform(put("/restaurantes/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Sabor & Arte Renovado"));
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) ao tentar atualizar ID inexistente")
        void deveRetornarNotFoundAoAtualizarInexistente() throws Exception {
            AtualizaRestauranteDTO requisicao = new AtualizaRestauranteDTO(
                    "Sabor & Arte",
                    criarAtualizaEnderecoDTO(),
                    HorarioFuncionamentoEnum.AMBOS,
                    99L
            );

            given(restauranteService.update(eq(99L), any(AtualizaRestauranteDTO.class)))
                    .willThrow(new EntityNotFoundException("Restaurante não encontrado"));

            mockMvc.perform(put("/restaurantes/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requisicao)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Cenários de Exclusão (DELETE)")
    class DeletarRestaurante {

        @Test
        @DisplayName("Deve excluir restaurante com sucesso (204 No Content)")
        void deveExcluirComSucesso() throws Exception {
            willDoNothing().given(restauranteService).delete(1L);

            mockMvc.perform(delete("/restaurantes/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar Not Found (404) ao tentar excluir ID inexistente")
        void deveRetornarNotFoundAoExcluirInexistente() throws Exception {
            willThrow(new EntityNotFoundException("Restaurante não encontrado"))
                    .given(restauranteService).delete(99L);

            mockMvc.perform(delete("/restaurantes/99"))
                    .andExpect(status().isNotFound());
        }
    }
}