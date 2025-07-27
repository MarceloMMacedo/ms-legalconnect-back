package br.com.legalconnect.marketplace.depoimento.application;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.legalconnect.common.dto.BaseResponse;
import br.com.legalconnect.marketplace.depoimento.application.dto.DepoimentoRequestDTO;
import br.com.legalconnect.marketplace.depoimento.application.dto.DepoimentoResponseDTO;
import br.com.legalconnect.marketplace.depoimento.application.service.DepoimentoAppService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para o módulo de Depoimentos.
 * Gerencia endpoints públicos e de administração (com segurança).
 */
@RestController
@RequiredArgsConstructor
public class DepoimentoController {

    private final DepoimentoAppService appService;

    // --- Endpoints Públicos ---

    /**
     * Lista depoimentos para exibição na página inicial, com opções de limite e
     * ordenação.
     * Exemplo: GET /api/v1/public/depoimentos?limit=5&random=true
     * 
     * @param limit  O número máximo de depoimentos a serem retornados (padrão: 5).
     * @param random Booleano para indicar se os depoimentos devem ser aleatórios
     *               (padrão: false).
     * @return ResponseEntity com a lista de depoimentos.
     */
    @GetMapping("/api/v1/public/depoimentos")
    public ResponseEntity<BaseResponse<List<DepoimentoResponseDTO>>> listarParaHome(
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "false") boolean random) {
        List<DepoimentoResponseDTO> depoimentos = appService.listarParaHome(limit, random);
        return ResponseEntity.ok(BaseResponse.<List<DepoimentoResponseDTO>>builder()
                .data(depoimentos)
                .message("Depoimentos listados com sucesso.")
                .build());
    }

    // --- Endpoints de Administração (requer ROLE_PLATAFORMA_ADMIN) ---

    /**
     * Cria um novo depoimento.
     * Exemplo: POST /api/v1/admin/depoimentos
     * 
     * @param request O DTO com os dados do depoimento a ser criado.
     * @return ResponseEntity com o depoimento criado e status 201 CREATED.
     */
    @PostMapping("/api/v1/admin/depoimentos")
    @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
    public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> criarDepoimento(
            @RequestBody @Valid DepoimentoRequestDTO request) {
        DepoimentoResponseDTO novoDepoimento = appService.criarDepoimento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.<DepoimentoResponseDTO>builder()
                .data(novoDepoimento)
                .message("Depoimento criado com sucesso.")
                .build());
    }

    /**
     * Atualiza um depoimento existente.
     * Exemplo: PUT /api/v1/admin/depoimentos/{id}
     * 
     * @param id      O ID do depoimento a ser atualizado.
     * @param request O DTO com os dados atualizados do depoimento.
     * @return ResponseEntity com o depoimento atualizado.
     */
    @PutMapping("/api/v1/admin/depoimentos/{id}")
    @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
    public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> atualizarDepoimento(
            @PathVariable UUID id,
            @RequestBody @Valid DepoimentoRequestDTO request) {
        DepoimentoResponseDTO depoimentoAtualizado = appService.atualizarDepoimento(id, request);
        return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                .data(depoimentoAtualizado)
                .message("Depoimento atualizado com sucesso.")
                .build());
    }

    /**
     * Exclui um depoimento pelo ID.
     * Exemplo: DELETE /api/v1/admin/depoimentos/{id}
     * 
     * @param id O ID do depoimento a ser excluído.
     * @return ResponseEntity com status 204 NO CONTENT.
     */
    @DeleteMapping("/api/v1/admin/depoimentos/{id}")
    @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
    public ResponseEntity<BaseResponse<Void>> excluirDepoimento(@PathVariable UUID id) {
        appService.excluirDepoimento(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(BaseResponse.<Void>builder()
                .message("Depoimento excluído com sucesso.")
                .build());
    }

    /**
     * Aprova um depoimento.
     * Exemplo: PATCH /api/v1/admin/depoimentos/{id}/aprovar
     * 
     * @param id O ID do depoimento a ser aprovado.
     * @return ResponseEntity com o depoimento aprovado.
     */
    @PatchMapping("/api/v1/admin/depoimentos/{id}/aprovar")
    @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
    public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> aprovarDepoimento(@PathVariable UUID id) {
        DepoimentoResponseDTO depoimentoAprovado = appService.aprovarDepoimento(id);
        return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                .data(depoimentoAprovado)
                .message("Depoimento aprovado com sucesso.")
                .build());
    }

    /**
     * Reprova um depoimento.
     * Exemplo: PATCH /api/v1/admin/depoimentos/{id}/reprovar
     * 
     * @param id O ID do depoimento a ser reprovado.
     * @return ResponseEntity com o depoimento reprovado.
     */
    @PatchMapping("/api/v1/admin/depoimentos/{id}/reprovar")
    @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
    public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> reprovarDepoimento(@PathVariable UUID id) {
        DepoimentoResponseDTO depoimentoReprovado = appService.reprovarDepoimento(id);
        return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                .data(depoimentoReprovado)
                .message("Depoimento reprovado com sucesso.")
                .build());
    }

    /**
     * Lista todos os depoimentos (uso administrativo).
     * Exemplo: GET /api/v1/admin/depoimentos
     * 
     * @return ResponseEntity com a lista de todos os depoimentos.
     */
    @GetMapping("/api/v1/admin/depoimentos")
    @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
    public ResponseEntity<BaseResponse<List<DepoimentoResponseDTO>>> listarTodosAdmin() {
        List<DepoimentoResponseDTO> depoimentos = appService.listarTodos();
        return ResponseEntity.ok(BaseResponse.<List<DepoimentoResponseDTO>>builder()
                .data(depoimentos)
                .message("Todos os depoimentos listados para administração.")
                .build());
    }

    /**
     * Busca um depoimento específico pelo ID (uso administrativo).
     * Exemplo: GET /api/v1/admin/depoimentos/{id}
     * 
     * @param id O ID do depoimento.
     * @return ResponseEntity com o depoimento encontrado.
     */
    @GetMapping("/api/v1/admin/depoimentos/{id}")
    @PreAuthorize("hasRole('PLATAFORMA_ADMIN')")
    public ResponseEntity<BaseResponse<DepoimentoResponseDTO>> buscarDepoimentoPorIdAdmin(@PathVariable UUID id) {
        DepoimentoResponseDTO depoimento = appService.buscarPorId(id);
        return ResponseEntity.ok(BaseResponse.<DepoimentoResponseDTO>builder()
                .data(depoimento)
                .message("Depoimento encontrado.")
                .build());
    }
}

