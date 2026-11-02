package com.br.fiap.desafio_tech_2.application.services;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.mapper.UsuarioMapper;
import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;
import com.br.fiap.desafio_tech_2.domain.entities.Usuario;
import com.br.fiap.desafio_tech_2.domain.repositories.TipoUsuarioRepository;
import com.br.fiap.desafio_tech_2.domain.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {


    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public DetalhaUsuarioDTO save(CadastraUsuarioDTO dados){
        TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(dados.tipoUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de usuário não encontrado para o ID: " + dados.tipoUsuarioId()));

        Usuario usuario = usuarioMapper.toEntity(dados, tipoUsuario);
        usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(usuario);
    }

    public List<DetalhaUsuarioDTO> findAll(){
        var listaUsario = usuarioRepository.findAll()
                .stream().map(usuarioMapper::toDTO).toList();

        return listaUsario;
    }

    public List<DetalhaUsuarioDTO> findByName(String name){
        var listaUsuario = usuarioRepository.findByNomeContainingIgnoreCase(name)
                .stream().map(usuarioMapper::toDTO).toList();

        return listaUsuario;

    }


    public DetalhaUsuarioDTO findById(Long id) {
        return usuarioMapper.toDTO(buscaPorId(id));

    }

    @Transactional
    public DetalhaUsuarioDTO update(Long id, AtualizaUsuarioDTO dados){
        Usuario usuario = this.buscaPorId(id);
        TipoUsuario novoTipoUsuario = null;

        if(dados.tipoUsuarioId() != null){
            novoTipoUsuario = tipoUsuarioRepository.findById(dados.tipoUsuarioId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de usuário não encontrado para o ID: " + dados.tipoUsuarioId()));
        }

        usuario.atualizaDados(dados, novoTipoUsuario);

        return usuarioMapper.toDTO(usuario);
    }

    @Transactional
    public void delete(Long id){
        usuarioRepository.delete(buscaPorId(id));
    }


    private Usuario buscaPorId(Long id){
        return usuarioRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para o id" + id));
        }
    }
