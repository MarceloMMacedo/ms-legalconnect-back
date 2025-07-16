package br.com.legalconnect.auth.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.com.legalconnect.auth.entity.Tenant;
import br.com.legalconnect.tenant.dto.TenantCreationRequest;
import br.com.legalconnect.tenant.dto.TenantResponseDTO;

/**
 * @interface TenantMapper
 * @brief Mapper para conversão entre a entidade `Tenant` e seus DTOs.
 *
 *        Utiliza MapStruct para gerar automaticamente o código de mapeamento,
 *        incluindo a conversão do enum `TenantStatus` para String.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TenantMapper {
    @Mapping(source = "status", target = "status")
    TenantResponseDTO toDto(Tenant entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true) // Status é gerenciado pelo serviço
    Tenant toEntity(TenantCreationRequest dto);

    List<TenantResponseDTO> toDtoList(List<Tenant> entities);

    default String mapTenantStatus(Tenant.TenantStatus status) {
        return status != null ? status.name() : null;
    }
}