package com.br.fiap.desafio_tech_2.application.service;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.mapper.UsuarioMapper;
import com.br.fiap.desafio_tech_2.application.services.UsuarioService;
import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;
import com.br.fiap.desafio_tech_2.domain.entities.Usuario;
import com.br.fiap.desafio_tech_2.domain.repositories.TipoUsuarioRepository;
import com.br.fiap.desafio_tech_2.domain.repositories.UsuarioRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoUsuarioRepository tipoUsuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @Nested
    @DisplayName("Testes do método save")
    class SaveTests {

        @Test
        @DisplayName("Deve salvar usuário com sucesso quando o TipoUsuario for válido")
        void deveSalvarUsuarioComSucesso() {
            // ARRANGE
            Long tipoUsuarioId = 1L;
            CadastraUsuarioDTO dto = criarCadastraUsuarioDTO(tipoUsuarioId);

            TipoUsuario tipoUsuario = new TipoUsuario(tipoUsuarioId, "Cliente");
            Usuario usuario = criarUsuarioEntidade(1L, tipoUsuario);

            DetalhaTipoUsuarioDTO tipoUsuarioDTO = new DetalhaTipoUsuarioDTO(tipoUsuarioId, "Cliente");
            DetalhaUsuarioDTO dtoEsperado = new DetalhaUsuarioDTO(
                    1L, dto.cpf(), dto.nome(), dto.email(), dto.login(), tipoUsuarioDTO
            );

            when(tipoUsuarioRepository.findById(tipoUsuarioId)).thenReturn(Optional.of(tipoUsuario));
            when(usuarioMapper.toEntity(dto, tipoUsuario)).thenReturn(usuario);
            when(usuarioRepository.save(usuario)).thenReturn(usuario);
            when(usuarioMapper.toDTO(usuario)).thenReturn(dtoEsperado);

            // ACT
            DetalhaUsuarioDTO resultado = usuarioService.save(dto);

            // ASSERT
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.cpf()).isEqualTo(dto.cpf());
            assertThat(resultado.tipoUsuario().id()).isEqualTo(tipoUsuarioId);

            verify(tipoUsuarioRepository, times(1)).findById(tipoUsuarioId);
            verify(usuarioRepository, times(1)).save(usuario);
            verify(usuarioMapper, times(1)).toDTO(usuario);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o TipoUsuario não for encontrado")
        void deveLancarExcecaoQuandoTipoUsuarioNaoEncontrado() {
            // ARRANGE
            Long tipoUsuarioId = 99L;
            CadastraUsuarioDTO dto = criarCadastraUsuarioDTO(tipoUsuarioId);

            when(tipoUsuarioRepository.findById(tipoUsuarioId)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> usuarioService.save(dto))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Tipo de usuário não encontrado");

            verify(tipoUsuarioRepository, times(1)).findById(tipoUsuarioId);
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método findAll")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar lista de usuários com sucesso")
        void deveRetornarListaDeUsuariosComSucesso() {
            // ARRANGE
            TipoUsuario tipoUsuario = new TipoUsuario(1L, "Cliente");
            Usuario usuario = criarUsuarioEntidade(1L, tipoUsuario);
            DetalhaUsuarioDTO dtoEsperado = criarDetalhaUsuarioDTO(1L);

            when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
            when(usuarioMapper.toDTO(usuario)).thenReturn(dtoEsperado);

            // ACT
            List<DetalhaUsuarioDTO> resultado = usuarioService.findAll();

            // ASSERT
            assertThat(resultado).isNotNull().hasSize(1);
            assertThat(resultado.get(0).id()).isEqualTo(1L);

            verify(usuarioRepository, times(1)).findAll();
            verify(usuarioMapper, times(1)).toDTO(usuario);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver usuários cadastrados")
        void deveRetornarListaVaziaQuandoNaoHouverUsuarios() {
            // ARRANGE
            when(usuarioRepository.findAll()).thenReturn(Collections.emptyList());

            // ACT
            List<DetalhaUsuarioDTO> resultado = usuarioService.findAll();

            // ASSERT
            assertThat(resultado).isEmpty();
            verify(usuarioRepository, times(1)).findAll();
            verify(usuarioMapper, never()).toDTO(any());
        }
    }

    @Nested
    @DisplayName("Testes do método findById")
    class FindByIdTests {

        @Test
        @DisplayName("Deve retornar usuário por ID com sucesso")
        void deveRetornarUsuarioPorIdComSucesso() {
            // ARRANGE
            Long id = 1L;
            TipoUsuario tipoUsuario = new TipoUsuario(1L, "Cliente");
            Usuario usuario = criarUsuarioEntidade(id, tipoUsuario);
            DetalhaUsuarioDTO dtoEsperado = criarDetalhaUsuarioDTO(id);

            when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
            when(usuarioMapper.toDTO(usuario)).thenReturn(dtoEsperado);

            // ACT
            DetalhaUsuarioDTO resultado = usuarioService.findById(id);

            // ASSERT
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(id);

            verify(usuarioRepository, times(1)).findById(id);
            verify(usuarioMapper, times(1)).toDTO(usuario);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o ID não for encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            // ARRANGE
            Long idInexistente = 99L;
            when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> usuarioService.findById(idInexistente))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Usuário não encontrado");

            verify(usuarioRepository, times(1)).findById(idInexistente);
            verify(usuarioMapper, never()).toDTO(any());
        }
    }

    @Nested
    @DisplayName("Testes do método update")
    class UpdateTests {

        @Test
        @DisplayName("Deve atualizar usuário com sucesso via Dirty Checking")
        void deveAtualizarUsuarioComSucesso() {
            // ARRANGE
            Long id = 1L;
            Long tipoUsuarioId = 1L;
            AtualizaUsuarioDTO dto = criarAtualizaUsuarioDTO(tipoUsuarioId);

            TipoUsuario tipoUsuario = new TipoUsuario(tipoUsuarioId, "Cliente");
            Usuario usuarioExistente = criarUsuarioEntidade(id, tipoUsuario);
            DetalhaUsuarioDTO dtoEsperado = criarDetalhaUsuarioDTO(id);

            when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuarioExistente));
            when(tipoUsuarioRepository.findById(tipoUsuarioId)).thenReturn(Optional.of(tipoUsuario));
            when(usuarioMapper.toDTO(usuarioExistente)).thenReturn(dtoEsperado);

            // ACT
            DetalhaUsuarioDTO resultado = usuarioService.update(id, dto);

            // ASSERT
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(id);

            verify(usuarioRepository, times(1)).findById(id);
            verify(tipoUsuarioRepository, times(1)).findById(tipoUsuarioId);
            verify(usuarioRepository, never()).save(any());
        }
    }



    @Nested
    @DisplayName("Testes do método delete")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar usuário com sucesso")
        void deveDeletarUsuarioComSucesso() {
            // ARRANGE
            Long id = 1L;
            Usuario usuario = criarUsuarioEntidade(id, new TipoUsuario(1L, "Cliente"));

            when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
            doNothing().when(usuarioRepository).delete(usuario);

            // ACT
            usuarioService.delete(id);

            // ASSERT
            verify(usuarioRepository, times(1)).findById(id);
            verify(usuarioRepository, times(1)).delete(usuario);
        }
    }

    // MÉTODOS AUXILIARES
    private CadastraUsuarioDTO criarCadastraUsuarioDTO(Long tipoUsuarioId) {
        return new CadastraUsuarioDTO(
                "12345678900", "Maria Souza", "maria@email.com", "maria.souza", "senha123", tipoUsuarioId
        );
    }

    private Usuario criarUsuarioEntidade(Long id, TipoUsuario tipoUsuario) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setCpf("12345678900");
        u.setNome("Maria Souza");
        u.setEmail("maria@email.com");
        u.setLogin("maria.souza");
        u.setTipoUsuario(tipoUsuario);
        return u;
    }

    private DetalhaUsuarioDTO criarDetalhaUsuarioDTO(Long id) {
        DetalhaTipoUsuarioDTO tipoDTO = new DetalhaTipoUsuarioDTO(1L, "Cliente");
        return new DetalhaUsuarioDTO(id, "12345678900", "Maria Souza", "maria@email.com", "maria.souza", tipoDTO);
    }

    private AtualizaUsuarioDTO criarAtualizaUsuarioDTO(Long tipoUsuarioId) {
        return new AtualizaUsuarioDTO(
                "Maria da Silva", "maria@email.com", tipoUsuarioId);

    }
}