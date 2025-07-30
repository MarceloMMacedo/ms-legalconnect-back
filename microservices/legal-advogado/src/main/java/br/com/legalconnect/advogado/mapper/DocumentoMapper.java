package br.com.legalconnect.advogado.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import br.com.legalconnect.advogado.domain.Documento;
import br.com.legalconnect.advogado.dto.enums.DocumentoTipo;
import br.com.legalconnect.advogado.dto.request.DocumentoUploadRequest;
import br.com.legalconnect.advogado.dto.response.DocumentoResponseDTO;

/**
 * Mapper MapStruct para a entidade Documento e seus DTOs.
 * Gerencia a conversão entre DocumentoUploadRequest, Documento e
 * DocumentoResponseDTO.
 */
@Mapper(componentModel = "spring")
public interface DocumentoMapper {
    DocumentoMapper INSTANCE = Mappers.getMapper(DocumentoMapper.class);

    /**
     * Mapeia um DocumentoUploadRequest para uma entidade Documento.
     * Ignora 'arquivoBase64' e 'mimeType' (dados para upload, não persistentes na
     * entidade).
     * 'urlS3', 'profissional' e 'tenantId' devem ser setados no serviço.
     *
     * @param dto O DTO de requisição para upload de documento.
     * @return A entidade Documento correspondente.
     */
    @Mapping(target = "urlS3", ignore = true) // Preenchido após o upload bem-sucedido
    @Mapping(target = "profissional", ignore = true) // Relacionamento com Profissional, preenchido no serviço
    @Mapping(target = "tenantId", ignore = true) // TenantId, preenchido no serviço
    @Mapping(source = "tipoDocumento", target = "tipoDocumento", qualifiedByName = "mapDocumentoTipoToString")
    Documento toEntity(DocumentoUploadRequest dto);

    /**
     * Mapeia uma entidade Documento para um DocumentoResponseDTO.
     *
     * @param entity A entidade Documento.
     * @return O DTO de resposta correspondente.
     */
    @Mapping(source = "tipoDocumento", target = "tipoDocumento") // Mapeamento direto de String para String
    DocumentoResponseDTO toResponseDTO(Documento entity);

    /**
     * Atualiza uma entidade Documento existente com os dados de um
     * DocumentoUploadRequest.
     * Campos ignorados: 'id', 'urlS3', 'profissional', 'tenantId'.
     *
     * @param dto    O DTO de requisição.
     * @param entity A entidade Documento a ser atualizada.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "urlS3", ignore = true)
    @Mapping(target = "profissional", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(source = "tipoDocumento", target = "tipoDocumento", qualifiedByName = "mapDocumentoTipoToString")
    void updateEntityFromDto(DocumentoUploadRequest dto, @MappingTarget Documento entity);

    /**
     * Converte um enum DocumentoTipo para sua representação em String.
     * Usado para mapear de DTO (enum) para Entidade (String).
     *
     * @param tipo O enum DocumentoTipo.
     * @return A representação em String do enum.
     */
    @Named("mapDocumentoTipoToString")
    default String mapDocumentoTipoToString(DocumentoTipo tipo) {
        return tipo != null ? tipo.name() : null;
    }
}