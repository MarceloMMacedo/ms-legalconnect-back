-- V1__initial_schema.sql
--
-- Flyway migration file for initial schema creation
-- Generated from PostgreSQL database dump on 2025-07-25
 

--
-- Table: tb_area_atuacao
--
CREATE TABLE tb_area_atuacao (
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    descricao text,
    nome character varying(255) NOT NULL
);

--
-- Table: tb_certificacao_profissional
--
CREATE TABLE tb_certificacao_profissional (
    data_conclusao date,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    profissional_id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    instituicao character varying(255),
    nome character varying(255) NOT NULL
);

--
-- Table: tb_documento_profissional
--
CREATE TABLE tb_documento_profissional (
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    profissional_id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    tipo_documento character varying(100) NOT NULL,
    url_s3 character varying(500) NOT NULL,
    nome_arquivo character varying(255) NOT NULL
);

--
-- Table: tb_endereco
--
CREATE TABLE tb_endereco (
    estado character varying(2) NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    cep character varying(9) NOT NULL,
    id uuid NOT NULL,
    pessoa_id uuid,
    numero character varying(20) NOT NULL,
    pais character varying(50) NOT NULL,
    tipo_endereco character varying(50) NOT NULL,
    bairro character varying(100) NOT NULL,
    cidade character varying(100) NOT NULL,
    complemento character varying(255),
    logradouro character varying(255) NOT NULL,
    CONSTRAINT tb_endereco_tipo_endereco_check CHECK (((tipo_endereco)::text = ANY ((ARRAY['RESIDENCIAL'::character varying, 'COMERCIAL'::character varying, 'ESCRITORIO'::character varying, 'COBRANCA'::character varying, 'ENTREGA'::character varying, 'OUTRO'::character varying])::text[])))
);

--
-- Table: tb_experiencia_profissional
--
CREATE TABLE tb_experiencia_profissional (
    data_fim date,
    data_inicio date NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    profissional_id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    cargo character varying(255) NOT NULL,
    descricao text,
    empresa character varying(255) NOT NULL
);

--
-- Table: tb_formacao_academica
--
CREATE TABLE tb_formacao_academica (
    data_conclusao date NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    profissional_id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    curso character varying(255) NOT NULL,
    instituicao character varying(255) NOT NULL
);

--
-- Table: tb_idioma
--
CREATE TABLE tb_idioma (
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    codigo character varying(10) NOT NULL,
    id uuid NOT NULL,
    nivel character varying(50),
    nome character varying(100) NOT NULL
);

--
-- Table: tb_local_atuacao
--
CREATE TABLE tb_local_atuacao (
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    nome character varying(255) NOT NULL
);

--
-- Table: tb_password_reset_token
--
CREATE TABLE tb_password_reset_token (
    tentativas integer DEFAULT 0 NOT NULL,
    usado boolean NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    expira_em timestamp(6) with time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    token text NOT NULL
);

--
-- Table: tb_pessoa
--
CREATE TABLE tb_pessoa (
    data_nascimento date,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    cpf character varying(14) NOT NULL,
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    nome_completo character varying(255) NOT NULL
);

--
-- Table: tb_pessoa_telefones
--
CREATE TABLE tb_pessoa_telefones (
    pessoa_id uuid NOT NULL,
    numero_telefone character varying(20)
);

--
-- Table: tb_profissional
--
CREATE TABLE tb_profissional (
    faz_parte_de_plano boolean NOT NULL,
    usa_marketplace boolean NOT NULL,
    empresa_id uuid,
    id uuid NOT NULL,
    pessoa_id uuid NOT NULL,
    plano_id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    numero_oab character varying(50) NOT NULL,
    status_profissional character varying(50) NOT NULL
);

--
-- Table: tb_profissional_area_atuacao
--
CREATE TABLE tb_profissional_area_atuacao (
    area_atuacao_id uuid NOT NULL,
    profissional_id uuid NOT NULL
);

