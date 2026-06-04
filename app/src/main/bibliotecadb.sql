CREATE DATABASE biblioteca;
USE biblioteca;

DROP TABLE IF EXISTS emprestimo;
DROP TABLE IF EXISTS livro;
DROP TABLE IF EXISTS usuario;
DROP TABLE IF EXISTS autor;

CREATE TABLE autor (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    nacionalidade VARCHAR(20) NOT NULL,
    dataNasc DATE NOT NULL,
    email VARCHAR(200) NOT NULL,
    biografia TEXT NOT NULL,

    PRIMARY KEY (id)
) AUTO_INCREMENT = 101;

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

CREATE TABLE usuario (
    id INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(200) NOT NULL,
    telefone VARCHAR(11),
    cpf CHAR(11) NOT NULL,
    senha VARCHAR(50) NOT NULL,

    PRIMARY KEY (id),

    CHECK (CHAR_LENGTH(telefone) IN (10,11)),
    CHECK (CHAR_LENGTH(cpf) = 11),
    CHECK (CHAR_LENGTH(senha) BETWEEN 4 AND 50)
) AUTO_INCREMENT = 1;

CREATE TABLE emprestimo (
    id INT NOT NULL AUTO_INCREMENT,
    data_emprestimo DATE NOT NULL,
    data_devolucao DATE NOT NULL,
    status BOOLEAN NOT NULL,
    valor_multa DECIMAL(7,2),
    id_livro INT NOT NULL,
    id_usuario INT NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (id_livro) REFERENCES livro(id),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id)
) AUTO_INCREMENT = 10;