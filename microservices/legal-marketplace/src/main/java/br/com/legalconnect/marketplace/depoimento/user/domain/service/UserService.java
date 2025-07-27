package br.com.legalconnect.marketplace.depoimento.user.domain.service;

import java.util.UUID;

/**
 * Interface para um serviço de usuário.
 * Em uma aplicação real, esta interface seria implementada pelo módulo de
 * usuários
 * e conteria métodos para gerenciar e consultar dados de usuários.
 * É usada aqui para simular a validação da existência de um usuário.
 */
public interface UserService {
    /**
     * Verifica se um usuário com o ID fornecido existe.
     * 
     * @param userId O ID do usuário a ser verificado.
     * @return true se o usuário existir, false caso contrário.
     */
    boolean userExists(UUID userId);
    // Outros métodos como getUserDetails(UUID userId), etc., poderiam ser
    // adicionados aqui.
}