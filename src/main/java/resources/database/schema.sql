
CREATE TABLE IF NOT EXISTS cliente (
    id_cliente SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    telefone VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS funcionario (
    id_funcionario SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    cargo VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS produto (
    id_produto SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco NUMERIC(10,2) NOT NULL,
    quantidade_estoque INTEGER NOT NULL DEFAULT 0,
    imagem_url TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_produto_preco CHECK (preco >= 0),
    CONSTRAINT chk_produto_estoque CHECK (quantidade_estoque >= 0)
);

CREATE TABLE IF NOT EXISTS endereco (
    id_endereco SERIAL PRIMARY KEY,
    id_cliente INTEGER NOT NULL,
    cep VARCHAR(9) NOT NULL,
    logradouro VARCHAR(150) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(100),
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,

    CONSTRAINT fk_endereco_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente)
);

CREATE TABLE IF NOT EXISTS pedido (
    id_pedido SERIAL PRIMARY KEY,
    id_cliente INTEGER NOT NULL,
    id_endereco INTEGER NOT NULL,
    id_funcionario INTEGER,
    data_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_total NUMERIC(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL,

    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente),

    CONSTRAINT fk_pedido_endereco
        FOREIGN KEY (id_endereco)
        REFERENCES endereco(id_endereco),

    CONSTRAINT fk_pedido_funcionario
        FOREIGN KEY (id_funcionario)
        REFERENCES funcionario(id_funcionario),

    CONSTRAINT chk_pedido_valor CHECK (valor_total >= 0)
);

CREATE TABLE IF NOT EXISTS item_pedido (
    id_item SERIAL PRIMARY KEY,
    id_pedido INTEGER NOT NULL,
    id_produto INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_item_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido),

    CONSTRAINT fk_item_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto),

    CONSTRAINT chk_item_quantidade CHECK (quantidade > 0),
    CONSTRAINT chk_item_preco CHECK (preco_unitario >= 0)
);

CREATE TABLE IF NOT EXISTS pagamento (
    id_pagamento SERIAL PRIMARY KEY,
    id_pedido INTEGER NOT NULL UNIQUE,
    forma_pagamento VARCHAR(30) NOT NULL,
    valor NUMERIC(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    data_pagamento TIMESTAMP,

    CONSTRAINT fk_pagamento_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido(id_pedido),

    CONSTRAINT chk_pagamento_valor CHECK (valor >= 0)
);
