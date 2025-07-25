package br.com.legalconnect.user.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios/up-data")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // - Listar usuários
    @GetMapping("existir/{id}")
    public ResponseEntity<Boolean> existsById(@PathVariable("id") UUID id) {
        Boolean userResponseDTO = userService.existsById(id);
        return ResponseEntity.ok(userResponseDTO);
    }

}
