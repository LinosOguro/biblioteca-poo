-- ============================================================================
-- SCRIPT DO BANCO DE DADOS - Sistema de Biblioteca
-- ----------------------------------------------------------------------------
-- Como executar (com o container Docker rodando):
--   docker exec -i biblioteca-db mariadb -uroot -p123456 < app/src/main/bibliotecadb.sql
--
-- ATENCAO: este script APAGA e RECRIA todas as tabelas (os dados se perdem).
-- Use sempre que quiser "zerar" o banco para o estado inicial de testes.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS biblioteca;
USE biblioteca;

-- A ordem dos DROPs importa: primeiro as tabelas que possuem chave estrangeira
-- (emprestimo depende de livro e usuario; livro depende de autor).
DROP TABLE IF EXISTS emprestimo;
DROP TABLE IF EXISTS livro;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS autor;

-- ----------------------------------------------------------------------------
-- AUTOR (6 campos)
-- ----------------------------------------------------------------------------
CREATE TABLE autor (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    nacionalidade VARCHAR(20) NOT NULL,
    dataNasc DATE NOT NULL,
    email VARCHAR(200) NOT NULL,
    biografia TEXT NOT NULL,

    PRIMARY KEY (id)
) AUTO_INCREMENT = 101;

-- ----------------------------------------------------------------------------
-- LIVRO (8 campos)
-- status: TRUE = disponivel para emprestimo / FALSE = emprestado
-- ----------------------------------------------------------------------------
CREATE TABLE livro (
    id INT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(150) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    ano_publicacao INT NOT NULL,
    num_paginas INT NOT NULL,
    idioma VARCHAR(30) NOT NULL,
    status BOOLEAN NOT NULL,
    id_autor INT NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (id_autor) REFERENCES autor(id)
) AUTO_INCREMENT = 1001;

-- ----------------------------------------------------------------------------
-- USUARIO (7 campos)
-- perfil: 'ADMIN' (gerencia tudo) ou 'COMUM' (apenas consulta livros e
--         visualiza os proprios emprestimos). Eh a base dos 2 perfis de acesso.
-- O campo "nome" tambem funciona como login do sistema.
-- ----------------------------------------------------------------------------
CREATE TABLE usuario (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL,
    telefone VARCHAR(11),
    cpf CHAR(11) NOT NULL,
    senha VARCHAR(50) NOT NULL,
    perfil VARCHAR(20) NOT NULL DEFAULT 'COMUM',

    PRIMARY KEY (id),

    CHECK (CHAR_LENGTH(telefone) IN (10,11)),
    CHECK (CHAR_LENGTH(cpf) = 11),
    CHECK (CHAR_LENGTH(senha) BETWEEN 4 AND 50),
    CHECK (perfil IN ('ADMIN', 'COMUM'))
) AUTO_INCREMENT = 1;

-- ----------------------------------------------------------------------------
-- EMPRESTIMO (7 campos)
-- status: TRUE = devolvido / FALSE = em aberto (livro ainda esta com o usuario)
-- ----------------------------------------------------------------------------
CREATE TABLE emprestimo (
    id INT NOT NULL AUTO_INCREMENT,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE NOT NULL,
    status BOOLEAN NOT NULL,
    valor_multa DECIMAL(7,2) NOT NULL DEFAULT 0,
    id_livro INT NOT NULL,
    id_usuario INT NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (id_livro) REFERENCES livro(id),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
) AUTO_INCREMENT = 10;

-- ============================================================================
-- DADOS INICIAIS (para o sistema nao comecar vazio)
-- ============================================================================

-- Usuarios iniciais: 1 de cada perfil.
-- Logins para teste:  admin / admin   (perfil ADMIN)
--                     leitor / 1234   (perfil COMUM)
INSERT INTO usuario (nome, email, telefone, cpf, senha, perfil) VALUES
('admin',  'admin@biblioteca.com', '11999990000', '00000000000', 'admin', 'ADMIN'),
('leitor', 'leitor@email.com',     '11988887777', '11122233344', '1234',  'COMUM');

INSERT INTO autor (nome, nacionalidade, dataNasc, email, biografia) VALUES
('Machado de Assis',  'Brasileira', '1839-06-21', 'machado@classicos.com', 'Fundador da Academia Brasileira de Letras e maior nome do realismo brasileiro.'),
('Clarice Lispector', 'Brasileira', '1920-12-10', 'clarice@classicos.com', 'Escritora e jornalista, autora de romances introspectivos marcantes.'),
('George Orwell',     'Britanica',  '1903-06-25', 'orwell@classicos.com',  'Escritor britanico, autor de distopias que criticam regimes totalitarios.');

-- O livro 1984 (id 1004) ja comeca EMPRESTADO (status = FALSE) para demonstrar a regra.
INSERT INTO livro (titulo, isbn, ano_publicacao, num_paginas, idioma, status, id_autor) VALUES
('Dom Casmurro',               '9788535910663', 1899, 256, 'Portugues', TRUE,  101),
('Memorias Postumas de Bras Cubas', '9788535910664', 1881, 288, 'Portugues', TRUE,  101),
('A Hora da Estrela',          '9788532508126', 1977,  96, 'Portugues', TRUE,  102),
('1984',                       '9780451524935', 1949, 328, 'Ingles',    FALSE, 103);

-- Emprestimo em aberto do livro 1984 (id 1004) para o usuario leitor (id 2).
INSERT INTO emprestimo (data_emprestimo, data_devolucao, status, valor_multa, id_livro, id_usuario) VALUES
('2026-06-01', '2026-06-15', FALSE, 0, 1004, 2);