--
-- Table: tb_profissional_idioma
--
CREATE TABLE tb_profissional_idioma (
    idioma_id uuid NOT NULL,
    profissional_id uuid NOT NULL
);

--
-- Table: tb_profissional_local_atuacao
--
CREATE TABLE tb_profissional_local_atuacao (
    local_atuacao_id uuid NOT NULL,
    profissional_id uuid NOT NULL
);

--
-- Table: tb_profissional_roles
--
CREATE TABLE tb_profissional_roles (
    profissional_id uuid NOT NULL,
    role_profissional_id uuid NOT NULL
);

--
-- Table: tb_profissional_tipo_atendimento
--
CREATE TABLE tb_profissional_tipo_atendimento (
    profissional_id uuid NOT NULL,
    tipo_atendimento_id uuid NOT NULL
);

--
-- Table: tb_refresh_token
--
CREATE TABLE tb_refresh_token (
    created_at timestamp(6) without time zone NOT NULL,
    expira_em timestamp(6) with time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    user_id uuid NOT NULL,
    token text NOT NULL
);

--
-- Table: tb_role
--
CREATE TABLE tb_role (
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    nome character varying(100) NOT NULL,
    descricao text
);

--
-- Table: tb_role_profissional
--
CREATE TABLE tb_role_profissional (
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    name character varying(50) NOT NULL
);

--
-- Table: tb_tenant
--
CREATE TABLE tb_tenant (
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    nome character varying(255) NOT NULL,
    schema_name character varying(255) NOT NULL
);

--
-- Table: tb_tipo_atendimento
--
CREATE TABLE tb_tipo_atendimento (
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    id uuid NOT NULL,
    nome character varying(255) NOT NULL
);

--
-- Table: tb_user
--
CREATE TABLE tb_user (
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    cpf character varying(14),
    id uuid NOT NULL,
    tenant_id uuid NOT NULL,
    telefone character varying(20),
    status character varying(50) NOT NULL,
    user_type character varying(50) NOT NULL,
    email character varying(255) NOT NULL,
    foto_url text,
    nome_completo character varying(255) NOT NULL,
    senha_hash text NOT NULL,
    CONSTRAINT tb_user_status_check CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'INACTIVE'::character varying, 'PENDING_APPROVAL'::character varying, 'REJECTED'::character varying, 'PENDING'::character varying])::text[]))),
    CONSTRAINT tb_user_user_type_check CHECK (((user_type)::text = ANY ((ARRAY['CLIENTE'::character varying, 'ADVOGADO'::character varying, 'PLATAFORMA_ADMIN'::character varying, 'SOCIO'::character varying])::text[])))
);

--
-- Table: tb_user_role
--
CREATE TABLE tb_user_role (
    role_id uuid NOT NULL,
    user_id uuid NOT NULL
);


--
-- Primary Key Constraints
--
ALTER TABLE ONLY tb_area_atuacao
    ADD CONSTRAINT tb_area_atuacao_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_certificacao_profissional
    ADD CONSTRAINT tb_certificacao_profissional_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_documento_profissional
    ADD CONSTRAINT tb_documento_profissional_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_endereco
    ADD CONSTRAINT tb_endereco_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_experiencia_profissional
    ADD CONSTRAINT tb_experiencia_profissional_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_formacao_academica
    ADD CONSTRAINT tb_formacao_academica_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_idioma
    ADD CONSTRAINT tb_idioma_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_local_atuacao
    ADD CONSTRAINT tb_local_atuacao_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_password_reset_token
    ADD CONSTRAINT tb_password_reset_token_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_pessoa
    ADD CONSTRAINT tb_pessoa_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_profissional_area_atuacao
    ADD CONSTRAINT tb_profissional_area_atuacao_pkey PRIMARY KEY (area_atuacao_id, profissional_id);

ALTER TABLE ONLY tb_profissional_idioma
    ADD CONSTRAINT tb_profissional_idioma_pkey PRIMARY KEY (idioma_id, profissional_id);

