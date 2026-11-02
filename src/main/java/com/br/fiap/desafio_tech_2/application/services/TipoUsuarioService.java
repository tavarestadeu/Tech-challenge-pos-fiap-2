package com.br.fiap.desafio_tech_2.application.services;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.mapper.TipoUsuarioMapper;
import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;
import com.br.fiap.desafio_tech_2.domain.repositories.TipoUsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TipoUsuarioService {

    private final TipoUsuarioMapper tipoUsuarioMapper;

    private final TipoUsuarioRepository tipoUsuarioRepository;

    public TipoUsuarioService(TipoUsuarioMapper tipoUsuarioMapper, TipoUsuarioRepository tipoUsuarioRepository) {
        this.tipoUsuarioMapper = tipoUsuarioMapper;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    @Transactional
    public DetalhaTipoUsuarioDTO save(CadastraTipoUsuarioDTO dados){
        var tipoUsuario = tipoUsuarioRepository.save(tipoUsuarioMapper.toEntity(dados));
        return tipoUsuarioMapper.toDTO(tipoUsuario);
    }


    public List<DetalhaTipoUsuarioDTO> findAll(){
        var listaTipoUsuario = tipoUsuarioRepository.findAll()
                .stream().map(tipoUsuarioMapper::toDTO).toList();

        return listaTipoUsuario;
    }

    public List<DetalhaTipoUsuarioDTO> findByName(String name){
        var listaTipoUsuario = tipoUsuarioRepository.findByNomeTipoContainingIgnoreCase(name)
                .stream().map(tipoUsuarioMapper::toDTO).toList();

        return listaTipoUsuario;
    }

    @Transactional
    public DetalhaTipoUsuarioDTO update(Long id, AtualizaTipoUsuarioDTO dados){
        TipoUsuario tipoUsuario = buscaPorId(id);

        tipoUsuario.atualizaDados(dados.nomeTipo());

        return tipoUsuarioMapper.toDTO(tipoUsuario);

    }

    @Transactional
    public void delete(Long id){
        var tipoUsuario = buscaPorId(id);
        this.tipoUsuarioRepository.delete(tipoUsuario);
    }


    public DetalhaTipoUsuarioDTO findById(Long id){
         return tipoUsuarioMapper.toDTO(buscaPorId(id));
    }

    private TipoUsuario buscaPorId(Long id){
        return this.tipoUsuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Usuário não encontrado para o ID: " + id));
    }



}