// ```sql--src/main/resources/db/migration/V1__create_tb_depoimento.sql

// CREATE TABLE
// IF NOT

// EXISTS tb_depoimento (
// id UUID PRIMARY KEY

// DEFAULT gen_random_uuid(), -- ID gerado automaticamente como UUID

// texto VARCHAR(500) NOT NULL,

// nome VARCHAR(100) NOT NULL,

// local VARCHAR(100),

// foto_url VARCHAR(255),
// user_id UUID NOT NULL, -- Novo campo para associar

// ao usuário (cliente ou profissional)

// tipo_depoimento VARCHAR(20) NOT NULL, -- Novo campo para indicar

// o tipo (CLIENTE/PROFISSIONAL)

// status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE', -- Novo campo para o status
// do depoimento
// created_at TIMESTAMP NOT NULL

// DEFAULT NOW(),
// updated_at TIMESTAMP NOT NULL

// DEFAULT NOW()
// );
// ```xml
// <!-- pom.xml (fragmento - certifique-se de adicionar ao seu pom.xml
// existente) -->
// <?xml version="1.0" encoding="UTF-8"?>
// <project
// xmlns="[http://maven.apache.org/POM/4.0.0](http://maven.apache.org/POM/4.0.0)"
// xmlns:xsi="[http://www.w3.org/2001/XMLSchema-instance](http://www.w3.org/2001/XMLSchema-instance)"
// xsi:schemaLocation="[http://maven.apache.org/POM/4.0.0](http://maven.apache.org/POM/4.0.0)
// [https://maven.apache.org/xsd/maven-4.0.0.xsd](https://maven.apache.org/xsd/maven-4.0.0.xsd)">
// <modelVersion>4.0.0</modelVersion>
// <parent>
// <groupId>org.springframework.boot</groupId>
// <artifactId>spring-boot-starter-parent</artifactId>
// <version>3.3.1</version> <!-- Use a versão mais recente do Spring Boot 3.x
// -->
// <relativePath/> <!-- lookup parent from repository -->
// </parent>
// <groupId>br.com.legalconnect</groupId>
// <artifactId>legalconnect-monolito</artifactId>
// <version>0.0.1-SNAPSHOT</version>
// <name>legalconnect-monolito</name>
// <description>Projeto monolito LegalConnect</description>

// <properties>
// <java.version>17</java.version>
// <org.mapstruct.version>1.5.5.Final</org.mapstruct.version> <!-- Versão do
// MapStruct -->
// </properties>

// <dependencies>
// <!-- Spring Boot Starter Web -->
// <dependency>
// <groupId>org.springframework.boot</groupId>
// <artifactId>spring-boot-starter-web</artifactId>
// </dependency>

// <!-- Spring Data JPA -->
// <dependency>
// <groupId>org.springframework.boot</groupId>
// <artifactId>spring-boot-starter-data-jpa</artifactId>
// </dependency>

// <!-- PostgreSQL Driver -->
// <dependency>
// <groupId>org.postgresql</groupId>
// <artifactId>postgresql</artifactId>
// <scope>runtime</scope>
// </dependency>

// <!-- Lombok -->
// <dependency>
// <groupId>org.projectlombok</groupId>
// <artifactId>lombok</artifactId>
// <optional>true</optional>
// </dependency>

// <!-- Hibernate Validator -->
// <dependency>
// <groupId>org.springframework.boot</groupId>
// <artifactId>spring-boot-starter-validation</artifactId>
// </dependency>

