package com.br.fiap.desafio_tech_2.domain.repositories;

import com.br.fiap.desafio_tech_2.domain.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    public List<Usuario> findByNomeContainingIgnoreCase(String name);
}
