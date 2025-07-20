-- src/main/resources/db/migration/V1__Create_All_Initial_Schema.sql

-- Seus comentários iniciais ou informações sobre o script
-- Este script cria todas as tabelas iniciais para o sistema LegalConnect.
-- Ele unifica as definições de schemas de usuários/tenants com as entidades de negócio.

---

-- Tabela tb_tenant
-- Entidade que representa um tenant (ambiente isolado para escritórios/advogados).
CREATE TABLE tb_tenant (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
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

---

-- Tabela tb_role
-- Entidade para definir os papéis de acesso do usuário no sistema.
-- Unificada a partir das duas definições, mantendo created_at e updated_at.
CREATE TABLE tb_role (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
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

---

-- Tabela tb_user
-- Entidade base para todos os usuários (clientes, advogados, administradores de tenant, etc.).
CREATE TABLE tb_user (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    tenant_id UUID NOT NULL, -- Chave estrangeira para tb_tenant
    nome_completo VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE, -- Unique por tenant/schema ou globalmente? Se global, pode ser um problema de multitenancy. Assumindo global por enquanto.
    cpf VARCHAR(14) NOT NULL UNIQUE,   -- Unique por tenant/schema ou globalmente? Assumindo global por enquanto.
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

---

-- Tabela de junção tb_user_role
-- Tabela de junção para o relacionamento muitos-para-muitos entre User e Role.
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

---

-- Tabela tb_refresh_token
-- Entidade que representa o Refresh Token no banco de dados.
-- Unificada a partir das duas definições, mantendo created_at e updated_at.
CREATE TABLE tb_refresh_token (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
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

---

-- Tabela tb_password_reset_token
-- Entidade que representa um token de redefinição de senha.
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

---

-- Tabela tb_empresa
-- Criação da tabela tb_empresa para dados de empresas.
CREATE TABLE tb_empresa (
    id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    cnpj VARCHAR(18) NOT NULL UNIQUE,
    email_contato VARCHAR(255) NULL,
    nome_fantasia VARCHAR(255) NOT NULL,
    razao_social VARCHAR(255) NOT NULL,
    CONSTRAINT tb_empresa_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE tb_empresa IS 'Entidade que representa uma empresa.';
COMMENT ON COLUMN tb_empresa.id IS 'Identificador único da empresa.';
COMMENT ON COLUMN tb_empresa.created_at IS 'Timestamp da criação do registro.';
COMMENT ON COLUMN tb_empresa.updated_at IS 'Timestamp da última atualização do registro.';
COMMENT ON COLUMN tb_empresa.cnpj IS 'CNPJ da empresa (único).';
COMMENT ON COLUMN tb_empresa.email_contato IS 'Email de contato da empresa.';
COMMENT ON COLUMN tb_empresa.nome_fantasia IS 'Nome fantasia da empresa.';
COMMENT ON COLUMN tb_empresa.razao_social IS 'Razão social da empresa.';

-- Índices para tb_empresa
CREATE INDEX idx_empresa_cnpj ON tb_empresa (cnpj);

---

-- Tabela tb_plano
-- Criação da tabela tb_plano para detalhes de planos de assinatura.
CREATE TABLE tb_plano (
    id UUID NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao TEXT NULL,
    preco_mensal NUMERIC(10, 2) NOT NULL,
    preco_anual NUMERIC(10, 2) NOT NULL,
    acesso_relatorios_avancados BOOLEAN NOT NULL,
    is_default BOOLEAN NOT NULL,
    limite_servicos_agendaveis INT NULL,
    periodo_teste_dias INT NULL,
    permite_pedidos_orcamento BOOLEAN NOT NULL,
    visibilidade_destaque VARCHAR(50) NOT NULL,
    CONSTRAINT tb_plano_pkey PRIMARY KEY (id),
    CONSTRAINT tb_plano_visibilidade_destaque_check CHECK (visibilidade_destaque IN ('PADRAO', 'PREMIUM', 'DESTAQUE_MAXIMO'))
);

COMMENT ON TABLE tb_plano IS 'Entidade que representa os diferentes planos de assinatura.';
COMMENT ON COLUMN tb_plano.id IS 'Identificador único do plano.';
COMMENT ON COLUMN tb_plano.created_at IS 'Timestamp da criação do registro.';
COMMENT ON COLUMN tb_plano.updated_at IS 'Timestamp da última atualização do registro.';
COMMENT ON COLUMN tb_plano.nome IS 'Nome do plano (único).';
COMMENT ON COLUMN tb_plano.descricao IS 'Descrição detalhada do plano.';
COMMENT ON COLUMN tb_plano.preco_mensal IS 'Preço mensal do plano.';
COMMENT ON COLUMN tb_plano.preco_anual IS 'Preço anual do plano.';
COMMENT ON COLUMN tb_plano.acesso_relatorios_avancados IS 'Indica se o plano inclui acesso a relatórios avançados.';
COMMENT ON COLUMN tb_plano.is_default IS 'Indica se este é o plano padrão.';
COMMENT ON COLUMN tb_plano.limite_servicos_agendaveis IS 'Limite de serviços agendáveis para o plano.';
COMMENT ON COLUMN tb_plano.periodo_teste_dias IS 'Período de teste em dias, se aplicável.';
COMMENT ON COLUMN tb_plano.permite_pedidos_orcamento IS 'Indica se o plano permite pedidos de orçamento.';
COMMENT ON COLUMN tb_plano.visibilidade_destaque IS 'Nível de visibilidade de destaque do plano.';

-- Índices para tb_plano
CREATE INDEX idx_plano_nome ON tb_plano (nome);
CREATE INDEX idx_plano_is_default ON tb_plano (is_default);

---

-- Tabela tb_pessoa
-- Entidade base para pessoas físicas (clientes, profissionais, administradores de plataforma).
CREATE TABLE tb_pessoa (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id UUID NOT NULL UNIQUE, -- Chave estrangeira para tb_user
    nome_completo VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    data_nascimento DATE NULL,
    CONSTRAINT fk_pessoa_user FOREIGN KEY (user_id) REFERENCES tb_user (id)
);

COMMENT ON TABLE tb_pessoa IS 'Entidade base para pessoas físicas (clientes, profissionais, administradores de plataforma).';
COMMENT ON COLUMN tb_pessoa.id IS 'Identificador único da pessoa.';
COMMENT ON COLUMN tb_pessoa.created_at IS 'Timestamp da criação do registro.';
COMMENT ON COLUMN tb_pessoa.updated_at IS 'Timestamp da última atualização do registro.';
COMMENT ON COLUMN tb_pessoa.user_id IS 'ID do usuário associado a esta pessoa.';
COMMENT ON COLUMN tb_pessoa.nome_completo IS 'Nome completo da pessoa.';
COMMENT ON COLUMN tb_pessoa.cpf IS 'CPF da pessoa (único).';
COMMENT ON COLUMN tb_pessoa.data_nascimento IS 'Data de nascimento da pessoa.';

-- Índices para tb_pessoa
CREATE INDEX idx_pessoa_user_id ON tb_pessoa (user_id);
CREATE INDEX idx_pessoa_cpf ON tb_pessoa (cpf);

---

-- Tabela tb_administrador
-- Entidade para administradores da plataforma. Herda de tb_pessoa.
CREATE TABLE tb_administrador (
    id UUID PRIMARY KEY, -- Chave estrangeira para tb_pessoa
    status VARCHAR(255) NULL, -- Ex: 'ATIVO', 'INATIVO'
    CONSTRAINT fk_administrador_pessoa FOREIGN KEY (id) REFERENCES tb_pessoa (id)
);

COMMENT ON TABLE tb_administrador IS 'Entidade que representa um administrador da plataforma.';
COMMENT ON COLUMN tb_administrador.id IS 'Identificador único do administrador (chave estrangeira para tb_pessoa).';
COMMENT ON COLUMN tb_administrador.status IS 'Status do administrador.';

---

-- Tabela tb_cliente
-- Entidade para clientes. Herda de tb_pessoa.
CREATE TABLE tb_cliente (
    id UUID PRIMARY KEY, -- Chave estrangeira para tb_pessoa
    status VARCHAR(255) NULL, -- Ex: 'ATIVO', 'INATIVO', 'PENDENTE'
    tipo VARCHAR(255) NULL,   -- Ex: 'PESSOA_FISICA', 'PESSOA_JURIDICA'
    CONSTRAINT fk_cliente_pessoa FOREIGN KEY (id) REFERENCES tb_pessoa (id)
);

COMMENT ON TABLE tb_cliente IS 'Entidade que representa um cliente.';
COMMENT ON COLUMN tb_cliente.id IS 'Identificador único do cliente (chave estrangeira para tb_pessoa).';
COMMENT ON COLUMN tb_cliente.status IS 'Status do cliente.';
COMMENT ON COLUMN tb_cliente.tipo IS 'Tipo de cliente (Pessoa Física ou Jurídica).';

---

-- Tabela tb_profissional
-- Entidade para profissionais (advogados). Herda de tb_pessoa.
CREATE TABLE tb_profissional (
    id UUID PRIMARY KEY, -- Chave estrangeira para tb_pessoa
    plano_id UUID NOT NULL, -- Chave estrangeira para tb_plano
    empresa_id UUID NULL, -- Opcional: para qual empresa o profissional está vinculado
    numero_oab VARCHAR(50) NOT NULL UNIQUE,
    status_profissional VARCHAR(50) NOT NULL,
    faz_parte_de_plano BOOLEAN NOT NULL,
    usa_marketplace BOOLEAN NOT NULL,
    CONSTRAINT fk_profissional_pessoa FOREIGN KEY (id) REFERENCES tb_pessoa (id),
    CONSTRAINT fk_profissional_plano FOREIGN KEY (plano_id) REFERENCES tb_plano (id),
    CONSTRAINT fk_profissional_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa (id),
    CONSTRAINT tb_profissional_status_profissional_check CHECK (status_profissional IN ('ATIVO', 'LICENCIADO', 'SUSPENSO', 'EM_ANALISE'))
);

COMMENT ON TABLE tb_profissional IS 'Entidade que representa um profissional (advogado).';
COMMENT ON COLUMN tb_profissional.id IS 'Identificador único do profissional (chave estrangeira para tb_pessoa).';
COMMENT ON COLUMN tb_profissional.plano_id IS 'ID do plano de assinatura do profissional.';
COMMENT ON COLUMN tb_profissional.empresa_id IS 'ID da empresa à qual o profissional está vinculado (opcional).';
COMMENT ON COLUMN tb_profissional.numero_oab IS 'Número da OAB do profissional (único).';
COMMENT ON COLUMN tb_profissional.status_profissional IS 'Status atual do profissional.';
COMMENT ON COLUMN tb_profissional.faz_parte_de_plano IS 'Indica se o profissional faz parte de um plano.';
COMMENT ON COLUMN tb_profissional.usa_marketplace IS 'Indica se o profissional utiliza o marketplace.';

-- Índices para tb_profissional
CREATE INDEX idx_profissional_plano_id ON tb_profissional (plano_id);
CREATE INDEX idx_profissional_empresa_id ON tb_profissional (empresa_id);
CREATE INDEX idx_profissional_oab ON tb_profissional (numero_oab);

---

-- Tabela tb_user_profissionals_role (Correção do nome para clareza)
-- Tabela de junção para os papéis específicos de um profissional (se for diferente de tb_user_role).
-- NOTE: Se um "profissional" é apenas um "user" com um "user_type" específico,
-- e seus papéis já são geridos por `tb_user_role`, esta tabela pode ser redundante.
-- Mantendo-a aqui conforme o seu script original, mas considere a sua necessidade.
CREATE TABLE tb_profissional_role (
    profissional_id UUID NOT NULL, -- Renomeado para clareza
    role_id UUID NOT NULL,
    PRIMARY KEY (profissional_id, role_id),
    CONSTRAINT fk_profissional_role_profissional FOREIGN KEY (profissional_id) REFERENCES tb_profissional (id),
    CONSTRAINT fk_profissional_role_role FOREIGN KEY (role_id) REFERENCES tb_role (id)
);

COMMENT ON TABLE tb_profissional_role IS 'Tabela de junção para os papéis específicos de um profissional.';
COMMENT ON COLUMN tb_profissional_role.profissional_id IS 'ID do profissional.';
COMMENT ON COLUMN tb_profissional_role.role_id IS 'ID do papel.';

-- Índices para tb_profissional_role
CREATE INDEX idx_profissional_role_profissional_id ON tb_profissional_role (profissional_id);
CREATE INDEX idx_profissional_role_role_id ON tb_profissional_role (role_id);

---

-- Tabela tb_empresa_telefones
-- Tabela de telefones para empresas.
CREATE TABLE tb_empresa_telefones (
    empresa_id UUID NOT NULL,
    numero_telefone VARCHAR(20) NOT NULL, -- Telefone não pode ser NULL se for a lista de telefones de uma empresa.
    PRIMARY KEY (empresa_id, numero_telefone), -- Adicionado PRIMARY KEY composta para unicidade
    CONSTRAINT fk_empresa_telefones_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa (id)
);

COMMENT ON TABLE tb_empresa_telefones IS 'Tabela para armazenar os números de telefone de uma empresa.';
COMMENT ON COLUMN tb_empresa_telefones.empresa_id IS 'ID da empresa.';
COMMENT ON COLUMN tb_empresa_telefones.numero_telefone IS 'Número de telefone da empresa.';

-- Índices para tb_empresa_telefones
CREATE INDEX idx_empresa_telefones_empresa_id ON tb_empresa_telefones (empresa_id);

---

-- Tabela tb_pessoa_telefones
-- Tabela de telefones para pessoas.
CREATE TABLE tb_pessoa_telefones (
    pessoa_id UUID NOT NULL,
    numero_telefone VARCHAR(20) NOT NULL, -- Telefone não pode ser NULL se for a lista de telefones de uma pessoa.
    PRIMARY KEY (pessoa_id, numero_telefone), -- Adicionado PRIMARY KEY composta para unicidade
    CONSTRAINT fk_pessoa_telefones_pessoa FOREIGN KEY (pessoa_id) REFERENCES tb_pessoa (id)
);

COMMENT ON TABLE tb_pessoa_telefones IS 'Tabela para armazenar os números de telefone de uma pessoa.';
COMMENT ON COLUMN tb_pessoa_telefones.pessoa_id IS 'ID da pessoa.';
COMMENT ON COLUMN tb_pessoa_telefones.numero_telefone IS 'Número de telefone da pessoa.';

-- Índices para tb_pessoa_telefones
CREATE INDEX idx_pessoa_telefones_pessoa_id ON tb_pessoa_telefones (pessoa_id);

---

-- Tabela tb_endereco
-- Tabela para endereços, associados a empresas ou pessoas.
CREATE TABLE tb_endereco (
    id UUID NOT NULL PRIMARY KEY,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    empresa_id UUID NULL, -- Pode ser nulo se for endereço de pessoa
    pessoa_id UUID NULL,  -- Pode ser nulo se for endereço de empresa
    cep VARCHAR(9) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(255) NULL,
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    pais VARCHAR(50) NOT NULL,
    tipo_endereco VARCHAR(50) NOT NULL,
    CONSTRAINT chk_endereco_associacao CHECK (
        (empresa_id IS NOT NULL AND pessoa_id IS NULL) OR
        (empresa_id IS NULL AND pessoa_id IS NOT NULL) OR
        (empresa_id IS NULL AND pessoa_id IS NULL) -- Pode ser um endereço 'solto' ou associado em outro lugar
    ),
    CONSTRAINT tb_endereco_tipo_endereco_check CHECK (tipo_endereco IN ('RESIDENCIAL', 'COMERCIAL', 'ESCRITORIO', 'COBRANCA', 'ENTREGA', 'OUTRO')),
    CONSTRAINT fk_endereco_empresa FOREIGN KEY (empresa_id) REFERENCES tb_empresa (id),
    CONSTRAINT fk_endereco_pessoa FOREIGN KEY (pessoa_id) REFERENCES tb_pessoa (id)
);

COMMENT ON TABLE tb_endereco IS 'Entidade que representa um endereço físico, que pode ser associado a uma empresa ou pessoa.';
COMMENT ON COLUMN tb_endereco.id IS 'Identificador único do endereço.';
COMMENT ON COLUMN tb_endereco.created_at IS 'Timestamp da criação do registro.';
COMMENT ON COLUMN tb_endereco.updated_at IS 'Timestamp da última atualização do registro.';
COMMENT ON COLUMN tb_endereco.empresa_id IS 'ID da empresa associada a este endereço (pode ser nulo).';
COMMENT ON COLUMN tb_endereco.pessoa_id IS 'ID da pessoa associada a este endereço (pode ser nulo).';
COMMENT ON COLUMN tb_endereco.cep IS 'CEP do endereço.';
COMMENT ON COLUMN tb_endereco.logradouro IS 'Nome da rua ou avenida.';
COMMENT ON COLUMN tb_endereco.numero IS 'Número do imóvel.';
COMMENT ON COLUMN tb_endereco.complemento IS 'Complemento do endereço (ex: apto, sala).';
COMMENT ON COLUMN tb_endereco.bairro IS 'Nome do bairro.';
COMMENT ON COLUMN tb_endereco.cidade IS 'Nome da cidade.';
COMMENT ON COLUMN tb_endereco.estado IS 'Sigla do estado (UF).';
COMMENT ON COLUMN tb_endereco.pais IS 'Nome do país.';
COMMENT ON COLUMN tb_endereco.tipo_endereco IS 'Tipo do endereço (RESIDENCIAL, COMERCIAL, etc.).';

-- Índices para tb_endereco
CREATE INDEX idx_endereco_empresa_id ON tb_endereco (empresa_id);
CREATE INDEX idx_endereco_pessoa_id ON tb_endereco (pessoa_id);
CREATE INDEX idx_endereco_cep ON tb_endereco (cep);
CREATE INDEX idx_endereco_cidade_estado ON tb_endereco (cidade, estado);


-- Configuração de permissões para o usuário 'jususer' (opcional, pode ser feito via configuração do ORM/datasource)
-- IMPORTANTE: O Flyway executa scripts como o usuário que você configurou no datasource.
-- Geralmente, permissões são gerenciadas pelo DBA ou por ferramentas de gerenciamento de usuários do banco.
-- Se 'jususer' for o usuário do datasource, ele já terá permissões por padrão.
-- Caso contrário, estas permissões podem ser úteis, mas é bom revisar o modelo de segurança.

-- REVOKE ALL ON ALL TABLES IN SCHEMA public FROM PUBLIC; -- Remova esta linha se não for gerenciar permissões globalmente
-- GRANT ALL ON ALL TABLES IN SCHEMA public TO jususer;   -- Remova esta linha se não for gerenciar permissões globalmente

-- Exemplo de como você faria para tabelas específicas, se necessário:
-- ALTER TABLE tb_tenant OWNER TO jususer;
-- GRANT ALL ON TABLE tb_tenant TO jususer;

-- Repita para outras tabelas conforme a necessidade, ou confie nas configurações padrão do ORM/datasource
-- e na administração de usuários do PostgreSQL.