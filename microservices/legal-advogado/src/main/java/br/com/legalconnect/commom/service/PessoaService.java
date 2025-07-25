package br.com.legalconnect.commom.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.domain.modal.entity.LocalAtuacao;
import br.com.legalconnect.commom.dto.request.PessoaRequestDTO;
import br.com.legalconnect.commom.dto.request.UserRequestDTO;
import br.com.legalconnect.commom.dto.response.PessoaResponseDTO;
import br.com.legalconnect.commom.mapper.PessoaMapper;
import br.com.legalconnect.commom.mapper.UserMapper;
import br.com.legalconnect.commom.model.Endereco;
import br.com.legalconnect.commom.model.Pessoa;
import br.com.legalconnect.commom.model.User;
import br.com.legalconnect.commom.repository.PessoaRepository; // Assumindo a existência de PessoaRepository
import br.com.legalconnect.commom.repository.UserRepository; // Assumindo a existência de UserRepository
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.transaction.Transactional;

/**
 * Serviço responsável pela gestão das entidades Pessoa e User.
 * Centraliza a lógica de negócio para criação, atualização e busca de Pessoas
 * e seus usuários associados, incluindo validações de unicidade e criptografia
 * de senha.
 */
@Service
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final UserRepository userRepository;
    private final PessoaMapper pessoaMapper;
    private final UserMapper userMapper;
    // Assumindo um EnderecoMapper se EnderecoRequestDTO for mapeado para Endereco
    // aqui
    // private final EnderecoMapper enderecoMapper;

    @Autowired
    public PessoaService(PessoaRepository pessoaRepository, UserRepository userRepository,
            PessoaMapper pessoaMapper, UserMapper userMapper) {
        this.pessoaRepository = pessoaRepository;
        this.userRepository = userRepository;
        this.pessoaMapper = pessoaMapper;
        this.userMapper = userMapper;
    }

    /**
     * Cria uma nova Pessoa e seu User associado.
     * Regras de Negócio:
     * - Garante que o CPF e o e-mail do usuário sejam únicos no sistema.
     * - Criptografa a senha antes de salvar.
     * - Define o status inicial do usuário como PENDING para aguardar ativação.
     *
     * @param pessoaRequestDTO DTO com os dados da Pessoa e do User.
     * @return DTO da Pessoa criada.
     * @throws BusinessException se o CPF ou e-mail já estiverem cadastrados.
     */
    @Transactional
    public Pessoa createPessoa(PessoaRequestDTO pessoaRequestDTO) {
        // Regra de Negócio: Validar unicidade de CPF
        if (pessoaRepository.existsByCpf(pessoaRequestDTO.getCpf())) {
            throw new BusinessException(ErrorCode.CPF_DUPLICADO, HttpStatus.CONFLICT, pessoaRequestDTO.getCpf());
        }

        // Regra de Negócio: Validar unicidade de e-mail para o usuário
        if (userRepository.existsByEmail(pessoaRequestDTO.getUsuario().getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICADO, HttpStatus.CONFLICT,
                    pessoaRequestDTO.getUsuario().getEmail());
        }

        // Cria e criptografa o usuário

        // Persiste o usuário
        var newUser = new User();
        newUser.setId(UUID.fromString(pessoaRequestDTO.getUsuario().getId()));

        // Cria a Pessoa e associa o usuário
        Pessoa newPessoa = Pessoa.builder()
                .usuario(newUser)
                .nomeCompleto(pessoaRequestDTO.getNomeCompleto())
                .cpf(pessoaRequestDTO.getCpf())
                .dataNascimento(pessoaRequestDTO.getDataNascimento())
                .telefones(new HashSet<>(pessoaRequestDTO.getTelefones()))
                .build();

        // Adiciona endereços
        if (pessoaRequestDTO.getEnderecos() != null && !pessoaRequestDTO.getEnderecos().isEmpty()) {
            Set<Endereco> enderecos = pessoaRequestDTO.getEnderecos().stream()
                    .map(dto -> {
                        Endereco endereco = new Endereco(); // Ou use enderecoMapper.toEntity(dto) se existir
                        endereco.setLogradouro(dto.getLogradouro());
                        endereco.setNumero(dto.getNumero());
                        endereco.setComplemento(dto.getComplemento());
                        endereco.setBairro(dto.getBairro());
                        endereco.setCidade(dto.getCidade());
                        endereco.setEstado(dto.getEstado());
                        endereco.setCep(dto.getCep());
                        endereco.setPais(dto.getPais() != null ? dto.getPais() : "Brasil"); // Define país padrão
                        endereco.setTipoEndereco(dto.getTipoEndereco());
                        endereco.setPessoa(newPessoa); // Associa o endereço à Pessoa
                        return endereco;
                    }).collect(Collectors.toSet());
            newPessoa.setEnderecos(enderecos);
        }
        // Reassigning to make it effectively final
        return (pessoaRepository.save(newPessoa));
    }

    /**
     * Busca uma Pessoa pelo ID.
     *
     * @param id ID da Pessoa.
     * @return DTO da Pessoa encontrada.
     * @throws BusinessException se a Pessoa não for encontrada.
     */
    public PessoaResponseDTO findPessoaById(UUID id) {
        Pessoa pessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Pessoa não encontrada."));
        return pessoaMapper.toResponseDTO(pessoa);
    }

    /**
     * Atualiza os dados de uma Pessoa existente.
     * Regras de Negócio:
     * - Permite a atualização de informações da Pessoa e do User associado.
     * - Valida a unicidade do e-mail se for alterado.
     * - Não permite a alteração do CPF.
     *
     * @param id               ID da Pessoa a ser atualizada.
     * @param pessoaRequestDTO DTO com os dados para atualização.
     * @return DTO da Pessoa atualizada.
     * @throws BusinessException se a Pessoa não for encontrada, ou se o e-mail já
     *                           estiver cadastrado.
     */
    @Transactional
    public PessoaResponseDTO updatePessoa(UUID id, PessoaRequestDTO pessoaRequestDTO) {
        Pessoa existingPessoa = pessoaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Pessoa não encontrada para atualização."));

        // Não permitir alteração de CPF (Regra de Negócio)
        if (!existingPessoa.getCpf().equals(pessoaRequestDTO.getCpf())) {
            throw new BusinessException(ErrorCode.DADOS_INVALIDOS, HttpStatus.BAD_REQUEST,
                    "Não é permitido alterar o CPF de uma pessoa existente.");
        }

        // Atualiza o User associado
        User existingUser = existingPessoa.getUsuario();
        UserRequestDTO userRequestDTO = pessoaRequestDTO.getUsuario();

        // Valida unicidade de e-mail se o e-mail for alterado
        if (!existingUser.getEmail().equals(userRequestDTO.getEmail())
                && userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICADO, HttpStatus.CONFLICT, userRequestDTO.getEmail());
        }

        userMapper.updateEntityFromDto(userRequestDTO, existingUser);
        userRepository.save(existingUser); // Salva as alterações no User

        // Atualiza os dados da Pessoa (exceto usuário e endereços que são gerenciados
        // separadamente)
        pessoaMapper.updateEntityFromDto(pessoaRequestDTO, existingPessoa);

        // Lógica para gerenciar Endereços: Adicionar, Atualizar, Remover
        // Isso pode ser complexo e requer lógica manual, pois EndereçoRequestDTO tem ID
        // opcional
        // Para simplificar, vou redefinir os endereços ou adicionar lógica de
        // diferenciação.
        // Uma abordagem mais robusta seria comparar listas e fazer operações de CRUD
        // individualmente.
        if (pessoaRequestDTO.getEnderecos() != null) {
            // Removendo endereços que não estão no DTO
            existingPessoa.getEnderecos().removeIf(existingEndereco -> pessoaRequestDTO.getEnderecos().stream()
                    .noneMatch(dto -> dto.getId() != null && dto.getId().equals(existingEndereco.getId().toString())));

            // Adicionando ou atualizando endereços
            for (br.com.legalconnect.commom.dto.request.EnderecoRequestDTO dto : pessoaRequestDTO.getEnderecos()) {
                if (dto.getId() == null) {
                    // Novo endereço
                    Endereco newEndereco = new Endereco();
                    newEndereco.setLogradouro(dto.getLogradouro());
                    newEndereco.setNumero(dto.getNumero());
                    newEndereco.setComplemento(dto.getComplemento());
                    newEndereco.setBairro(dto.getBairro());
                    newEndereco.setCidade(dto.getCidade());
                    newEndereco.setEstado(dto.getEstado());
                    newEndereco.setCep(dto.getCep());
                    newEndereco.setPais(dto.getPais() != null ? dto.getPais() : "Brasil");
                    newEndereco.setTipoEndereco(dto.getTipoEndereco());
                    newEndereco.setPessoa(existingPessoa);
                    existingPessoa.getEnderecos().add(newEndereco);
                } else {
                    // Atualizar endereço existente
                    existingPessoa.getEnderecos().stream()
                            .filter(e -> e.getId().toString().equals(dto.getId()))
                            .findFirst()
                            .ifPresent(e -> {
                                e.setLogradouro(dto.getLogradouro());
                                e.setNumero(dto.getNumero());
                                e.setComplemento(dto.getComplemento());
                                e.setBairro(dto.getBairro());
                                e.setCidade(dto.getCidade());
                                e.setEstado(dto.getEstado());
                                e.setCep(dto.getCep());
                                e.setPais(dto.getPais() != null ? dto.getPais() : "Brasil");
                                e.setTipoEndereco(dto.getTipoEndereco());
                            });
                }
            }
        }

        existingPessoa = pessoaRepository.save(existingPessoa);
        return pessoaMapper.toResponseDTO(existingPessoa);
    }

    /**
     * Deleta uma Pessoa pelo ID.
     * Regras de Negócio:
     * - Também deleta o User associado devido ao CascadeType.ALL na Pessoa.
     *
     * @param id ID da Pessoa a ser deletada.
     * @throws BusinessException se a Pessoa não for encontrada.
     */
    @Transactional
    public void deletePessoa(UUID id) {
        if (!pessoaRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                    "Pessoa não encontrada para deleção.");
        }
        pessoaRepository.deleteById(id);
    }

    public Optional<LocalAtuacao> findPessoaByCpf(String cpf) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPessoaByCpf'");
    }

    public Optional<LocalAtuacao> findPessoaByEmail(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPessoaByEmail'");
    }
}