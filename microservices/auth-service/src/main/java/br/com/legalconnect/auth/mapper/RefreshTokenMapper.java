package br.com.legalconnect.auth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import br.com.legalconnect.auth.dto.RefreshTokenResponseDTO;
import br.com.legalconnect.auth.entity.RefreshToken;

/**
 * @interface RefreshTokenMapper
 * @brief Mapper para conversão entre a entidade `RefreshToken` e seu DTO de
 *        resposta.
 *
 *        Utiliza MapStruct para gerar automaticamente o código de mapeamento.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RefreshTokenMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "expiraEm", target = "expiresAt")
    RefreshTokenResponseDTO toDto(RefreshToken entity);
}