package br.com.legalconnect.advogado.service;

import java.util.Base64; // Para decodificar Base64
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.advogado.domain.Documento;
import br.com.legalconnect.advogado.domain.Profissional;
import br.com.legalconnect.advogado.dto.request.DocumentoUploadRequest;
import br.com.legalconnect.advogado.dto.response.DocumentoResponseDTO;
import br.com.legalconnect.advogado.mapper.DocumentoMapper;
import br.com.legalconnect.advogado.repository.DocumentoRepository;
import br.com.legalconnect.advogado.repository.ProfissionalRepository;
import br.com.legalconnect.commom.service.S3Service;
import br.com.legalconnect.commom.service.TenantContext;
import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;
import jakarta.transaction.Transactional;

/**
 * Serviço responsável pela gestão de documentos de um Profissional.
 * Inclui o upload de arquivos para o S3 e o armazenamento dos metadados no
 * banco de dados.
 */
@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper documentoMapper;
    private final ProfissionalRepository profissionalRepository;
    private final S3Service s3Service; // Serviço para integração com S3

    @Autowired
    public DocumentoService(DocumentoRepository documentoRepository,
            DocumentoMapper documentoMapper,
            ProfissionalRepository profissionalRepository,
            S3Service s3Service) {
        this.documentoRepository = documentoRepository;
        this.documentoMapper = documentoMapper;
        this.profissionalRepository = profissionalRepository;
        this.s3Service = s3Service;
    }

    /**
     * Realiza o upload de um documento para o S3 e persiste seus metadados.
     * Regras de Negócio:
     * - O profissional deve existir e pertencer ao tenant atual.
     * - O conteúdo do arquivo em Base64 é decodificado e enviado ao S3.
     * - A URL do S3 é armazenada no banco de dados.
     *
     * @param profissionalId ID do profissional.
     * @param request        DTO com os dados do documento e o arquivo em Base64.
     * @return DTO do documento criado.
     * @throws BusinessException em caso de falha no upload ou se o profissional não
     *                           for encontrado.
     */
    @Transactional
    public DocumentoResponseDTO uploadDocumento(UUID profissionalId, DocumentoUploadRequest request) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Profissional profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO, HttpStatus.NOT_FOUND,
                        "Profissional não encontrado para upload de documento."));

        if (!profissional.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Profissional pertence a outro tenant.");
        }

        // Decodificar Base64 e fazer upload para S3
        byte[] fileBytes;
        try {
            fileBytes = Base64.getDecoder().decode(request.getArquivoBase64());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.INVALID_DOCUMENT_FORMAT, HttpStatus.BAD_REQUEST,
                    "Conteúdo do arquivo em Base64 inválido.");
        }

        String s3Key = "profissionais/" + profissionalId + "/documentos/" + UUID.randomUUID() + "/"
                + request.getNomeArquivo();
        String fileUrl = s3Service.uploadFile(fileBytes, s3Key, request.getMimeType());

        if (fileUrl == null) {
            throw new BusinessException(ErrorCode.DOCUMENT_UPLOAD_FAILED, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha ao fazer upload do documento para o S3.");
        }

        Documento documento = documentoMapper.toEntity(request);
        documento.setUrlS3(fileUrl);
        documento.setProfissional(profissional);
        documento.setTenantId(tenantId);

        documento = documentoRepository.save(documento);
        return documentoMapper.toResponseDTO(documento);
    }

    /**
     * Busca um documento pelo seu ID e pelo ID do profissional.
     * Regras de Negócio:
     * - O documento deve pertencer ao profissional e ao tenant correto.
     *
     * @param profissionalId ID do profissional.
     * @param documentoId    ID do documento.
     * @return DTO do documento encontrado.
     * @throws BusinessException se o documento não for encontrado.
     */
    public DocumentoResponseDTO findDocumentoById(UUID profissionalId, UUID documentoId) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Documento documento = documentoRepository.findByIdAndProfissionalId(documentoId, profissionalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Documento não encontrado para este profissional."));

        if (!documento.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Documento pertence a outro tenant ou profissional.");
        }

        return documentoMapper.toResponseDTO(documento);
    }

    /**
     * Lista todos os documentos de um profissional.
     * Regras de Negócio:
     * - Apenas documentos do tenant atual são retornados.
     *
     * @param profissionalId ID do profissional.
     * @return Lista de DTOs de documentos.
     */
    public List<DocumentoResponseDTO> findAllDocumentosByProfissionalId(UUID profissionalId) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Profissional profissional = profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFISSIONAL_NAO_ENCONTRADO, HttpStatus.NOT_FOUND,
                        "Profissional não encontrado."));
        if (!profissional.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Profissional pertence a outro tenant.");
        }

        return documentoRepository.findAllByProfissionalId(profissionalId).stream()
                .map(documentoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Deleta um documento pelo seu ID e pelo ID do profissional.
     * Regras de Negócio:
     * - O documento é removido do S3 antes de ser deletado do banco de dados.
     *
     * @param profissionalId ID do profissional.
     * @param documentoId    ID do documento a ser deletado.
     * @throws BusinessException se o documento não for encontrado ou falha na
     *                           deleção do S3.
     */
    @Transactional
    public void deleteDocumento(UUID profissionalId, UUID documentoId) {
        UUID tenantId = TenantContext.getCurrentTenantId();

        Documento existingDocumento = documentoRepository.findByIdAndProfissionalId(documentoId, profissionalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTIDADE_NAO_ENCONTRADA, HttpStatus.NOT_FOUND,
                        "Documento não encontrado para deleção."));

        if (!existingDocumento.getTenantId().equals(tenantId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS, HttpStatus.FORBIDDEN,
                    "Acesso negado. Documento pertence a outro tenant.");
        }

        // Deletar do S3 primeiro
        try {
            s3Service.deleteFile(existingDocumento.getUrlS3());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ERRO_INTERNO_SERVIDOR, HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha ao deletar o documento do S3: " + e.getMessage());
        }

        documentoRepository.deleteByIdAndProfissionalId(documentoId, profissionalId);
    }
}