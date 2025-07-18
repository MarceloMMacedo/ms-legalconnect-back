-- src/main/resources/db/migration/V1__Create_All_Tables.sql

-- Cria a tabela tb_tenant
CREATE TABLE tb_tenant (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, -- Corrigido para created_at
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, -- Corrigido para updated_at
    nome VARCHAR(255) NOT NULL,
    schema_name VARCHAR(63) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL
);

COMMENT ON TABLE tb_tenant IS 'Entidade que representa um tenant (ambiente isolado para escritórios/advogados).';
COMMENT ON COLUMN tb_tenant.id IS 'Identificador único do tenant.';
COMMENT ON COLUMN tb_tenant.created_at IS 'Timestamp da criação do registro.';
COMMENT ON COLUMN tb_tenant.updated_at IS 'Timestamp da última atualização do registro.';
COMMENT ON COLUMN tb_tenant.nome IS 'Nome descritivo do tenant (ex: "JusPlatform Principal").';
COMMENT ON COLUMN tb_tenant.schema_name IS 'Nome do esquema do banco de dados para multitenancy.';
COMMENT ON COLUMN tb_tenant.status IS 'Status operacional atual do tenant (ex: ACTIVE, INACTIVE, PENDING_ACTIVATION, SUSPENDED).';

-- Índices para tb_tenant
CREATE INDEX idx_tenant_schema_name ON tb_tenant (schema_name);
CREATE INDEX idx_tenant_status ON tb_tenant (status);


-- Tabela tb_role
CREATE TABLE tb_role (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, -- Corrigido para created_at
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, -- Corrigido para updated_at
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT
);

COMMENT ON TABLE tb_role IS 'Entidade para definir os papéis de acesso do usuário no sistema.';
COMMENT ON COLUMN tb_role.id IS 'Identificador único do papel.';
COMMENT ON COLUMN tb_role.created_at IS 'Timestamp da criação do registro.';
COMMENT ON COLUMN tb_role.updated_at IS 'Timestamp da última atualização do registro.';
COMMENT ON COLUMN tb_role.nome IS 'Nome descritivo e único do papel (ex: CLIENTE, ADVOGADO).';
COMMENT ON COLUMN tb_role.descricao IS 'Uma descrição detalhada do papel.';

-- Índices para tb_role
CREATE INDEX idx_role_nome ON tb_role (nome);


-- Tabela tb_user
CREATE TABLE tb_user (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, -- Corrigido para created_at
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, -- Corrigido para updated_at
    tenant_id UUID NOT NULL, -- Chave estrangeira para tb_tenant
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    senha_hash TEXT NOT NULL,
    foto_url TEXT,
    user_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant (id)
);

COMMENT ON TABLE tb_user IS 'Entidade base para todos os usuários (clientes, advogados, administradores de tenant, etc.).';
COMMENT ON COLUMN tb_user.id IS 'Identificador único do usuário.';
COMMENT ON COLUMN tb_user.created_at IS 'Timestamp da criação do registro.';
COMMENT ON COLUMN tb_user.updated_at IS 'Timestamp da última atualização do registro.';
COMMENT ON COLUMN tb_user.tenant_id IS 'Identificador do tenant ao qual o usuário pertence.';
COMMENT ON COLUMN tb_user.nome_completo IS 'Nome completo do usuário.';
COMMENT ON COLUMN tb_user.email IS 'Endereço de e-mail principal do usuário (único por schema de tenant).';
COMMENT ON COLUMN tb_user.cpf IS 'Número do Cadastro de Pessoa Física do usuário (único por schema de tenant).';
COMMENT ON COLUMN tb_user.telefone IS 'Número de telefone de contato do usuário.';
COMMENT ON COLUMN tb_user.senha_hash IS 'Representação criptografada (hashed) da senha do usuário.';
COMMENT ON COLUMN tb_user.foto_url IS 'URL da foto de perfil do usuário no S3.';
COMMENT ON COLUMN tb_user.user_type IS 'Categoria principal do usuário (ex: CLIENTE, ADVOGADO, PLATAFORMA_ADMIN).';
COMMENT ON COLUMN tb_user.status IS 'Status atual da conta do usuário (ex: ACTIVE, INACTIVE, PENDING_APPROVAL, REJECTED).';

-- Índices para tb_user
CREATE INDEX idx_user_tenant_id ON tb_user (tenant_id);
CREATE INDEX idx_user_email ON tb_user (email);
CREATE INDEX idx_user_cpf ON tb_user (cpf);
CREATE INDEX idx_user_type ON tb_user (user_type);
CREATE INDEX idx_user_status ON tb_user (status);


