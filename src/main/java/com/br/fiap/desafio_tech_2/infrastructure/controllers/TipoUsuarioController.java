package com.br.fiap.desafio_tech_2.infrastructure.controllers;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaTipoUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.services.TipoUsuarioService;
import com.br.fiap.desafio_tech_2.domain.entities.TipoUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tipo-usuario")
@Tag(name = "Tipo de Usuário", description = "Endpoints para gerenciamento dos tipos de usuários (ex: Cliente, Dono de Restaurante)")
public class TipoUsuarioController {

    @Autowired
    private TipoUsuarioService tipoUsuarioService;


    @Operation(
            summary = "Cadastra um novo tipo de usuário",
            description = "Cria um novo perfil de usuário no sistema para posterior vinculação aos usuários."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo de usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (nome do tipo de usuário ausente ou em branco)")
    })
    @PostMapping
    public ResponseEntity<DetalhaTipoUsuarioDTO> saveTipoUsuario(@RequestBody @Valid CadastraTipoUsuarioDTO dados,
                                                             UriComponentsBuilder uriBuilder){

        var detalhaTipoUsuarioDTO = this.tipoUsuarioService.save(dados);
        URI uri = uriBuilder.path("/tipo-usuario/{id}").buildAndExpand(detalhaTipoUsuarioDTO.id()).toUri();

        return ResponseEntity.created(uri).body(detalhaTipoUsuarioDTO);

    }

    @Operation(
            summary = "Lista tipos de usuários",
            description = "Retorna todos os tipos de usuário cadastrados ou realiza filtragem parcial/exata por nome."
    )
    @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso")
    @GetMapping
    public ResponseEntity<List<DetalhaTipoUsuarioDTO>> getByName(@RequestParam(value = "nome", required = false) String name) {
        List<DetalhaTipoUsuarioDTO> listaTipoUsuario;
        if(name != null && !name.isBlank()){
            listaTipoUsuario = this.tipoUsuarioService.findByName(name);
        }else {
            listaTipoUsuario = this.tipoUsuarioService.findAll();
        }

        return ResponseEntity.ok(listaTipoUsuario);

    }

    @Operation(
            summary = "Busca tipo de usuário por ID",
            description = "Retorna os detalhes de um tipo de usuário a partir do seu identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de usuário localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado para o ID informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DetalhaTipoUsuarioDTO> getById(@PathVariable("id") Long id){
        DetalhaTipoUsuarioDTO usuario = tipoUsuarioService.findById(id);

        return ResponseEntity.ok(usuario);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de atualização inválidos"),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado para o ID informado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DetalhaTipoUsuarioDTO> update(@PathVariable("id") Long id, @RequestBody AtualizaTipoUsuarioDTO dados){
        DetalhaTipoUsuarioDTO clienteAtualizadoDetalhamento = this.tipoUsuarioService.update(id, dados);

        return ResponseEntity.ok(clienteAtualizadoDetalhamento);
    }

    @Operation(
            summary = "Remove um tipo de usuário por ID",
            description = "Exclui permanentemente um tipo de usuário cadastrado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tipo de usuário removido com sucesso (sem conteúdo)"),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário não encontrado para o ID informado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id){
        this.tipoUsuarioService.delete(id);

        return ResponseEntity.noContent().build();
    }




}
