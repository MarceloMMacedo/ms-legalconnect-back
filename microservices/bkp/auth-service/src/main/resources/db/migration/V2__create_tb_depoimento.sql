CREATE TABLE IF NOT EXISTS tb_depoimento (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    texto VARCHAR(500) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    local VARCHAR(100),
    foto_url VARCHAR(255),
    user_id UUID NOT NULL,
    tipo_depoimento VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);