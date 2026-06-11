# Sistema de Biblioteca — Trabalho de POO (FATEC)
Projeto biblioteca com javaFx

Sistema corporativo de gerenciamento de biblioteca em **Java 21 + JavaFX + MariaDB**,
dividido nas camadas **Fronteira (view), Controle (control), Entidade (model),
DAO (dao) e Segurança (security)**, protegido por usuário e senha com **2 perfis
de acesso** (ADMIN e COMUM).

## Integrantes
- Enzo Oguro Linos
- Leonardo Alencar Bordoni

## Link do Vídeo explicativo:
https://youtu.be/nShbP4b2Ia4

## ODS — Objetivo de Desenvolvimento Sustentável
Este trabalho se relaciona com o **ODS 4 — Educação de Qualidade**: o sistema
informatiza o acervo e os empréstimos de uma biblioteca, democratizando e
organizando o acesso a livros. Bibliotecas bem geridas ampliam o acesso à
leitura e apoiam a aprendizagem ao longo da vida (meta 4.6 — alfabetização),
e o controle de devoluções garante que mais pessoas consigam usar o mesmo acervo.

## Como rodar

1. **Subir o banco (Docker):**
   ```
   docker start biblioteca-db
   ```
   (na primeira vez: `docker run --name biblioteca-db -e MARIADB_ROOT_PASSWORD=123456 -p 3306:3306 -d mariadb:latest`)

2. **Criar/recriar as tabelas e dados de exemplo:**
   ```
   docker exec -i biblioteca-db mariadb -uroot -p123456 < app/src/main/bibliotecadb.sql
   ```

3. **Rodar a aplicação** (pelo VS Code em `App.java`, ou):
   ```
   .\gradlew.bat run
   ```

## Logins de teste

| Usuário | Senha | Perfil | Pode fazer |
|---------|-------|--------|------------|
| `admin`  | `admin` | ADMIN | CRUD completo de livros, autores, usuários e empréstimos |
| `leitor` | `1234`  | COMUM | Consultar livros e ver os próprios empréstimos |

## Arquitetura (resumo)

```
view (Fronteira)  ->  control (Controle)  ->  dao (DAO)  ->  MariaDB
      |                     |
      +----- security (Sessão / perfis)    model (Entidade) circula entre as camadas
```

Leia o **GUIA-DO-CODIGO.md** para entender o fluxo completo e como modificar o sistema.
