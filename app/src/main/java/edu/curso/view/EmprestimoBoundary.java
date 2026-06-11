package edu.curso.view;

import java.util.List;

import edu.curso.control.EmprestimoControl;
import edu.curso.control.LivroControl;
import edu.curso.control.UsuarioControl;
import edu.curso.model.Emprestimo;
import edu.curso.model.Livro;
import edu.curso.model.Usuario;
import edu.curso.security.Sessao;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * CAMADA FRONTEIRA (Boundary) - Tela de CRUD de Emprestimos.
 * Segue a mesma receita da AutorBoundary (leia os comentarios de la).
 *
 * Particularidades desta tela:
 *  - Dois ComboBox (Livro e Usuario) e dois DatePicker (datas).
 *  - PERFIS em acao:
 *      ADMIN -> ve TODOS os emprestimos e pode criar/editar/excluir;
 *      COMUM -> ve apenas OS PROPRIOS emprestimos, sem formulario.
 *    (quem filtra a lista eh o EmprestimoControl.listar(), usando a Sessao)
 *  - Na EDICAO, os combos de livro/usuario ficam travados: um emprestimo
 *    nao "muda de livro" - encerra-se um e cria-se outro.
 */
public class EmprestimoBoundary {

    private EmprestimoControl control = new EmprestimoControl();
    // Controles auxiliares so para carregar os ComboBox.
    private LivroControl livroControl = new LivroControl();
    private UsuarioControl usuarioControl = new UsuarioControl();

    private Emprestimo selecionado = null;

    private TableView<Emprestimo> tabela = new TableView<>();

    private ComboBox<Livro> cmbLivro = new ComboBox<>();
    private ComboBox<Usuario> cmbUsuario = new ComboBox<>();
    private DatePicker dpEmprestimo = new DatePicker();
    private DatePicker dpDevolucao = new DatePicker();
    private CheckBox chkDevolvido = new CheckBox("Devolvido");
    private TextField txtMulta = new TextField();

    private Button btnNovo = new Button("Novo");
    private Button btnSalvar = new Button("Salvar");
    private Button btnExcluir = new Button("Excluir");

    public void start(Stage stage) {
        BorderPane raiz = new BorderPane();
        raiz.setTop(AppMenu.criarMenu(stage));

        // ---- Centro: tabela (a lista ja vem filtrada pelo perfil) ----
        Label aviso = new Label(Sessao.isAdmin()
                ? "Todos os emprestimos do sistema:"
                : "Seus emprestimos:");
        aviso.setStyle("-fx-font-weight: bold;");

        montarColunas();
        tabela.setPrefHeight(260);
        tabela.setOnMouseClicked(e -> preencherFormularioComSelecionado());

        VBox centro = new VBox(10);
        centro.setPadding(new Insets(10));
        centro.getChildren().addAll(aviso, tabela);
        raiz.setCenter(centro);

        // ---- Formulario e botoes: SOMENTE para o ADMIN ----
        if (Sessao.isAdmin()) {
            raiz.setBottom(montarFormulario());
            carregarCombos();
        }

        carregarTabela();

        stage.setScene(new Scene(raiz, 980, 620));
        stage.setTitle("Gerenciar Emprestimos");
        stage.show();
    }

    private VBox montarFormulario() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        cmbLivro.setPromptText("Selecione o livro");
        cmbUsuario.setPromptText("Selecione o usuario");
        txtMulta.setPromptText("0.00");
        txtMulta.setPrefWidth(100);

        form.add(new Label("Livro:"), 0, 0);
        form.add(cmbLivro, 1, 0);
        form.add(new Label("Usuario:"), 2, 0);
        form.add(cmbUsuario, 3, 0);
        form.add(new Label("Data emprestimo:"), 0, 1);
        form.add(dpEmprestimo, 1, 1);
        form.add(new Label("Devolucao prevista:"), 2, 1);
        form.add(dpDevolucao, 3, 1);
        form.add(new Label("Multa (R$):"), 0, 2);
        form.add(txtMulta, 1, 2);
        form.add(chkDevolvido, 3, 2);

