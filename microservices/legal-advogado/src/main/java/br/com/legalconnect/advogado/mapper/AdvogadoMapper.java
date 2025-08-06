package br.com.legalconnect.advogado.mapper;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.legalconnect.advogado.domain.Profissional;
import br.com.legalconnect.advogado.repository.AreaAtuacaoRepository;
import br.com.legalconnect.advogado.repository.LocalAtuacaoRepository;
import br.com.legalconnect.advogado.repository.TipoAtendimentoRepository;
import br.com.legalconnect.commom.model.Endereco;
import br.com.legalconnect.perfilcardadvogado.dto.response.AdvogadoResponseDTO; // Import atualizado
import br.com.legalconnect.perfilcardadvogado.dto.response.AdvogadoResponseDTO.MetricasDTO; // Import atualizado
import br.com.legalconnect.perfilcardadvogado.dto.response.AdvogadoResponseDTO.ServicoDTO; // Import atualizado

/**
 * Mapper MapStruct para converter a entidade Profissional em
 * AdvogadoResponseDTO.
 * Lida com a agregação de dados de outras entidades para formar o DTO final.
 */
@Mapper(componentModel = "spring")
public abstract class AdvogadoMapper {

    @Autowired
    protected AreaAtuacaoRepository areaAtuacaoRepository;
    @Autowired
    protected LocalAtuacaoRepository localAtuacaoRepository;
    @Autowired
    protected TipoAtendimentoRepository tipoAtendimentoRepository;

    @Mapping(source = "id", target = "id")
    @Mapping(source = "numeroOab", target = "oab")
    @Mapping(source = "fazParteDePlano", target = "fazParteDePlano")
    @Mapping(source = "usuario.nomeCompleto", target = "nome")
    @Mapping(source = "usuario.fotoUrl", target = "fotoUrl")
    @Mapping(source = "plano.nome", target = "nomePlano") // Mapeia o nome do plano
    // Mapeamentos para campos que precisam de lógica customizada ou agregação
    @Mapping(target = "avaliacao", expression = "java(calcularMediaAvaliacao(profissional))")
    @Mapping(target = "numAvaliacoes", expression = "java(calcularNumAvaliacoes(profissional))")
    @Mapping(target = "bio", expression = "java(getBio(profissional))")
    @Mapping(target = "especialidades", expression = "java(mapAreaAtuacaoIdsToNomes(profissional.getAreaAtuacaoIds()))")
    @Mapping(target = "localizacao", expression = "java(mapLocalizacao(profissional))")
    @Mapping(target = "estado", expression = "java(mapEstado(profissional))")
    @Mapping(target = "municipio", expression = "java(mapMunicipio(profissional))")
    @Mapping(target = "verificadoOAB", expression = "java(isOABVerificada(profissional))")
    @Mapping(target = "nivel", expression = "java(getNivelProfissional(profissional))")
    @Mapping(target = "formacao", expression = "java(getUltimaFormacao(profissional))")
    @Mapping(target = "metricas", expression = "java(mapMetricas(profissional))")
    @Mapping(target = "servicos", expression = "java(mapServicos(profissional))")
    public abstract AdvogadoResponseDTO toAdvogadoResponseDTO(Profissional profissional);

    // Métodos de mapeamento customizados

    @Named("calcularMediaAvaliacao")
    protected Double calcularMediaAvaliacao(Profissional profissional) {
        // Lógica para calcular a média de avaliação.
        // Assumindo que a entidade Profissional não tem um campo de avaliação direta,
        // isso viria de um relacionamento com Avaliacoes.
        // Por simplicidade, retornamos um valor fixo ou aleatório.
        return 4.5; // Exemplo
    }

    @Named("calcularNumAvaliacoes")
    protected Integer calcularNumAvaliacoes(Profissional profissional) {
        // Lógica para obter o número de avaliações.
        return 120; // Exemplo
    }

    @Named("getBio")
    protected String getBio(Profissional profissional) {
        // Assumindo que a bio pode estar no campo 'descricao' da Pessoa ou em um campo
        // específico do Profissional.
        // Se não houver, pode ser uma string vazia ou null.
        return "Advogado com 10 anos de experiência em Direito de Família e Sucessões."; // Exemplo
    }