ALTER TABLE ONLY tb_profissional_local_atuacao
    ADD CONSTRAINT tb_profissional_local_atuacao_pkey PRIMARY KEY (local_atuacao_id, profissional_id);

ALTER TABLE ONLY tb_profissional
    ADD CONSTRAINT tb_profissional_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_profissional_roles
    ADD CONSTRAINT tb_profissional_roles_pkey PRIMARY KEY (profissional_id, role_profissional_id);

ALTER TABLE ONLY tb_profissional_tipo_atendimento
    ADD CONSTRAINT tb_profissional_tipo_atendimento_pkey PRIMARY KEY (profissional_id, tipo_atendimento_id);

ALTER TABLE ONLY tb_refresh_token
    ADD CONSTRAINT tb_refresh_token_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_role
    ADD CONSTRAINT tb_role_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_role_profissional
    ADD CONSTRAINT tb_role_profissional_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_tenant
    ADD CONSTRAINT tb_tenant_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_tipo_atendimento
    ADD CONSTRAINT tb_tipo_atendimento_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_user
    ADD CONSTRAINT tb_user_pkey PRIMARY KEY (id);

ALTER TABLE ONLY tb_user_role
    ADD CONSTRAINT tb_user_role_pkey PRIMARY KEY (role_id, user_id);

--
-- Unique Constraints
--
ALTER TABLE ONLY tb_area_atuacao
    ADD CONSTRAINT tb_area_atuacao_nome_key UNIQUE (nome);

ALTER TABLE ONLY tb_idioma
    ADD CONSTRAINT tb_idioma_codigo_key UNIQUE (codigo);

ALTER TABLE ONLY tb_idioma
    ADD CONSTRAINT tb_idioma_nome_key UNIQUE (nome);

ALTER TABLE ONLY tb_local_atuacao
    ADD CONSTRAINT tb_local_atuacao_nome_key UNIQUE (nome);

ALTER TABLE ONLY tb_password_reset_token
    ADD CONSTRAINT tb_password_reset_token_token_key UNIQUE (token);

ALTER TABLE ONLY tb_password_reset_token
    ADD CONSTRAINT tb_password_reset_token_user_id_key UNIQUE (user_id);

ALTER TABLE ONLY tb_pessoa
    ADD CONSTRAINT tb_pessoa_cpf_key UNIQUE (cpf);

ALTER TABLE ONLY tb_pessoa
    ADD CONSTRAINT tb_pessoa_user_id_key UNIQUE (user_id);

ALTER TABLE ONLY tb_profissional
    ADD CONSTRAINT tb_profissional_numero_oab_key UNIQUE (numero_oab);

ALTER TABLE ONLY tb_profissional
    ADD CONSTRAINT tb_profissional_pessoa_id_key UNIQUE (pessoa_id);

ALTER TABLE ONLY tb_refresh_token
    ADD CONSTRAINT tb_refresh_token_token_key UNIQUE (token);

ALTER TABLE ONLY tb_refresh_token
    ADD CONSTRAINT tb_refresh_token_user_id_key UNIQUE (user_id);

ALTER TABLE ONLY tb_role
    ADD CONSTRAINT tb_role_nome_key UNIQUE (nome);

ALTER TABLE ONLY tb_role_profissional
    ADD CONSTRAINT tb_role_profissional_name_key UNIQUE (name);

ALTER TABLE ONLY tb_tenant
    ADD CONSTRAINT tb_tenant_nome_key UNIQUE (nome);

ALTER TABLE ONLY tb_tenant
    ADD CONSTRAINT tb_tenant_schema_name_key UNIQUE (schema_name);

ALTER TABLE ONLY tb_tipo_atendimento
    ADD CONSTRAINT tb_tipo_atendimento_nome_key UNIQUE (nome);

ALTER TABLE ONLY tb_user
    ADD CONSTRAINT tb_user_cpf_key UNIQUE (cpf);

ALTER TABLE ONLY tb_user
    ADD CONSTRAINT tb_user_email_key UNIQUE (email);

