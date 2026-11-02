package com.br.fiap.desafio_tech_2.infrastructure.controllers;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaRestauranteDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraRestauranteDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaRestauranteDTO;
import com.br.fiap.desafio_tech_2.application.services.RestauranteService;
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
@RequestMapping("/restaurantes")
@RequiredArgsConstructor
@Tag(name = "Restaurantes", description = "Endpoints para gerenciamento do cadastro de restaurantes e seus respectivos donos")
public class RestauranteController {

    private final RestauranteService restauranteService;


    @Operation(
            summary = "Cadastra um novo restaurante",
            description = "Cadastra um restaurante no sistema vinculando-o a um usuário responsável (dono) existente. Exige nome, endereço, tipo de cozinha e horário de funcionamento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (campos obrigatórios ausentes ou incorretos)"),
            @ApiResponse(responseCode = "404", description = "Usuário (dono) informado não foi encontrado")
    })
    @PostMapping
    public ResponseEntity<DetalhaRestauranteDTO> save(@RequestBody @Valid CadastraRestauranteDTO dados,
                                                      UriComponentsBuilder uriBuilder){

        DetalhaRestauranteDTO restaurante = restauranteService.save(dados);
        URI uri = uriBuilder.path("/restaurantes/{id}").buildAndExpand(restaurante.id()).toUri();

        return ResponseEntity.created(uri).body(restaurante);
    }

    @Operation(
            summary = "Lista restaurantes",
            description = "Retorna todos os restaurantes cadastrados ou permite a filtragem parcial/exata pelo nome do estabelecimento."
    )
    @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso")
    @GetMapping
    public ResponseEntity<List<DetalhaRestauranteDTO>> getRestaurante(@RequestParam(value = "nome", required = false)
                                                                      String name){
        List<DetalhaRestauranteDTO> restaurantes;

        if(name != null && !name.isBlank()){
            restaurantes = restauranteService.findByName(name);
        }else {
            restaurantes = restauranteService.findAll();
        }

        return ResponseEntity.ok(restaurantes);
    }

    @Operation(summary = "Busca restaurante por ID", description = "Retorna os detalhes completos de um restaurante a partir do seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado para o ID informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DetalhaRestauranteDTO> getById(@PathVariable("id") Long id){
        DetalhaRestauranteDTO restaurante = restauranteService.findById(id);

        return ResponseEntity.ok(restaurante);
    }

    @Operation(
            summary = "Atualiza um restaurante por ID",
            description = "Atualiza as informações de um restaurante existente (nome, endereço, tipo de cozinha, horário de funcionamento ou troca de dono)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de atualização inválidos"),
            @ApiResponse(responseCode = "404", description = "Restaurante ou Usuário (dono) informado não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DetalhaRestauranteDTO> update(@PathVariable("id") Long id,
                                                        @RequestBody AtualizaRestauranteDTO dados){

        DetalhaRestauranteDTO restaurante = restauranteService.update(id, dados);

        return ResponseEntity.ok(restaurante);
    }

    @Operation(summary = "Remove um restaurante por ID", description = "Exclui permanentemente o cadastro de um restaurante no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurante removido com sucesso (sem conteúdo)"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado para o ID informado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        restauranteService.delete(id);

        return ResponseEntity.noContent().build();
    }




}
