package com.br.fiap.desafio_tech_2.infrastructure.controllers;

import com.br.fiap.desafio_tech_2.application.dto.AtualizaUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.CadastraUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.dto.DetalhaUsuarioDTO;
import com.br.fiap.desafio_tech_2.application.services.TipoUsuarioService;
import com.br.fiap.desafio_tech_2.application.services.UsuarioService;
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
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários do sistema (Clientes e Donos de Restaurante)")
public class UsuarioController {

    private final UsuarioService usuarioService;


    @Operation(
            summary = "Cadastra um novo usuário",
            description = "Cria um novo usuário no sistema associando-o a um Tipo de Usuário existente (ex: Cliente ou Dono de Restaurante)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos (campos obrigatórios ausentes ou incorretos)"),
            @ApiResponse(responseCode = "404", description = "Tipo de usuário informado não encontrado")
    })
    @PostMapping
    public ResponseEntity<DetalhaUsuarioDTO> save(@RequestBody @Valid CadastraUsuarioDTO dados,
                                                  UriComponentsBuilder uriBuilder){

        DetalhaUsuarioDTO detalhaUsuarioDTO = usuarioService.save(dados);
        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(detalhaUsuarioDTO.id()).toUri();

        return ResponseEntity.created(uri).body(detalhaUsuarioDTO);

    }

    @Operation(
            summary = "Lista usuários",
            description = "Retorna todos os usuários cadastrados ou realiza o filtro parcial/exato pelo nome caso o parâmetro seja informado."
    )
    @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso")
    @GetMapping
    public ResponseEntity<List<DetalhaUsuarioDTO>> getUsuario(@RequestParam(value = "nome", required = false) String name){
        List<DetalhaUsuarioDTO> listaUsuario;
        if(name != null && !name.isBlank()){
            listaUsuario = usuarioService.findByName(name);
        }else {
            listaUsuario = usuarioService.findAll();
        }

        return ResponseEntity.ok(listaUsuario);
    }

    @Operation(summary = "Busca usuário por ID", description = "Retorna os detalhes de um usuário específico a partir do seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário localizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado para o ID informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DetalhaUsuarioDTO> getUsuarioById(@PathVariable("id") Long id){
        DetalhaUsuarioDTO usuario = usuarioService.findById(id);

        return ResponseEntity.ok(usuario);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de atualização inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado para o ID informado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DetalhaUsuarioDTO> update(@PathVariable("id") Long id,
                                                    @RequestBody @Valid AtualizaUsuarioDTO dados){
        DetalhaUsuarioDTO usuario = usuarioService.update(id, dados);

        return ResponseEntity.ok(usuario);
    }


    @Operation(summary = "Remove um usuário por ID", description = "Exclui permanentemente um usuário cadastrado no sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário removido com sucesso (sem conteúdo)"),
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        usuarioService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