-- Tabela de junção tb_user_role
CREATE TABLE tb_user_role (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES tb_user (id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES tb_role (id)
);

COMMENT ON TABLE tb_user_role IS 'Tabela de junção para o relacionamento muitos-para-muitos entre User e Role.';
COMMENT ON COLUMN tb_user_role.user_id IS 'ID do usuário.';
COMMENT ON COLUMN tb_user_role.role_id IS 'ID do papel.';

-- Índices para tb_user_role
CREATE INDEX idx_user_role_user_id ON tb_user_role (user_id);
CREATE INDEX idx_user_role_role_id ON tb_user_role (role_id);


-- Tabela tb_refresh_token
CREATE TABLE tb_refresh_token (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, -- Corrigido para created_at
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL, -- Corrigido para updated_at
    user_id UUID NOT NULL UNIQUE,
    token TEXT NOT NULL UNIQUE,
    expira_em TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES tb_user (id)
);

COMMENT ON TABLE tb_refresh_token IS 'Entidade que representa o Refresh Token no banco de dados.';
COMMENT ON COLUMN tb_refresh_token.id IS 'Identificador único do refresh token.';
COMMENT ON COLUMN tb_refresh_token.created_at IS 'Timestamp da criação do registro.';
COMMENT ON COLUMN tb_refresh_token.updated_at IS 'Timestamp da última atualização do registro.';
COMMENT ON COLUMN tb_refresh_token.user_id IS 'Usuário ao qual este refresh token está associado.';
COMMENT ON COLUMN tb_refresh_token.token IS 'O valor real do refresh token.';
COMMENT ON COLUMN tb_refresh_token.expira_em IS 'Data e hora em que este refresh token se tornará inválido.';

-- Índices para tb_refresh_token
CREATE INDEX idx_refresh_token_user_id ON tb_refresh_token (user_id);
CREATE INDEX idx_refresh_token_expira_em ON tb_refresh_token (expira_em);

-- Tabela tb_password_reset_token
CREATE TABLE tb_password_reset_token (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    token TEXT NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    expira_em TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    usado BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES tb_user (id)
);

COMMENT ON TABLE tb_password_reset_token IS 'Entidade que representa um token de redefinição de senha.';
COMMENT ON COLUMN tb_password_reset_token.id IS 'Identificador único do token de redefinição.';
COMMENT ON COLUMN tb_password_reset_token.created_at IS 'Timestamp da criação do registro.';
COMMENT ON COLUMN tb_password_reset_token.updated_at IS 'Timestamp da última atualização do registro.';
COMMENT ON COLUMN tb_password_reset_token.token IS 'O valor real do token de redefinição.';
COMMENT ON COLUMN tb_password_reset_token.user_id IS 'Usuário ao qual este token de redefinição está associado.';
COMMENT ON COLUMN tb_password_reset_token.expira_em IS 'Data e hora em que este token se tornará inválido.';
COMMENT ON COLUMN tb_password_reset_token.usado IS 'Indica se o token já foi utilizado para redefinir a senha.';

-- Índices para tb_password_reset_token
CREATE INDEX idx_password_reset_token_user_id ON tb_password_reset_token (user_id);
CREATE INDEX idx_password_reset_token_expira_em ON tb_password_reset_token (expira_em);
CREATE INDEX idx_password_reset_token_usado ON tb_password_reset_token (usado);

-- -- Tabela tb_refresh_token
-- CREATE TABLE tb_refresh_token (
--     id UUID PRIMARY KEY,
--     created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
--     updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
--     user_id UUID NOT NULL UNIQUE,
--     token TEXT NOT NULL UNIQUE,
--     expira_em TIMESTAMP WITHOUT TIME ZONE NOT NULL,
--     CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES tb_user (id)
-- );

-- COMMENT ON TABLE tb_refresh_token IS 'Entidade que representa o Refresh Token no banco de dados.';
-- COMMENT ON COLUMN tb_refresh_token.id IS 'Identificador único do refresh token.';
-- COMMENT ON COLUMN tb_refresh_token.created_at IS 'Timestamp da criação do registro.';
-- COMMENT ON COLUMN tb_refresh_token.updated_at IS 'Timestamp da última atualização do registro.';
-- COMMENT ON COLUMN tb_refresh_token.user_id IS 'Usuário ao qual este refresh token está associado.';
-- COMMENT ON COLUMN tb_refresh_token.token IS 'O valor real do refresh token.';
-- COMMENT ON COLUMN tb_refresh_token.expira_em IS 'Data e hora em que este refresh token se tornará inválido.';

-- -- Índices para tb_refresh_token
-- CREATE INDEX idx_refresh_token_user_id ON tb_refresh_token (user_id);
-- CREATE INDEX idx_refresh_token_expira_em ON tb_refresh_token (expira_em);

