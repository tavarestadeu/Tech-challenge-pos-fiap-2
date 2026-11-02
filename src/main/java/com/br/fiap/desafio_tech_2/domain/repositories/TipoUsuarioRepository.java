package com.br.fiap.desafio_tech_2.domain.repositories;

import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoUsuarioRepository extends JpaRepository<TipoUsuario, Long> {

    List<TipoUsuario> findByNomeTipoContainingIgnoreCase(String name);

    List<TipoUsuario> findAll();



}
