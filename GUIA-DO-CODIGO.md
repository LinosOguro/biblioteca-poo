# GUIA DO CÓDIGO — leia isto antes da aula de amanhã

Objetivo deste guia: você conseguir fazer **qualquer mudança no sistema sem ajuda**,
porque entende onde cada coisa mora e qual o caminho que um dado percorre.

---

## 1. O mapa das 5 camadas

Cada pacote é uma camada com UMA responsabilidade:

| Pacote | Camada | Responsabilidade | Arquivos |
|---|---|---|---|
| `view` | **Fronteira** | Telas: coletar o que o usuário digita e exibir resultados. **Zero regra de negócio, zero SQL.** | LoginBoundary, PrincipalBoundary, AppMenu, AutorBoundary, LivroBoundary, UsuarioBoundary, EmprestimoBoundary |
| `control` | **Controle** | Validar campo a campo, aplicar regras de negócio, decidir INSERT ou UPDATE. | AutorControl, LivroControl, UsuarioControl, EmprestimoControl, LoginControl |
| `model` | **Entidade** | Objetos que representam as linhas das tabelas (só atributos + getters/setters). | Autor, Livro, Usuario, Emprestimo |
| `dao` | **DAO** | SQL puro: conversa com o MariaDB via JDBC. | ConexaoFactory + interface e Impl de cada entidade |
| `security` | **Segurança** | Quem está logado e qual o perfil (ADMIN/COMUM). | Sessao |

**Regra de ouro:** a view só fala com control; o control só fala com dao; ninguém pula camada.

---

## 2. Anatomia de UM fluxo completo (decore este!)

O que acontece quando o admin preenche o formulário de Autor e clica **Salvar**:

```
[AutorBoundary]                          (view)
  btnSalvar.setOnAction -> salvar()
  salvar() pega os TEXTOS dos campos e chama:
      control.salvar(id, txtNome.getText(), ..., dpDataNasc.getValue(), ...)
      |  id = null  se nada selecionado na tabela (CADASTRO)
      |  id = selecionado.getId()  se uma linha foi clicada (EDIÇÃO)
      v
[AutorControl]                           (control)
  salvar(...) valida CADA campo e acumula mensagens em List<String> erros
      - lista NÃO vazia -> devolve os erros (não toca no banco)
      - lista vazia -> monta o objeto Autor e chama:
            id == null ? dao.cadastrar(a) : dao.atualizar(id, a)
      v
[AutorDAOImpl]                           (dao)
  cadastrar(a): monta o INSERT com "?", abre conexão pela ConexaoFactory
  (try-with-resources fecha sozinho), preenche os "?" e executeUpdate()
      v
[MariaDB]  grava a linha na tabela autor
      ^
[AutorBoundary]  recebe a List<String> de volta:
  - vazia  -> Alert de sucesso + limparFormulario() + carregarTabela("")
  - cheia  -> mostrarErros(): um Alert com UMA mensagem por linha
```

Os outros 3 CRUDs (Livro, Usuario, Emprestimo) são **cópias dessa receita**.
Se entendeu o Autor, entendeu todos.

---

## 3. Convenções que se repetem no código inteiro

- **`selecionado`** (na view): o objeto da linha clicada na tabela.
  `null` = modo cadastro; preenchido = modo edição.
- **`salvar(Long id, ...)`** (no control): `id == null` → INSERT; `id != null` → UPDATE.
- **Retornos dos controls:**
  - `salvar(...)` → `List<String>` de erros (**vazia = sucesso**)
  - `excluir(...)` → `String` (**null = sucesso**, senão a mensagem de erro)
  - `listar(...)` → a lista de objetos (erro de banco → lista vazia + stack trace no console)
- **`consultarPorNome("")`** com LIKE `%%` casa com tudo → é assim que "listar todos" funciona.
- **PropertyValueFactory("nome")** liga a coluna da tabela ao getter `getNome()` do model.
  O texto entre aspas é o nome do atributo **com a 1ª letra minúscula**.
- **Significado dos booleans `status`:**
  - Livro: `true` = disponível / `false` = emprestado
  - Empréstimo: `true` = devolvido / `false` = em aberto

---

## 4. Receitas de modificação (os prováveis "pedidos do professor")

### Receita A — Adicionar um campo numa entidade (ex: `editora` no Livro)

Mexa em **5 lugares**, nesta ordem:

1. **SQL** (`bibliotecadb.sql`): adicione a coluna no `CREATE TABLE livro`:
   `editora VARCHAR(100) NOT NULL,` → rode o script de novo no Docker.
2. **Model** (`Livro.java`): atributo `private String editora;` + getter/setter
   (+ no construtor, se quiser).
3. **DAO** (`LivroDAOImpl.java`): 
   - no INSERT: acrescente a coluna, um `?` a mais e o `stm.setString(n, l.getEditora())`;
   - no UPDATE: idem (atenção: o `WHERE id = ?` anda uma posição pra frente!);
   - no `montarLivro`: `l.setEditora(rs.getString("editora"));`
4. **Control** (`LivroControl.java`): receba `String editora` no `salvar(...)`,
   valide (`if vazio -> erros.add("Editora: campo obrigatorio.")`) e
   passe pro construtor/setter do Livro.
5. **View** (`LivroBoundary.java`):
   - `private TextField txtEditora = new TextField();`
   - adicione no GridPane: `form.add(new Label("Editora:"), 0, 4); form.add(txtEditora, 1, 4);`
   - inclua no `control.salvar(..., txtEditora.getText(), ...)`;
   - preencha em `preencherFormularioComSelecionado()` e limpe em `limparFormulario()`;
   - nova coluna na tabela: copie um bloco `TableColumn` e troque o nome/property.

