package br.com.legalconnect.advogado.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import br.com.legalconnect.advogado.application.dto.enums.DocumentoTipo;
import br.com.legalconnect.advogado.application.dto.request.DocumentoUploadRequest;
import br.com.legalconnect.advogado.application.dto.response.DocumentoResponseDTO;
import br.com.legalconnect.advogado.domain.modal.entity.DocumentoEntity;

@Mapper(componentModel = "spring")
public interface DocumentoMapper {

    // Mapeia DocumentoUploadRequest para o modelo de domínio Documento
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "tipoDocumento", target = "tipoDocumento", qualifiedByName = "mapStringToDocumentoTipo")
    DocumentoEntity toDomainModel(DocumentoUploadRequest request);

    // Mapeia o modelo de domínio Documento para DocumentoResponseDTO
    @Mapping(source = "tipoDocumento", target = "tipoDocumento", qualifiedByName = "mapDocumentoTipoToString")
    DocumentoResponseDTO toResponseDTO(DocumentoEntity documento);

    @Named("mapStringToDocumentoTipo")
    default DocumentoTipo mapStringToDocumentoTipo(String tipoDocumento) {
        if (tipoDocumento == null) {
            return null;
        }
        try {
            return DocumentoTipo.valueOf(tipoDocumento.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Tratar caso de tipo de documento inválido, talvez lançar uma exceção de
            // validação
            return DocumentoTipo.OUTRO; // Ou outro tratamento de erro
        }
    }

    @Named("mapDocumentoTipoToString")
    default String mapDocumentoTipoToString(DocumentoTipo tipoDocumento) {
        return tipoDocumento != null ? tipoDocumento.name() : null;
    }
}