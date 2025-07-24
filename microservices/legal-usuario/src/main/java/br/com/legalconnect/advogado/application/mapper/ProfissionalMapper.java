package br.com.legalconnect.advogado.application.mapper;

import java.util.List;
import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import br.com.legalconnect.advogado.application.dto.request.AtualizacaoPerfilRequest;
import br.com.legalconnect.advogado.application.dto.request.CertificacaoRequestDTO;
import br.com.legalconnect.advogado.application.dto.request.ExperienciaProfissionalRequestDTO;
import br.com.legalconnect.advogado.application.dto.request.FormacaoAcademicaRequestDTO;
import br.com.legalconnect.advogado.application.dto.request.ProfissionalCreateRequest;
import br.com.legalconnect.advogado.application.dto.response.CertificacaoResponseDTO;
import br.com.legalconnect.advogado.application.dto.response.ExperienciaProfissionalResponseDTO;
import br.com.legalconnect.advogado.application.dto.response.FormacaoAcademicaResponseDTO;
import br.com.legalconnect.advogado.application.dto.response.ProfissionalDetalhadoResponse;
import br.com.legalconnect.advogado.domain.modal.entity.CertificacaoEntity;
import br.com.legalconnect.advogado.domain.modal.entity.ExperienciaProfissionalEntity;
import br.com.legalconnect.advogado.domain.modal.entity.FormacaoAcademicaEntity;
import br.com.legalconnect.entity.Profissional;

@Mapper(componentModel = "spring", uses = { CertificacaoMapper.class, ExperienciaMapper.class, FormacaoMapper.class })
public interface ProfissionalMapper {

    // Mapeia ProfissionalCreateRequest para o modelo de domínio Profissional
    @Mapping(target = "id", ignore = true) // ID será gerado no domínio/persistência
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "statusProfissional", constant = "EM_ANALISE") // Status inicial padrão
    @Mapping(target = "usaMarketplace", constant = "false")
    @Mapping(target = "fazParteDePlano", constant = "false")
    // @Mapping(target = "certificacoes", ignore = true) // Coleções serão tratadas
    // separadamente ou no serviço
    // @Mapping(target = "documentos", ignore = true)
    // @Mapping(target = "experiencias", ignore = true)
    // @Mapping(target = "formacoes", ignore = true)
    // @Mapping(target = "locaisAtuacaoIds", ignore = true)
    // @Mapping(target = "areaAtuacaoIds", ignore = true)
    // @Mapping(target = "idiomaIds", ignore = true)
    // @Mapping(target = "tipoAtendimentoIds", ignore = true)
    // @Mapping(target = "roleProfissionalIds", ignore = true)
    // @Mapping(source = "planoId", target = "planoId")
    // @Mapping(source = "tenantId", target = "tenantId")
    // Note: pessoaId e empresaId não são mapeados diretamente aqui, pois virão de
    // outros serviços
    Profissional toDomainModel(ProfissionalCreateRequest request);

    // Atualiza um modelo de domínio Profissional a partir de
    // AtualizacaoPerfilRequest
    @Mapping(target = "id", ignore = true) // ID não é atualizável
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "statusProfissional", ignore = true) // Status é atualizado por métodos de negócio
    @Mapping(target = "numeroOab", ignore = true) // OAB geralmente não é atualizável após o cadastro
    // @Mapping(target = "certificacoes", ignore = true) // Coleções tratadas por
    // mappers específicos ou no serviço
    // @Mapping(target = "documentos", ignore = true)
    // @Mapping(target = "experiencias", ignore = true)
    // @Mapping(target = "formacoes", ignore = true)
    // @Mapping(target = "pessoaId", ignore = true)
    // @Mapping(target = "tenantId", ignore = true)
    // // Mapeamento de coleções de UUIDs
    // @Mapping(source = "locaisAtuacaoIds", target = "locaisAtuacaoIds")
    // @Mapping(source = "areaAtuacaoIds", target = "areaAtuacaoIds")
    // @Mapping(source = "idiomaIds", target = "idiomaIds")
    // @Mapping(source = "tipoAtendimentoIds", target = "tipoAtendimentoIds")
    void updateDomainModelFromRequest(AtualizacaoPerfilRequest request, @MappingTarget Profissional profissional);

    // Mapeia o modelo de domínio Profissional para ProfissionalDetalhadoResponse
    @Mapping(source = "statusProfissional", target = "statusProfissional") // Mapeia o enum para String
    // @Mapping(source = "certificacoes", target = "certificacoes")
    // @Mapping(source = "documentos", target = "documentos")
    // @Mapping(source = "experiencias", target = "experiencias")
    // @Mapping(source = "formacoes", target = "formacoes")
    ProfissionalDetalhadoResponse toDetalhadoResponse(Profissional profissional);

    // Mapeamento de coleções aninhadas
    Set<CertificacaoEntity> mapCertificacaoRequestDTOsToCertificacoes(List<CertificacaoRequestDTO> dtos);

    Set<ExperienciaProfissionalEntity> mapExperienciaRequestDTOsToExperiencias(
            List<ExperienciaProfissionalRequestDTO> dtos);

    Set<FormacaoAcademicaEntity> mapFormacaoRequestDTOsToFormacoes(List<FormacaoAcademicaRequestDTO> dtos);

    List<CertificacaoResponseDTO> mapCertificacoesToCertificacaoResponseDTOs(Set<CertificacaoEntity> certificacoes);

    List<ExperienciaProfissionalResponseDTO> mapExperienciasToExperienciaResponseDTOs(
            Set<ExperienciaProfissionalEntity> experiencias);

    List<FormacaoAcademicaResponseDTO> mapFormacoesToFormacaoResponseDTOs(Set<FormacaoAcademicaEntity> formacoes);
}