package br.com.legalconnect.marketplace.depoimento.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import br.com.legalconnect.marketplace.depoimento.application.dto.DepoimentoRequestDTO;
import br.com.legalconnect.marketplace.depoimento.application.dto.DepoimentoResponseDTO;
import br.com.legalconnect.marketplace.depoimento.domain.enums.DepoimentoStatus; // Importado o novo enum
import br.com.legalconnect.marketplace.depoimento.domain.model.Depoimento;

/**
 * Mapper para conversão entre DTOs e a entidade Depoimento, utilizando
 * MapStruct.
 */
@Mapper(componentModel = "spring")
public interface DepoimentoMapper {

    /**
     * Converte uma entidade Depoimento para um DepoimentoResponseDTO.
     * 
     * @param entity A entidade Depoimento.
     * @return O DTO de resposta.
     */
    @Mapping(target = "tipoDepoimento", source = "entity.tipoDepoimento") // Mapeia enum TipoDepoimento para String
                                                                          // automaticamente
    @Mapping(target = "status", source = "entity.status") // Mapeia enum DepoimentoStatus para String automaticamente
    DepoimentoResponseDTO toResponse(Depoimento entity);

    /**
     * Converte um DepoimentoRequestDTO para uma entidade Depoimento.
     * Ignora o ID, createdAt e updatedAt, pois são gerados ou gerenciados pela
     * entidade/BaseEntity.
     * O status será gerenciado pela lógica de negócio no AppService, não
     * diretamente pelo mapper na criação.
     * 
     * @param dto O DTO de requisição.
     * @return A entidade Depoimento.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tipoDepoimento", expression = "java(TipoDepoimento.valueOf(dto.getTipoDepoimento()))") // Converte
                                                                                                              // String
                                                                                                              // para
                                                                                                              // Enum
    @Mapping(target = "status", ignore = true) // O status inicial é definido na lógica de negócio do AppService
    Depoimento toEntity(DepoimentoRequestDTO dto);

    /**
     * Atualiza uma entidade Depoimento a partir de um DepoimentoRequestDTO.
     * Ignora o ID, createdAt e updatedAt. O status é mapeado via método auxiliar
     * `mapStringToDepoimentoStatus`.
     * 
     * @param dto    O DTO de requisição.
     * @param entity A entidade a ser atualizada.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tipoDepoimento", expression = "java(TipoDepoimento.valueOf(dto.getTipoDepoimento()))")
    @Mapping(target = "status", source = "dto.status", qualifiedByName = "mapStringToDepoimentoStatus")
    void updateEntityFromDto(DepoimentoRequestDTO dto, @MappingTarget Depoimento entity);

    /**
     * Método auxiliar para mapear String para DepoimentoStatus.
     * Retorna null se a string for nula ou vazia, permitindo que a lógica de
     * negócio lide com defaults.
     * 
     * @param status String do status.
     * @return DepoimentoStatus ou null.
     */
    @Named("mapStringToDepoimentoStatus")
    default DepoimentoStatus mapStringToDepoimentoStatus(String status) {
        return (status != null && !status.isEmpty()) ? DepoimentoStatus.valueOf(status.toUpperCase()) : null;
    }
}