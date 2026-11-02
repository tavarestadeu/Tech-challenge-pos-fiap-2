package com.br.fiap.desafio_tech_2.application.services;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaEnderecoDTO;
import com.br.fiap.desafio_tech_2.application.dto.AtualizaRestauranteDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraRestauranteDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaRestauranteDTO;
import com.br.fiap.desafio_tech_2.application.mapper.EnderecoMapper;
import com.br.fiap.desafio_tech_2.application.mapper.RestauranteMapper;
import com.br.fiap.desafio_tech_2.domain.entities.Endereco;
import com.br.fiap.desafio_tech_2.domain.entities.Restaurante;
import com.br.fiap.desafio_tech_2.domain.entities.Usuario;
import com.br.fiap.desafio_tech_2.domain.exceptions.TipoUsuarioInvalidoException;
import com.br.fiap.desafio_tech_2.domain.repositories.RestauranteRepository;
import com.br.fiap.desafio_tech_2.domain.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioRepository usuarioRepository;
    private final RestauranteMapper restauranteMapper;
    private final EnderecoMapper enderecoMapper;

    @Transactional
    public DetalhaRestauranteDTO save(CadastraRestauranteDTO dados){
        Usuario dono = usuarioRepository.findById(dados.donoId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o ID: " + dados.donoId()));

        validaTipoUsuarioDoDono(dono.getTipoUsuario().getId());

        Restaurante restaurante = restauranteMapper.toEntity(dados, dono);
        restauranteRepository.save(restaurante);

        return restauranteMapper.toDTO(restaurante);

    }

    public List<DetalhaRestauranteDTO> findAll(){
        List<DetalhaRestauranteDTO> restaurantes = restauranteRepository.findAll()
                .stream().map(restauranteMapper::toDTO).toList();

        return restaurantes;
    }

    public List<DetalhaRestauranteDTO> findByName(String name){
        List<DetalhaRestauranteDTO> restaurantes = restauranteRepository.findByNomeContainingIgnoreCase(name)
                .stream().map(restauranteMapper::toDTO).toList();

        return restaurantes;
    }

    public DetalhaRestauranteDTO findById(Long id){
        return restauranteMapper.toDTO(buscaPorId(id));
    }

    @Transactional
    public DetalhaRestauranteDTO update(Long id, AtualizaRestauranteDTO dados){

        Restaurante restaurante = buscaPorId(id);

        if(dados.donoId() != null){
            atualizaDono(restaurante, dados.donoId());
        }

        if(dados.endereco() != null){
            atualizaEndereco(dados.endereco(), restaurante.getEndereco());
        }

        restaurante.atualizaDados(dados.nome(), dados.horarioFuncionamento());

        return restauranteMapper.toDTO(restaurante);

    }

    @Transactional
    public void delete(Long id){
        restauranteRepository.delete(buscaPorId(id));
    }


    private void atualizaDono(Restaurante restaurante, Long novoDonoId) {
        Usuario novoDono = usuarioRepository.findById(novoDonoId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o ID: " + novoDonoId));

        validaTipoUsuarioDoDono(novoDono.getTipoUsuario().getId());


        restaurante.atualizaDono(novoDono);
    }

    private void validaTipoUsuarioDoDono(Long idDoDono){
        if(!Long.valueOf(2L).equals(idDoDono)){
            throw new TipoUsuarioInvalidoException("O usuário informado não possui permissão para ser cadastrado como dono de restaurante. Informe um usuário do tipo 2.");
        }
    }

    private Restaurante buscaPorId(Long id){
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante não encontrado para o id " + id));
    }

    private void atualizaEndereco(AtualizaEnderecoDTO dto, Endereco endereco){
        enderecoMapper.updateEntityFromDTO(dto, endereco);
    }
}