--
-- Foreign Key Constraints
--
ALTER TABLE ONLY tb_user
    ADD CONSTRAINT fk2y7b2wurvfj95cr2sux1ayt0u FOREIGN KEY (tenant_id) REFERENCES tb_tenant(id);

ALTER TABLE ONLY tb_profissional_local_atuacao
    ADD CONSTRAINT fk3v36h6886evi6depyes3ivdux FOREIGN KEY (profissional_id) REFERENCES tb_profissional(id);

ALTER TABLE ONLY tb_profissional_area_atuacao
    ADD CONSTRAINT fk4aqydql7eiebu149yt2u3ssoy FOREIGN KEY (profissional_id) REFERENCES tb_profissional(id);

ALTER TABLE ONLY tb_user_role
    ADD CONSTRAINT fk7vn3h53d0tqdimm8cp45gc0kl FOREIGN KEY (user_id) REFERENCES tb_user(id);

ALTER TABLE ONLY tb_password_reset_token
    ADD CONSTRAINT fk84f3gn2ewih92oshfpwlorn0b FOREIGN KEY (user_id) REFERENCES tb_user(id);

ALTER TABLE ONLY tb_profissional_idioma
    ADD CONSTRAINT fk9tf311acnlr50799r9xcgqrwm FOREIGN KEY (profissional_id) REFERENCES tb_profissional(id);

ALTER TABLE ONLY tb_formacao_academica
    ADD CONSTRAINT fkaketpucc7qq6qt9rk0bndhci4 FOREIGN KEY (profissional_id) REFERENCES tb_profissional(id);

ALTER TABLE ONLY tb_profissional_roles
    ADD CONSTRAINT fka7ctp74d636vw7k89ggehwt7v FOREIGN KEY (role_profissional_id) REFERENCES tb_role_profissional(id);

ALTER TABLE ONLY tb_profissional_roles
    ADD CONSTRAINT fkcmi65kmf7713ic47pipnimds5 FOREIGN KEY (profissional_id) REFERENCES tb_profissional(id);

ALTER TABLE ONLY tb_profissional_tipo_atendimento
    ADD CONSTRAINT fkcr4ospg4rmn9nw32b7y6vcq83 FOREIGN KEY (profissional_id) REFERENCES tb_profissional(id);

ALTER TABLE ONLY tb_user_role
    ADD CONSTRAINT fkea2ootw6b6bb0xt3ptl28bymv FOREIGN KEY (role_id) REFERENCES tb_role(id);

ALTER TABLE ONLY tb_profissional
    ADD CONSTRAINT fkfrod9e0odxnybahs2vyag4h9e FOREIGN KEY (id) REFERENCES tb_pessoa(id);

ALTER TABLE ONLY tb_documento_profissional
    ADD CONSTRAINT fkkj3pae2qtbdbbobeex90odg3x FOREIGN KEY (profissional_id) REFERENCES tb_profissional(id);

ALTER TABLE ONLY tb_certificacao_profissional
    ADD CONSTRAINT fkkscnhmu05xaimf9rtwftquxdf FOREIGN KEY (profissional_id) REFERENCES tb_profissional(id);

ALTER TABLE ONLY tb_refresh_token
    ADD CONSTRAINT fkl86d47obg6eykx5gfwgjm1jfa FOREIGN KEY (user_id) REFERENCES tb_user(id);

ALTER TABLE ONLY tb_experiencia_profissional
    ADD CONSTRAINT fkn9vr54fduu4ed4pg1ymnt04mj FOREIGN KEY (profissional_id) REFERENCES tb_profissional(id);

ALTER TABLE ONLY tb_endereco
    ADD CONSTRAINT fkolr88x7vhtskdguw79l1oeaht FOREIGN KEY (pessoa_id) REFERENCES tb_pessoa(id);

ALTER TABLE ONLY tb_pessoa_telefones
    ADD CONSTRAINT fkqe0j8mdopuutux01tifx3fhgg FOREIGN KEY (pessoa_id) REFERENCES tb_pessoa(id);
