package br.com.legalconnect.depoimento.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.legalconnect.depoimento.repository.UserRepository;
import lombok.RequiredArgsConstructor;

/**
 * Implementação mock do serviço de usuário para fins de demonstração.
 * Em uma aplicação real, esta classe faria a comunicação com o repositório de
 * usuários
 * ou outro serviço de identidade.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;

    public boolean userExists(UUID userId) {
        // Lógica mock: Em uma aplicação real, você consultaria o UserRepository aqui.
        // Para este exemplo, verifica se o ID está no conjunto de IDs simulados.
        return userRepository.existsById(userId);

    }
}