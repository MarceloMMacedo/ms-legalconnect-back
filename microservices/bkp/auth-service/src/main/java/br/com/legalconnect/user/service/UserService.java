package br.com.legalconnect.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.legalconnect.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Boolean existsById(UUID id) {
        return userRepository.existsById(id);

    }
}
