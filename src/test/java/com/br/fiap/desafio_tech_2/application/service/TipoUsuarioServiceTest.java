package com.br.fiap.desafio_tech_2.application.service;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.mapper.TipoUsuarioMapper;
import com.br.fiap.desafio_tech_2.application.services.TipoUsuarioService;
import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;
import com.br.fiap.desafio_tech_2.domain.repositories.TipoUsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TipoUsuarioServiceTest {

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Mock
    private TipoUsuarioMapper tipoUsuarioMapper;

    @InjectMocks
    private TipoUsuarioService tipoUsuarioService;

    @Nested
    @DisplayName("Testes do método save")
    class SaveTests {

        @Test
        @DisplayName("Deve salvar tipoUsuario com sucesso")
        void deveSalvarTipoUsuarioComSucesso(){
            //ARRANGE
            String nomeTipo = "Cliente";
            CadastraTipoUsuarioDTO dto = criaCadastraTipoUsuarioDTO(nomeTipo);

            TipoUsuario tipoUsuario = new TipoUsuario();
            tipoUsuario.setId(1L);
            tipoUsuario.setNomeTipo(nomeTipo);

            DetalhaTipoUsuarioDTO dtoEsperado = criaDetalhaTipoUsuarioDTO(tipoUsuario.getId(), tipoUsuario.getNomeTipo());


            when(tipoUsuarioMapper.toEntity(dto)).thenReturn(tipoUsuario);
            when(tipoUsuarioRepository.save(tipoUsuario)).thenReturn(tipoUsuario);
            when(tipoUsuarioMapper.toDTO(tipoUsuario)).thenReturn(dtoEsperado);

            //ACT (AÇÃO)
            DetalhaTipoUsuarioDTO resultado = tipoUsuarioService.save(dto);

            //ASSERT (VALIDAÇÃO)
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.nomeTipo()).isEqualTo(dto.nomeTipo());

            verify(tipoUsuarioMapper, times(1)).toEntity(dto);
            verify(tipoUsuarioRepository, times(1)).save(tipoUsuario);
            verify(tipoUsuarioMapper, times(1)).toDTO(tipoUsuario);

        }

    }

    @Nested
    @DisplayName("Testes do método findAll")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar lista com todos os tipos de usuário registrados")
        void deveRetornarListaDeTiposUsuarioComSucesso() {
            // ARRANGE
            TipoUsuario tipoUsuario = new TipoUsuario(1L, "Cliente");
            DetalhaTipoUsuarioDTO dtoEsperado = criaDetalhaTipoUsuarioDTO(1L, "Cliente");

            when(tipoUsuarioRepository.findAll()).thenReturn(List.of(tipoUsuario));
            when(tipoUsuarioMapper.toDTO(tipoUsuario)).thenReturn(dtoEsperado);

            // ACT
            List<DetalhaTipoUsuarioDTO> resultado = tipoUsuarioService.findAll();

            // ASSERT
            assertThat(resultado).isNotNull().hasSize(1);
            assertThat(resultado.get(0).id()).isEqualTo(1L);
            assertThat(resultado.get(0).nomeTipo()).isEqualTo("Cliente");

            verify(tipoUsuarioRepository, times(1)).findAll();
            verify(tipoUsuarioMapper, times(1)).toDTO(tipoUsuario);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não existirem tipos de usuário")
        void deveRetornarListaVaziaQuandoNaoExistiremRegistros() {
            // ARRANGE
            when(tipoUsuarioRepository.findAll()).thenReturn(Collections.emptyList());

            // ACT
            List<DetalhaTipoUsuarioDTO> resultado = tipoUsuarioService.findAll();

            // ASSERT
            assertThat(resultado).isEmpty();
            verify(tipoUsuarioRepository, times(1)).findAll();
            verify(tipoUsuarioMapper, never()).toDTO(any());
        }
    }

    @Nested
    @DisplayName("Testes do método findById")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar tipo de usuário por ID com sucesso")
        void deveRetornarTipoUsuarioPorIdComSucesso() {
            // ARRANGE
            Long id = 1L;
            TipoUsuario tipoUsuario = new TipoUsuario(id, "Cliente");
            DetalhaTipoUsuarioDTO dtoEsperado = criaDetalhaTipoUsuarioDTO(id, "Cliente");

            when(tipoUsuarioRepository.findById(id)).thenReturn(Optional.of(tipoUsuario));
            when(tipoUsuarioMapper.toDTO(tipoUsuario)).thenReturn(dtoEsperado);

            // ACT
            DetalhaTipoUsuarioDTO resultado = tipoUsuarioService.findById(id);

            // ASSERT
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(id);
            assertThat(resultado.nomeTipo()).isEqualTo("Cliente");

            verify(tipoUsuarioRepository, times(1)).findById(id);
            verify(tipoUsuarioMapper, times(1)).toDTO(tipoUsuario);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o ID não for encontrado")
        void deveLancarExcecaoQuandoIdNaoEncontrado() {
            // ARRANGE
            Long idInexistente = 99L;
            when(tipoUsuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> tipoUsuarioService.findById(idInexistente))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Tipo de Usuário não encontrado para o ID: 99");

            verify(tipoUsuarioRepository, times(1)).findById(idInexistente);
            verify(tipoUsuarioMapper, never()).toDTO(any());
        }
    }

    @Nested
    @DisplayName("Testes do método update")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar tipo de usuário com sucesso via Dirty Checking")
        void deveAtualizarTipoUsuarioComSucesso() {
            // ARRANGE
            Long id = 1L;
            AtualizaTipoUsuarioDTO dtoAtualizacao = criaAtualizaTipoUsuarioDTO("Dono de Restaurante");

            TipoUsuario tipoUsuarioExistente = new TipoUsuario(id, "Cliente");
            DetalhaTipoUsuarioDTO dtoEsperado = criaDetalhaTipoUsuarioDTO(id, "Dono de Restaurante");

            when(tipoUsuarioRepository.findById(id)).thenReturn(Optional.of(tipoUsuarioExistente));
            when(tipoUsuarioMapper.toDTO(tipoUsuarioExistente)).thenReturn(dtoEsperado);

            // ACT
            DetalhaTipoUsuarioDTO resultado = tipoUsuarioService.update(id, dtoAtualizacao);

            // ASSERT
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(id);
            assertThat(resultado.nomeTipo()).isEqualTo("Dono de Restaurante");

            verify(tipoUsuarioRepository, times(1)).findById(id);
            verify(tipoUsuarioMapper, times(1)).toDTO(tipoUsuarioExistente);
            verify(tipoUsuarioRepository, never()).save(any()); // Padrão @Transactional
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException ao tentar atualizar registro inexistente")
        void deveLancarExcecaoNaAtualizacaoQuandoIdNaoEncontrado() {
            // ARRANGE
            Long idInexistente = 99L;
            AtualizaTipoUsuarioDTO dto = criaAtualizaTipoUsuarioDTO("Novo Nome");

            when(tipoUsuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> tipoUsuarioService.update(idInexistente, dto))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Tipo de Usuário não encontrado para o ID: 99");

            verify(tipoUsuarioRepository, times(1)).findById(idInexistente);
            verify(tipoUsuarioRepository, never()).save(any());
        }
    }

    private AtualizaTipoUsuarioDTO criaAtualizaTipoUsuarioDTO(String nomeTipo) {
        return new AtualizaTipoUsuarioDTO(nomeTipo);
    }

    @Nested
    @DisplayName("Testes do método delete")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar tipo de usuário com sucesso quando o ID existir")
        void deveDeletarTipoUsuarioComSucesso() {
            // ARRANGE
            Long id = 1L;
            TipoUsuario tipoUsuarioExistente = new TipoUsuario(id, "Cliente");

            when(tipoUsuarioRepository.findById(id)).thenReturn(Optional.of(tipoUsuarioExistente));
            doNothing().when(tipoUsuarioRepository).delete(tipoUsuarioExistente);

            // ACT
            tipoUsuarioService.delete(id);

            // ASSERT
            verify(tipoUsuarioRepository, times(1)).findById(id);
            verify(tipoUsuarioRepository, times(1)).delete(tipoUsuarioExistente);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException ao tentar deletar registro inexistente")
        void deveLancarExcecaoNaExclusaoQuandoIdNaoEncontrado() {
            // ARRANGE
            Long idInexistente = 99L;
            when(tipoUsuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> tipoUsuarioService.delete(idInexistente))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Tipo de Usuário não encontrado para o ID: 99");

            verify(tipoUsuarioRepository, times(1)).findById(idInexistente);
            verify(tipoUsuarioRepository, never()).delete(any());
        }
    }

    //MÉTODOS AUXILIARES

    private DetalhaTipoUsuarioDTO criaDetalhaTipoUsuarioDTO(Long id, String nomeTipo) {
        return new DetalhaTipoUsuarioDTO(id, nomeTipo);
    }

    private CadastraTipoUsuarioDTO criaCadastraTipoUsuarioDTO(String nomeTipo) {
        return new CadastraTipoUsuarioDTO(nomeTipo);
    }
}