// <!--

// Spring Security (necessário para @PreAuthorize) -->
// <dependency>
// <groupId>org.springframework.boot</groupId>
// <artifactId>spring-boot-starter-security</artifactId>
// </dependency>

// <!-- MapStruct Core -->
// <dependency>
// <groupId>org.mapstruct</groupId>
// <artifactId>mapstruct</artifactId>
// <version>${org.mapstruct.version}</version>
// </dependency>

// <!--

// MapStruct Processor (para geração de código durante a compilação) -->
// <dependency>
// <groupId>org.mapstruct</groupId>
// <artifactId>mapstruct-processor</artifactId>
// <version>${org.mapstruct.version}</version>
// <scope>provided</scope>
// </dependency>

// <!-- Lombok MapStruct

// integration (importante para Lombok + MapStruct juntos) -->
// <dependency>
// <groupId>org.projectlombok</groupId>
// <artifactId>lombok-mapstruct-binding</artifactId>
// <version>0.2.0</version> <!-- Verifique a versão mais recente compatível -->
// <scope>provided</scope>
// </dependency>

// <!-- Spring Boot Test -->
// <dependency>
// <groupId>org.springframework.boot</groupId>
// <artifactId>spring-boot-starter-test</artifactId>
// <scope>test</scope>
// </dependency>

// <!-- Mockito (já vem com spring-boot-starter-test, mas bom ter explícito se
// precisar de features específicas) -->
// <dependency>
// <groupId>org.mockito</groupId>
// <artifactId>mockito-core</artifactId>
// <scope>test</scope>
// </dependency>
// <dependency>
// <groupId>org.mockito</groupId>
// <artifactId>mockito-junit-jupiter</artifactId>
// <scope>test</scope>
// </dependency>
// <dependency>
// <groupId>org.springframework.security</groupId>
// <artifactId>spring-security-test</artifactId>
// <scope>test</scope>
// </dependency>
// </dependencies>

// <build>
// <plugins>
// <plugin>
// <groupId>org.springframework.boot</groupId>
// <artifactId>spring-boot-maven-plugin</artifactId>
// <configuration>
// <excludes>
// <exclude>
// <groupId>org.projectlombok</groupId>
// <artifactId>lombok</artifactId>
// </exclude>
// </excludes>
// </configuration>
// </plugin>
// <!-- Plugin para o MapStruct processor -->
// <plugin>
// <groupId>org.apache.maven.plugins</groupId>
// <artifactId>maven-compiler-plugin</artifactId>
// <version>3.8.1</version> <!-- Use uma versão compatível com sua versão do
// Java -->
// <configuration>
// <annotationProcessorPaths>
// <path>
// <groupId>org.mapstruct</groupId>
// <artifactId>mapstruct-processor</artifactId>
// <version>${org.mapstruct.version}</version>
// </path>
// <path>
// <groupId>org.projectlombok</groupId>
// <artifactId>lombok</artifactId>
// <version>${lombok.version}</version>
// </path>
// <path>
// <groupId>org.projectlombok</groupId>
// <artifactId>lombok-mapstruct-binding</artifactId>
// <version>0.2.0</version>
// </path>
// </annotationProcessorPaths>
// <compilerArgs>
// <arg>-Amapstruct.defaultComponentModel=spring</arg>
// </compilerArgs>
// </configuration>
// </plugin>
// </plugins>
// </build>
// </project>
// ```yaml
// # src/main/resources/application.yml
// spring:
// datasource:
// url: jdbc:postgresql://localhost:5432/legalconnect_db
// username: postgres
// password: sua-senha-do-banco # ATENÇÃO: Substitua 'sua-senha-do-banco' pela
// senha real do seu PostgreSQL
// driver-class-name: org.postgresql.Driver
// jpa:
// hibernate:
// ddl-auto: validate # Não usar 'update' ou 'create-drop' em produção!
// 'validate' é seguro para checar schema.
// show-sql: true # Exibe as queries SQL geradas pelo Hibernate no console
// properties:
// hibernate:
// dialect: org.hibernate.dialect.PostgreSQLDialect # Dialeto específico para
// PostgreSQL
// format_sql: true # Formata o SQL exibido no console
// # Configurações para habilitar a segurança (necessário para @PreAuthorize e
// JWT)
// security:
// oauth2:
// resourceserver:
// jwt:
// # Sua configuração de JWT aqui. Exemplo simplificado para fins de
// compilação/teste local.
// # Em um ambiente real, isso seria um JWK Set URI ou uma chave pública do seu
// provedor de identidade.
// issuer-uri: http://localhost:8080/auth/realms/legalconnect # Exemplo, ajuste
// conforme seu provedor de segurança
// logging:
// level:
// org.springframework.security: DEBUG # Habilita logs de debug para Spring
// Security para facilitar o entendimento
// org.hibernate.SQL: DEBUG # Habilita logs de SQL para Hibernate
// org.hibernate.type.descriptor.sql.BasicBinder: TRACE # Exibe os parâmetros
// das queries SQL