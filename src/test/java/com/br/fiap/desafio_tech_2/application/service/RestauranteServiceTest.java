package com.br.fiap.desafio_tech_2.application.service;

import com.br.fiap.desafio_tech_2.application.dto.*;
import com.br.fiap.desafio_tech_2.application.mapper.EnderecoMapper;
import com.br.fiap.desafio_tech_2.application.mapper.RestauranteMapper;
import com.br.fiap.desafio_tech_2.application.services.RestauranteService;
import com.br.fiap.desafio_tech_2.domain.entities.Endereco;
import com.br.fiap.desafio_tech_2.domain.entities.Restaurante;
import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;
import com.br.fiap.desafio_tech_2.domain.entities.Usuario;
import com.br.fiap.desafio_tech_2.domain.enums.HorarioFuncionamentoEnum;
import com.br.fiap.desafio_tech_2.domain.exceptions.TipoUsuarioInvalidoException;
import com.br.fiap.desafio_tech_2.domain.repositories.RestauranteRepository;
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
public class RestauranteServiceTest {

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RestauranteMapper restauranteMapper;

    @Mock
    private EnderecoMapper enderecoMapper;

    @InjectMocks
    private RestauranteService restauranteService;

    @Nested
    @DisplayName("Testes do método save")
    class SaveTests {

        @Test
        @DisplayName("Deve salvar restaurante com sucesso quando o dono for do tipo 2")
        void deveSalvarRestauranteComSucesso() {
            // ARRANGE (Cenário)
            Long donoId = 1L;
            CadastraRestauranteDTO dto = criarCadastroRestauranteDTO(donoId);

            TipoUsuario tipoDono = new TipoUsuario(2L, "Dono de Restaurante");
            Usuario dono = new Usuario();
            dono.setId(donoId);
            dono.setCpf("12345678900");
            dono.setNome("João da Silva");
            dono.setEmail("joao@email.com");
            dono.setLogin("joao.silva");
            dono.setTipoUsuario(tipoDono);

            DetalhaTipoUsuarioDTO tipoUsuarioDTO = new DetalhaTipoUsuarioDTO(2L, "Dono de Restaurante");
            DetalhaUsuarioDTO usuarioDTO = new DetalhaUsuarioDTO(
                    dono.getId(),
                    dono.getCpf(),
                    dono.getNome(),
                    dono.getEmail(),
                    dono.getLogin(),
                    tipoUsuarioDTO
            );

            Endereco enderecoEntidade = new Endereco(
                    dto.endereco().logradouro(),
                    dto.endereco().numero(),
                    dto.endereco().cep(),
                    dto.endereco().bairro(),
                    dto.endereco().cidade(),
                    dto.endereco().uf(),
                    dto.endereco().complemento()
            );

            Restaurante restaurante = new Restaurante();
            restaurante.setId(10L);
            restaurante.setNome(dto.nome());
            restaurante.setEndereco(enderecoEntidade);
            restaurante.setHorarioFuncionamento(dto.horarioFuncionamento());
            restaurante.setDonoDeRestaurante(dono);

            DetalhaRestauranteDTO dtoEsperado = new DetalhaRestauranteDTO(
                    10L, dto.nome(), dto.endereco(), dto.horarioFuncionamento(), usuarioDTO
            );

            when(usuarioRepository.findById(donoId)).thenReturn(Optional.of(dono));
            when(restauranteMapper.toEntity(dto, dono)).thenReturn(restaurante);
            when(restauranteRepository.save(restaurante)).thenReturn(restaurante);
            when(restauranteMapper.toDTO(restaurante)).thenReturn(dtoEsperado);

            // ACT (Ação)
            DetalhaRestauranteDTO resultado = restauranteService.save(dto);

            // ASSERT (Validação)
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(10L);
            assertThat(resultado.nome()).isEqualTo(dto.nome());
            assertThat(resultado.donoDeRestaurante()).isNotNull();
            assertThat(resultado.donoDeRestaurante().id()).isEqualTo(donoId);
            assertThat(resultado.donoDeRestaurante().cpf()).isEqualTo("12345678900");
            assertThat(resultado.donoDeRestaurante().tipoUsuario().id()).isEqualTo(2L);

            verify(usuarioRepository, times(1)).findById(donoId);
            verify(restauranteRepository, times(1)).save(restaurante);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o id do dono não for encontrado")
        void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
            // ARRANGE
            Long donoId = 99L;
            CadastraRestauranteDTO dto = criarCadastroRestauranteDTO(donoId);

            when(usuarioRepository.findById(donoId)).thenReturn(Optional.empty());

            // ACT AND ASSERT
            assertThatThrownBy(() -> restauranteService.save(dto))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Usuário não encontrado para o ID: 99");

            verify(usuarioRepository, times(1)).findById(donoId);
            verifyNoIntermediaryAndNoSaveInteractions();
        }