        btnNovo.setOnAction(e -> limparFormulario());
        btnSalvar.setOnAction(e -> salvar());
        btnExcluir.setOnAction(e -> excluir());

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER_RIGHT);
        botoes.setPadding(new Insets(0, 10, 10, 10));
        botoes.getChildren().addAll(btnNovo, btnSalvar, btnExcluir);

        VBox rodape = new VBox(5);
        Label tituloForm = new Label("Dados do Emprestimo");
        tituloForm.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 10;");
        rodape.getChildren().addAll(tituloForm, form, botoes);
        return rodape;
    }

    private void montarColunas() {
        TableColumn<Emprestimo, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        // "tituloLivro" e "nomeUsuario" vem dos JOINs feitos no DAO.
        TableColumn<Emprestimo, String> colLivro = new TableColumn<>("Livro");
        colLivro.setCellValueFactory(new PropertyValueFactory<>("tituloLivro"));
        colLivro.setPrefWidth(230);

        TableColumn<Emprestimo, String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("nomeUsuario"));
        colUsuario.setPrefWidth(140);

        TableColumn<Emprestimo, java.time.LocalDate> colData = new TableColumn<>("Emprestimo");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataEmprestimo"));
        colData.setPrefWidth(100);

        TableColumn<Emprestimo, java.time.LocalDate> colDev = new TableColumn<>("Devolucao");
        colDev.setCellValueFactory(new PropertyValueFactory<>("dataDevolucao"));
        colDev.setPrefWidth(100);

        // "situacao" chama getSituacao() -> "Devolvido" ou "Em aberto".
        TableColumn<Emprestimo, String> colSituacao = new TableColumn<>("Situacao");
        colSituacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));
        colSituacao.setPrefWidth(90);

        TableColumn<Emprestimo, java.math.BigDecimal> colMulta = new TableColumn<>("Multa");
        colMulta.setCellValueFactory(new PropertyValueFactory<>("valorMulta"));
        colMulta.setPrefWidth(80);

        tabela.getColumns().addAll(colId, colLivro, colUsuario, colData,
                colDev, colSituacao, colMulta);
    }

    /** A regra de perfil mora no Controle: listar() ja devolve a lista certa. */
    private void carregarTabela() {
        List<Emprestimo> lista = control.listar();
        tabela.setItems(FXCollections.observableArrayList(lista));
    }

    private void carregarCombos() {
        cmbLivro.setItems(FXCollections.observableArrayList(livroControl.listar("")));
        cmbUsuario.setItems(FXCollections.observableArrayList(usuarioControl.listar("")));
    }

    private void preencherFormularioComSelecionado() {
        Emprestimo e = tabela.getSelectionModel().getSelectedItem();
        if (e == null) {
            return;
        }
        // O perfil COMUM nao tem formulario nesta tela; nada a preencher.
        if (!Sessao.isAdmin()) {
            return;
        }
        selecionado = e;

        // Localiza nos combos o livro e o usuario do emprestimo clicado.
        for (Livro l : cmbLivro.getItems()) {
            if (l.getId().equals(e.getIdLivro())) {
                cmbLivro.setValue(l);
                break;
            }
        }
        for (Usuario u : cmbUsuario.getItems()) {
            if (u.getId().equals(e.getIdUsuario())) {
                cmbUsuario.setValue(u);
                break;
            }
        }
        dpEmprestimo.setValue(e.getDataEmprestimo());
        dpDevolucao.setValue(e.getDataDevolucao());
        chkDevolvido.setSelected(Boolean.TRUE.equals(e.getStatus()));
        txtMulta.setText(e.getValorMulta() == null ? "0" : e.getValorMulta().toString());

        // Em edicao nao se troca o livro nem o usuario do emprestimo.
        cmbLivro.setDisable(true);
        cmbUsuario.setDisable(true);
    }

    private void limparFormulario() {
        selecionado = null;
        cmbLivro.setValue(null);
        cmbUsuario.setValue(null);
        dpEmprestimo.setValue(null);
        dpDevolucao.setValue(null);
        chkDevolvido.setSelected(false);
        txtMulta.clear();
        cmbLivro.setDisable(false);
        cmbUsuario.setDisable(false);
        tabela.getSelectionModel().clearSelection();
        // Recarrega os combos: a disponibilidade dos livros pode ter mudado.
        carregarCombos();
    }

    private void salvar() {
        Long id = (selecionado == null) ? null : selecionado.getId();

        List<String> erros = control.salvar(id, cmbLivro.getValue(), cmbUsuario.getValue(),
                dpEmprestimo.getValue(), dpDevolucao.getValue(),
                chkDevolvido.isSelected(), txtMulta.getText());

        if (erros.isEmpty()) {
            new Alert(AlertType.INFORMATION, "Emprestimo salvo com sucesso!").show();
            limparFormulario();
            carregarTabela();
        } else {
            mostrarErros(erros);
        }
    }

    private void excluir() {
        if (selecionado == null) {
            new Alert(AlertType.WARNING, "Clique em um emprestimo na tabela para excluir.").show();
            return;
        }
        Alert confirma = new Alert(AlertType.CONFIRMATION,
                "Excluir o emprestimo do livro '" + selecionado.getTituloLivro() + "'?");
        confirma.showAndWait().ifPresent(botao -> {
            if (botao == ButtonType.OK) {
                // Passa tambem o id do livro: ao excluir, o livro eh liberado.
                String erro = control.excluir(selecionado.getId(), selecionado.getIdLivro());
                if (erro == null) {
                    new Alert(AlertType.INFORMATION, "Emprestimo excluido.").show();
                    limparFormulario();
                    carregarTabela();
                } else {
                    new Alert(AlertType.ERROR, erro).show();
                }
            }
        });
    }

    private void mostrarErros(List<String> erros) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Validacao");
        alerta.setHeaderText("Corrija os seguintes campos:");
        alerta.setContentText(String.join("\n", erros));
        alerta.show();
    }
}
