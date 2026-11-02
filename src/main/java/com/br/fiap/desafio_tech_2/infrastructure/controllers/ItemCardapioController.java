package com.br.fiap.desafio_tech_2.infrastructure.controllers;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaItemCardapioDTO;
import com.br.fiap.desafio_tech_2.application.services.ItemCardapioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/item-cardapio")
@RequiredArgsConstructor
@Tag(name = "Item do Cardápio", description = "Endpoints para gerenciamento de itens e pratos do cardápio")
public class ItemCardapioController {

    private final ItemCardapioService itemCardapioService;


    @Operation(summary = "Cadastra um novo item no cardápio", description = "Salva um item associado a um restaurante existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado para o ID informado")
    })
    @PostMapping
    public ResponseEntity<DetalhaItemCardapioDTO> save(@RequestBody @Valid CadastraItemCardapioDTO dados ,
                                                       UriComponentsBuilder uriBuilder){

        DetalhaItemCardapioDTO itemCardapio = itemCardapioService.save(dados);
        URI uri = uriBuilder.path("/item-cardadio/{id}").buildAndExpand(itemCardapio.id()).toUri();

        return ResponseEntity.created(uri).body(itemCardapio);

    }

    @Operation(summary = "Lista itens do cardápio", description = "Retorna todos os itens cadastrados ou filtra por nome via parâmetro.")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @GetMapping
    public ResponseEntity<List<DetalhaItemCardapioDTO>> getItemCardapio(@RequestParam(value = "nome", required = false) String name){
        List<DetalhaItemCardapioDTO> listaItemCardapio;

        if(name != null && !name.isBlank()){
            listaItemCardapio = itemCardapioService.findByName(name);
        } else {
            listaItemCardapio = itemCardapioService.findAll();
        }

        return ResponseEntity.ok(listaItemCardapio);
    }

    @Operation(summary = "Busca um item do cardápio por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item localizado"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DetalhaItemCardapioDTO> getById(@PathVariable("id") Long id){

        DetalhaItemCardapioDTO itemCardapioDTO = itemCardapioService.findById(id);

        return ResponseEntity.ok(itemCardapioDTO);
    }

    @Operation(
            summary = "Atualiza um item do cardápio por ID",
            description = "Atualiza os dados de um item existente (nome, descrição, preço, disponibilidade e foto). O restaurante vinculado não pode ser alterado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item do cardápio atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (ex: preço negativo)"),
            @ApiResponse(responseCode = "404", description = "Item do cardápio não encontrado para o ID informado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DetalhaItemCardapioDTO> update(@PathVariable("id") Long id,
                                                         @RequestBody AtualizaItemCardapioDTO dados){

        DetalhaItemCardapioDTO itemCardapio = itemCardapioService.update(id, dados);

        return ResponseEntity.ok(itemCardapio);




        }

    @Operation(summary = "Remove um item do cardápio por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item removido com sucesso"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        itemCardapioService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