        @Test
        @DisplayName("Deve lançar TipoUsuarioInvalidoException quando o dono não for do tipo 2")
        void deveLancarExcecaoQuandoUsuarioNaoForTipoDono() {
            // ARRANGE
            Long donoId = 1L;
            CadastraRestauranteDTO dto = criarCadastroRestauranteDTO(donoId);

            TipoUsuario tipoUsuario = new TipoUsuario(1L, "Cliente");
            Usuario cliente = new Usuario();
            cliente.setId(donoId);
            cliente.setTipoUsuario(tipoUsuario);

            when(usuarioRepository.findById(donoId)).thenReturn(Optional.of(cliente));

            // ACT AND ASSERT
            assertThatThrownBy(() -> restauranteService.save(dto))
                    .isInstanceOf(TipoUsuarioInvalidoException.class)
                    .hasMessageContaining("Informe um usuário do tipo 2.");

            verify(usuarioRepository, times(1)).findById(donoId);
            verifyNoIntermediaryAndNoSaveInteractions();
        }
    }

    @Nested
    @DisplayName("Testes do método findAll")
    class FindAllTests {

        @Test
        @DisplayName("Deve retornar lista de restaurantes com sucesso quando existirem registros")
        void deveRetornarListaDeRestaurantesComSucesso() {
            // ARRANGE (Cenário)
            Long donoId = 1L;
            TipoUsuario tipoDono = new TipoUsuario(2L, "Dono de Restaurante");
            Usuario dono = new Usuario();
            dono.setId(donoId);
            dono.setNome("João da Silva");
            dono.setTipoUsuario(tipoDono);

            DetalhaTipoUsuarioDTO tipoUsuarioDTO = new DetalhaTipoUsuarioDTO(2L, "Dono de Restaurante");
            DetalhaUsuarioDTO donoDTO = new DetalhaUsuarioDTO(
                    dono.getId(), "12345678900", dono.getNome(), "joao@email.com", "joao.silva", tipoUsuarioDTO
            );

            EnderecoDTO enderecoDTO = new EnderecoDTO("Rua A", "123", "01000-000", "Bairro X", "São Paulo", "SP", "Apto 1");
            Endereco enderecoEntidade = new Endereco("Rua A", "123", "01000-000", "Bairro X", "São Paulo", "SP", "Apto 1");

            Restaurante restaurante = new Restaurante();
            restaurante.setId(10L);
            restaurante.setNome("Restaurante Teste");
            restaurante.setEndereco(enderecoEntidade);
            restaurante.setHorarioFuncionamento(HorarioFuncionamentoEnum.DIURNO);
            restaurante.setDonoDeRestaurante(dono);

            DetalhaRestauranteDTO dtoEsperado = new DetalhaRestauranteDTO(
                    10L, "Restaurante Teste", enderecoDTO, HorarioFuncionamentoEnum.DIURNO, donoDTO
            );

            when(restauranteRepository.findAll()).thenReturn(List.of(restaurante));
            when(restauranteMapper.toDTO(restaurante)).thenReturn(dtoEsperado);

            // ACT (Ação)
            List<DetalhaRestauranteDTO> resultado = restauranteService.findAll();

            // ASSERT (Validação)
            assertThat(resultado).isNotNull();
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).id()).isEqualTo(10L);
            assertThat(resultado.get(0).nome()).isEqualTo("Restaurante Teste");
            assertThat(resultado.get(0).donoDeRestaurante().id()).isEqualTo(donoId);

