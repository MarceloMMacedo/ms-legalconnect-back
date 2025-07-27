package br.com.legalconnect.marketplace.depoimento.user.infrastructure.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.legalconnect.marketplace.depoimento.user.domain.service.UserService;

/**
 * Implementação mock do serviço de usuário para fins de demonstração.
 * Em uma aplicação real, esta classe faria a comunicação com o repositório de
 * usuários
 * ou outro serviço de identidade.
 */
@Service
public class UserServiceImpl implements UserService {

    // Simula um conjunto de IDs de usuários existentes para testes
    private final Set<UUID> existingUserIds = new HashSet<>();

    public UserServiceImpl() {
        // Adiciona alguns IDs de usuários de exemplo para simulação
        existingUserIds.add(UUID.fromString("c0e7b4a2-9d7a-4c2f-8b5e-1a3b5c7d9e1f")); // Exemplo de Cliente
        existingUserIds.add(UUID.fromString("a1b2c3d4-e5f6-7890-1234-567890abcdef")); // Exemplo de Profissional
        existingUserIds.add(UUID.fromString("f1d2e3c4-b5a6-9876-5432-10fedcba9876")); // Exemplo de Admin
    }

    @Override
    public boolean userExists(UUID userId) {
        // Lógica mock: Em uma aplicação real, você consultaria o UserRepository aqui.
        // Para este exemplo, verifica se o ID está no conjunto de IDs simulados.
        return existingUserIds.contains(userId);
    }
}