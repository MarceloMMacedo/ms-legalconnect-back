package br.com.legalconnect.commom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.legalconnect.common.exception.BusinessException;
import br.com.legalconnect.common.exception.ErrorCode;

/**
 * Serviço de integração simulada com AWS S3 para upload e deleção de arquivos.
 * Em um ambiente real, esta classe conteria a lógica de comunicação com a AWS
 * SDK.
 */
@Service
public class S3Service {

    private static final Logger log = LoggerFactory.getLogger(S3Service.class);
    private static final String MOCK_S3_BASE_URL = "https://mock-s3-bucket.s3.amazonaws.com/";

    /**
     * Simula o upload de um arquivo para o S3.
     *
     * @param fileBytes Os bytes do arquivo.
     * @param key       A chave/caminho do arquivo no bucket S3.
     * @param mimeType  O tipo MIME do arquivo.
     * @return A URL pública do arquivo no S3.
     * @throws BusinessException se ocorrer um erro durante o "upload" simulado.
     */
    public String uploadFile(byte[] fileBytes, String key, String mimeType) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new BusinessException(ErrorCode.DOCUMENT_UPLOAD_FAILED, HttpStatus.BAD_REQUEST,
                    "O conteúdo do arquivo não pode ser vazio.");
        }
        if (key == null || key.isEmpty()) {
            throw new BusinessException(ErrorCode.DOCUMENT_UPLOAD_FAILED, HttpStatus.BAD_REQUEST,
                    "A chave do S3 não pode ser vazia.");
        }

        // Simulação de upload: Loga a operação e retorna uma URL mock
        log.info("Simulando upload para S3. Chave: {}, Tamanho: {} bytes, Tipo: {}", key, fileBytes.length, mimeType);
        String fileUrl = MOCK_S3_BASE_URL + key;
        log.info("Upload simulado concluído. URL: {}", fileUrl);
        return fileUrl;
    }

    /**
     * Simula a deleção de um arquivo do S3.
     *
     * @param fileUrl A URL pública do arquivo a ser deletado.
     * @throws BusinessException se ocorrer um erro durante a "deleção" simulada.
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new BusinessException(ErrorCode.DADOS_INVALIDOS, HttpStatus.BAD_REQUEST,
                    "URL do arquivo não pode ser vazia para deleção.");
        }

        // Simulação de deleção: Loga a operação
        log.info("Simulando deleção de S3. URL: {}", fileUrl);
        // Em um ambiente real, aqui estaria a chamada para o AWS S3 SDK para deletar o
        // objeto.
        // Por exemplo: s3Client.deleteObject(bucketName, key);
        log.info("Deleção simulada concluída para URL: {}", fileUrl);
    }
}