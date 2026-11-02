package com.br.fiap.desafio_tech_2.domain.repositories;

import com.br.fiap.desafio_tech_2.domain.entities.ItemCardapio;
import com.br.fiap.desafio_tech_2.domain.entities.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCardapioRepository extends JpaRepository<ItemCardapio, Long> {

    List<ItemCardapio> findByNomeContainingIgnoreCase(String name);

}