> **Macete:** siga um campo que já existe (ex: `idioma`) com Ctrl+F em cada um
> dos 5 arquivos e replique o padrão.

### Receita B — Adicionar/alterar uma validação
Só no **Control** da entidade. Ex: ISBN com no mínimo 10 caracteres:
```java
} else if (isbn.trim().length() < 10) {
    erros.add("ISBN: minimo de 10 caracteres.");
}
```

### Receita C — Nova coluna na TableView (campo que já existe)
Só na **View**, dentro de `montarColunas()`: copie um bloco `TableColumn`,
troque o título e o nome do property, e inclua no `getColumns().addAll(...)`.

### Receita D — Mudar uma regra de negócio (ex: prazo máximo de 15 dias)
No **EmprestimoControl.salvar**, junto das outras validações:
```java
if (dataEmprestimo != null && dataDevolucao != null
        && dataDevolucao.isAfter(dataEmprestimo.plusDays(15))) {
    erros.add("Data de devolucao: prazo maximo de 15 dias.");
}
```

### Receita E — Restringir algo a um perfil
Use a camada de segurança: `if (Sessao.isAdmin()) { ... }` para mostrar/esconder
botões (veja PrincipalBoundary), ou o bloqueio no início do `start`
(veja AutorBoundary/UsuarioBoundary).

### Receita F — Criar uma entidade NOVA (ex: Editora)
Copie a "família" do Autor inteira e renomeie:
`Autor.java → Editora.java`, `AutorDAO/Impl`, `AutorControl`, `AutorBoundary`
+ `CREATE TABLE` no SQL + botão na PrincipalBoundary/AppMenu.

---

## 5. Segurança e perfis — como funciona

1. `LoginControl.autenticar` confere nome+senha no banco e chama **`Sessao.login(u)`**.
2. A partir daí, qualquer classe pergunta `Sessao.isAdmin()` ou `Sessao.getUsuarioLogado()`.
3. Onde os perfis aparecem no código:
   - `PrincipalBoundary` e `AppMenu`: botões/menus de Autores e Usuários só para ADMIN;
   - `LivroBoundary` e `EmprestimoBoundary`: o formulário só é montado `if (Sessao.isAdmin())` — COMUM só consulta;
   - `AutorBoundary` e `UsuarioBoundary`: bloqueio total no início do `start`;
   - `EmprestimoControl.listar()`: ADMIN vê tudo, COMUM vê só os próprios (filtro no SQL via `consultarPorUsuario`);
   - `UsuarioControl.excluir`: ninguém exclui a si mesmo.
4. `Logout` chama `Sessao.logout()` e volta pro login.

---

## 6. Regras de negócio do empréstimo (EmprestimoControl)

1. Só empresta livro **disponível** (`livro.status == true`).
2. Criar empréstimo → livro vira **emprestado** (`status = false`).
3. Marcar "Devolvido" → livro volta a **disponível**.
4. Excluir empréstimo → livro é **liberado**.
5. Na edição, os combos de livro/usuário ficam **travados** (não se troca o livro de um empréstimo; encerra um e cria outro).

Quem sincroniza o livro é o método privado `atualizarDisponibilidadeDoLivro`,
que usa o `LivroDAO.buscarPorId` + `atualizar`.

---

## 7. Problemas comuns (troubleshooting)

| Sintoma | Causa provável | Solução |
|---|---|---|
| "Erro ao acessar o banco" no login | Container parado | Docker Desktop aberto + `docker start biblioteca-db` |
| Tabela `usuario` sem coluna `perfil` / login falha | Banco antigo | Rode o script SQL de novo (ele recria tudo) |
| `gradlew run` reclama de JAVA_HOME | Variável apontando pro JDK da extensão | `$env:JAVA_HOME = "C:\opt\jdk-21.0.10-full"` ou variável de sistema |
| Coluna da tabela vem vazia | Nome errado no PropertyValueFactory | Tem que bater com o getter do model (1ª letra minúscula) |
| Erro de FK ao excluir | Registro tem dependentes | É proposital: a mensagem amigável vem do `excluir()` do control |

---

## 8. Treino para hoje à noite (faça SOZINHO, sem IA)

1. Leia `AutorBoundary` + `AutorControl` + `AutorDAOImpl` de cima a baixo (20 min).
2. Execute a **Receita A** adicionando o campo `editora` no Livro. Rode e teste.
3. Execute a **Receita D** com prazo máximo de 30 dias no empréstimo.
4. Se travar em algum passo, releia a anatomia da seção 2 — a resposta está no caminho view → control → dao.

Conseguiu fazer os 3? Você está pronto pra qualquer mudança que o professor inventar.

---

## 9. Roteiro sugerido pro vídeo de 2 minutos

| Tempo | Cena |
|---|---|
| 0:00–0:15 | Mostrar o login errando a senha (validação) e entrando como **admin** |
| 0:15–0:45 | CRUD de Autor: cadastrar com campos errados (mostrar as mensagens individuais), corrigir, salvar, editar, excluir |
| 0:45–1:10 | CRUD de Livro com ComboBox de autor + cadastrar um empréstimo (livro fica "Emprestado") |
| 1:10–1:30 | Marcar devolvido → livro volta a "Disponível" |
| 1:30–1:55 | Logout, entrar como **leitor** (COMUM): mostrar que só consulta livros e vê os próprios empréstimos |
| 1:55–2:00 | Encerrar citando as 5 camadas e o ODS 4 |