            verify(restauranteRepository, times(1)).findAll();
            verify(restauranteMapper, times(1)).toDTO(restaurante);
        }
    }

    @Nested
    @DisplayName("Testes do método findByName")
    class FindByNameTests {

        @Test
        @DisplayName("Deve retornar lista de restaurantes ao buscar por nome existente")
        void deveRetornarListaDeRestaurantesAoBuscarPorNome() {
            // ARRANGE (Cenário)
            String nomeBusca = "Teste";
            Long donoId = 1L;

            TipoUsuario tipoDono = new TipoUsuario(2L, "Dono de Restaurante");
            Usuario dono = new Usuario();
            dono.setId(donoId);
            dono.setNome("João da Silva");
            dono.setTipoUsuario(tipoDono);

            DetalhaTipoUsuarioDTO tipoUsuarioDTO = new DetalhaTipoUsuarioDTO(2L, "Dono de Restaurante");
            DetalhaUsuarioDTO donoDTO = new DetalhaUsuarioDTO(
                    dono.getId(), "12345678900", dono.getNome(), "joao@email.com", "joao.silva", tipoUsuarioDTO
            );

            EnderecoDTO enderecoDTO = new EnderecoDTO("Rua A", "123", "01000-000", "Bairro X", "São Paulo", "SP", "Apto 1");
            Endereco enderecoEntidade = new Endereco("Rua A", "123", "01000-000", "Bairro X", "São Paulo", "SP", "Apto 1");

            Restaurante restaurante = new Restaurante();
            restaurante.setId(10L);
            restaurante.setNome("Restaurante Teste");
            restaurante.setEndereco(enderecoEntidade);
            restaurante.setHorarioFuncionamento(HorarioFuncionamentoEnum.DIURNO);
            restaurante.setDonoDeRestaurante(dono);

            DetalhaRestauranteDTO dtoEsperado = new DetalhaRestauranteDTO(
                    10L, "Restaurante Teste", enderecoDTO, HorarioFuncionamentoEnum.DIURNO, donoDTO
            );

            when(restauranteRepository.findByNomeContainingIgnoreCase(nomeBusca)).thenReturn(List.of(restaurante));
            when(restauranteMapper.toDTO(restaurante)).thenReturn(dtoEsperado);

            // ACT (Ação)
            List<DetalhaRestauranteDTO> resultado = restauranteService.findByName(nomeBusca);

            // ASSERT (Validação)
            assertThat(resultado).isNotNull();
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).id()).isEqualTo(10L);
            assertThat(resultado.get(0).nome()).isEqualTo("Restaurante Teste");
            assertThat(resultado.get(0).donoDeRestaurante().id()).isEqualTo(donoId);

            verify(restauranteRepository, times(1)).findByNomeContainingIgnoreCase(nomeBusca);
            verify(restauranteMapper, times(1)).toDTO(restaurante);
        }

        @Test
        @DisplayName("Deve retornar lista vazia ao buscar por nome inexistente")
        void deveRetornarListaVaziaQuandoNomeNaoEncontrado() {
            // ARRANGE
            String nomeInexistente = "XPTO";
            when(restauranteRepository.findByNomeContainingIgnoreCase(nomeInexistente)).thenReturn(Collections.emptyList());

            // ACT
            List<DetalhaRestauranteDTO> resultado = restauranteService.findByName(nomeInexistente);

            // ASSERT
            assertThat(resultado).isEmpty();
            verify(restauranteRepository, times(1)).findByNomeContainingIgnoreCase(nomeInexistente);
            verify(restauranteMapper, never()).toDTO(any());
        }
    }

    @Nested
    @DisplayName("Testes do método update")
    class UpdateTests {
        @Test
        @DisplayName("Deve atualizar restaurante com sucesso quando o id existir e o dono for válido")
        void deveAtualizarRestauranteComSucesso() {
            // ARRANGE (Cenário)
            Long restauranteId = 10L;
            Long donoId = 1L;


            AtualizaRestauranteDTO dto = criarAtualizaRestauranteDTO(donoId);

            TipoUsuario tipoDono = new TipoUsuario(2L, "Dono de Restaurante");
            Usuario dono = new Usuario();
            dono.setId(donoId);
            dono.setNome("João da Silva");
            dono.setTipoUsuario(tipoDono);

            DetalhaTipoUsuarioDTO tipoUsuarioDTO = new DetalhaTipoUsuarioDTO(2L, "Dono de Restaurante");
            DetalhaUsuarioDTO usuarioDTO = new DetalhaUsuarioDTO(
                    dono.getId(), "12345678900", dono.getNome(), "joao@email.com", "joao.silva", tipoUsuarioDTO
            );

            Endereco enderecoEntidade = new Endereco(
                    dto.endereco().logradouro(), dto.endereco().numero(), dto.endereco().cep(),
                    dto.endereco().bairro(), dto.endereco().cidade(), dto.endereco().uf(), dto.endereco().complemento()
            );

            Restaurante restauranteExistente = new Restaurante();
            restauranteExistente.setId(restauranteId);
            restauranteExistente.setNome("Nome Antigo");

            Restaurante restauranteAtualizado = new Restaurante();
            restauranteAtualizado.setId(restauranteId);
            restauranteAtualizado.setNome(dto.nome());
            restauranteAtualizado.setEndereco(enderecoEntidade);
            restauranteAtualizado.setHorarioFuncionamento(dto.horarioFuncionamento());
            restauranteAtualizado.setDonoDeRestaurante(dono);

            EnderecoDTO enderecoDTO = new EnderecoDTO(
                    dto.endereco().logradouro(),
                    dto.endereco().numero(),
                    dto.endereco().cep(),
                    dto.endereco().bairro(),
                    dto.endereco().cidade(),
                    dto.endereco().uf(),
                    dto.endereco().complemento()
            );


            DetalhaRestauranteDTO dtoEsperado = new DetalhaRestauranteDTO(
                    restauranteId, dto.nome(), enderecoDTO, dto.horarioFuncionamento(), usuarioDTO
            );

            when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restauranteExistente));
            when(usuarioRepository.findById(donoId)).thenReturn(Optional.of(dono));
            when(restauranteMapper.toDTO(restauranteAtualizado)).thenReturn(dtoEsperado);

            // ACT (Ação)
            DetalhaRestauranteDTO resultado = restauranteService.update(restauranteId, dto);

            // ASSERT (Validação)
            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(restauranteId);
            assertThat(resultado.nome()).isEqualTo(dto.nome());

            verify(restauranteRepository, times(1)).findById(restauranteId);
            verify(usuarioRepository, times(1)).findById(donoId);
            verify(restauranteMapper, times(1)).toDTO(restauranteAtualizado);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException quando o novo usuário dono não for encontrado")
        void deveLancarExcecaoQuandoUsuarioDonoNaoEncontradoNaAtualizacao() {
            // ARRANGE
            Long restauranteId = 10L;
            Long donoInexistenteId = 99L;
            AtualizaRestauranteDTO dto = criarAtualizaRestauranteDTO(donoInexistenteId);

            Restaurante restauranteExistente = new Restaurante();
            restauranteExistente.setId(restauranteId);

            when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restauranteExistente));
            when(usuarioRepository.findById(donoInexistenteId)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> restauranteService.update(restauranteId, dto))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Usuário não encontrado para o ID: 99");

            verify(restauranteRepository, times(1)).findById(restauranteId);
            verify(usuarioRepository, times(1)).findById(donoInexistenteId);
            verify(restauranteRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar TipoUsuarioInvalidoException quando o novo dono não for do tipo 2")
        void deveLancarExcecaoQuandoUsuarioDonoNaoForTipoDonoNaAtualizacao() {
            // ARRANGE
            Long restauranteId = 10L;
            Long clienteId = 1L;
            AtualizaRestauranteDTO dto = criarAtualizaRestauranteDTO(clienteId);

            Restaurante restauranteExistente = new Restaurante();
            restauranteExistente.setId(restauranteId);

            TipoUsuario tipoCliente = new TipoUsuario(1L, "Cliente");
            Usuario cliente = new Usuario();
            cliente.setId(clienteId);
            cliente.setTipoUsuario(tipoCliente);

            when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restauranteExistente));
            when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

            // ACT & ASSERT
            assertThatThrownBy(() -> restauranteService.update(restauranteId, dto))
                    .isInstanceOf(TipoUsuarioInvalidoException.class)
                    .hasMessageContaining("Informe um usuário do tipo 2.");

            verify(restauranteRepository, times(1)).findById(restauranteId);
            verify(usuarioRepository, times(1)).findById(clienteId);
            verify(restauranteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Testes do método delete")
    class DeleteTests {

        @Test
        @DisplayName("Deve deletar restaurante com sucesso quando o id existir")
        void deveDeletarRestauranteComSucesso() {
            // ARRANGE
            Long restauranteId = 10L;
            Restaurante restauranteExistente = new Restaurante();
            restauranteExistente.setId(restauranteId);

            when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restauranteExistente));
            doNothing().when(restauranteRepository).delete(restauranteExistente);

            // ACT
            restauranteService.delete(restauranteId);

            // ASSERT
            verify(restauranteRepository, times(1)).findById(restauranteId);
               verify(restauranteRepository, times(1)).delete(restauranteExistente);
        }

        @Test
        @DisplayName("Deve lançar EntityNotFoundException ao tentar deletar restaurante inexistente")
        void deveLancarExcecaoQuandoRestauranteNaoEncontradoNaExclusao() {
            // ARRANGE
            Long restauranteId = 99L;

            when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.empty());

            // ACT & ASSERT
            assertThatThrownBy(() -> restauranteService.delete(restauranteId))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("Restaurante não encontrado para o id 99");

            verify(restauranteRepository, times(1)).findById(restauranteId);
            verify(restauranteRepository, never()).delete(any());
        }


    }





    // Métodos Utilitários
    private CadastraRestauranteDTO criarCadastroRestauranteDTO(Long donoId) {
        EnderecoDTO enderecoDTO = new EnderecoDTO(
                "Rua A", "123", "01000-000", "Bairro X", "São Paulo", "SP", "Apto 1"
        );
        return new CadastraRestauranteDTO(
                "Restaurante Teste",
                enderecoDTO,
                HorarioFuncionamentoEnum.DIURNO,
                donoId
        );
    }

    private void verifyNoIntermediaryAndNoSaveInteractions() {
        verify(restauranteMapper, never()).toEntity(any(), any());
        verify(restauranteRepository, never()).save(any());
    }

    private AtualizaRestauranteDTO criarAtualizaRestauranteDTO(Long donoId) {
        AtualizaEnderecoDTO enderecoDTO = new AtualizaEnderecoDTO(
                "Rua B", "456", "02000-000", "Bairro Y", "São Paulo", "SP", "Apto 2"
        );
        return new AtualizaRestauranteDTO(
                "Restaurante Atualizado",
                enderecoDTO,
                HorarioFuncionamentoEnum.NOTURNO,
                donoId
        );
    }
}