    @Named("mapAreaAtuacaoIdsToNomes")
    protected List<String> mapAreaAtuacaoIdsToNomes(Set<UUID> areaAtuacaoIds) {
        if (areaAtuacaoIds == null || areaAtuacaoIds.isEmpty()) {
            return List.of();
        }
        return areaAtuacaoRepository.findAllById(areaAtuacaoIds).stream()
                .map(area -> area.getNome())
                .collect(Collectors.toList());
    }

    @Named("mapLocalizacao")
    protected String mapLocalizacao(Profissional profissional) {
        // Retorna a primeira cidade/estado do endereço principal do profissional
        return profissional.getEnderecos().stream()
                .filter(e -> e.getTipoEndereco() == Endereco.TipoEndereco.ESCRITORIO
                        || e.getTipoEndereco() == Endereco.TipoEndereco.COMERCIAL)
                .findFirst()
                .map(e -> e.getCidade() + " - " + e.getEstado())
                .orElse("Não informada");
    }

    @Named("mapEstado")
    protected String mapEstado(Profissional profissional) {
        return profissional.getEnderecos().stream()
                .filter(e -> e.getTipoEndereco() == Endereco.TipoEndereco.ESCRITORIO
                        || e.getTipoEndereco() == Endereco.TipoEndereco.COMERCIAL)
                .findFirst()
                .map(e -> e.getEstado())
                .orElse(null);
    }

    @Named("mapMunicipio")
    protected String mapMunicipio(Profissional profissional) {
        return profissional.getEnderecos().stream()
                .filter(e -> e.getTipoEndereco() == Endereco.TipoEndereco.ESCRITORIO
                        || e.getTipoEndereco() == Endereco.TipoEndereco.COMERCIAL)
                .findFirst()
                .map(e -> e.getCidade())
                .orElse(null);
    }

    @Named("isOABVerificada")
    protected Boolean isOABVerificada(Profissional profissional) {
        // Simulação de verificação da OAB. Em um cenário real, isso envolveria
        // integração com um serviço externo ou um processo de validação interno.
        return true; // Exemplo: sempre true para advogados cadastrados
    }

    @Named("getNivelProfissional")
    protected String getNivelProfissional(Profissional profissional) {
        // Lógica para determinar o nível (Júnior, Pleno, Sênior) com base na
        // experiência,
        // tempo de OAB, ou algum campo específico na entidade Profissional.
        return "Sênior"; // Exemplo
    }

    @Named("getUltimaFormacao")
    protected String getUltimaFormacao(Profissional profissional) {
        // Retorna o curso da última formação acadêmica (mais recente)
        return profissional.getFormacoes().stream()
                .sorted((f1, f2) -> f2.getDataConclusao().compareTo(f1.getDataConclusao()))
                .findFirst()
                .map(f -> f.getCurso() + " em " + f.getInstituicao())
                .orElse("Não informada");
    }

    @Named("mapMetricas")
    protected MetricasDTO mapMetricas(Profissional profissional) {
        // Simulação de métricas. Em um cenário real, isso viria de dados de
        // performance.
        return MetricasDTO.builder()
                .satisfacao(0.95) // Exemplo: 95% de satisfação
                .casosConcluidos(50) // Exemplo: 50 casos concluídos
                .build();
    }

    @Named("mapServicos")
    protected List<ServicoDTO> mapServicos(Profissional profissional) {
        // Mapeia os tipos de atendimento para serviços.
        // Em um cenário real, haveria uma entidade 'Servico' com nome, descrição e
        // preço.
        return profissional.getTipoAtendimentoIds().stream()
                .map(id -> tipoAtendimentoRepository.findById(id).orElse(null))
                .filter(java.util.Objects::nonNull)
                .map(tipo -> ServicoDTO.builder()
                        .nome(tipo.getNome())
                        .descricao("Serviço de " + tipo.getNome().toLowerCase())
                        .preco("R$ 250,00") // Preço fixo para exemplo
                        .build())
                .collect(Collectors.toList());
    }
}