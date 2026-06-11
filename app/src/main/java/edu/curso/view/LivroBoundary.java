package edu.curso.view;

import java.util.List;

import edu.curso.control.AutorControl;
import edu.curso.control.LivroControl;
import edu.curso.model.Autor;
import edu.curso.model.Livro;
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
 * CAMADA FRONTEIRA (Boundary) - Tela de CRUD de Livros.
 * Segue a mesma receita da AutorBoundary (leia os comentarios de la).
 *
 * Novidades desta tela:
 *  - ComboBox de AUTOR: o usuario escolhe o autor numa lista de objetos
 *    Autor (o texto exibido vem do toString() do model).
 *  - CheckBox "Disponivel": vira o boolean "status" do livro.
 *  - PERFIS: o usuario COMUM apenas CONSULTA o acervo - para ele, o
 *    formulario e os botoes de edicao nem sao adicionados a tela.
 */
public class LivroBoundary {

    private LivroControl control = new LivroControl();
    // Usado apenas para carregar a lista de autores do ComboBox.
    private AutorControl autorControl = new AutorControl();

    private Livro selecionado = null;

    private TableView<Livro> tabela = new TableView<>();
    private TextField txtBusca = new TextField();

    private TextField txtTitulo = new TextField();
    private TextField txtIsbn = new TextField();
    private TextField txtAno = new TextField();
    private TextField txtPaginas = new TextField();
    private TextField txtIdioma = new TextField();
    private CheckBox chkDisponivel = new CheckBox("Disponivel para emprestimo");
    private ComboBox<Autor> cmbAutor = new ComboBox<>();

    private Button btnNovo = new Button("Novo");
    private Button btnSalvar = new Button("Salvar");
    private Button btnExcluir = new Button("Excluir");

    public void start(Stage stage) {
        BorderPane raiz = new BorderPane();
        raiz.setTop(AppMenu.criarMenu(stage));

        // ---- Centro: busca + tabela (visivel para TODOS os perfis) ----
        txtBusca.setPromptText("Buscar por titulo...");
        txtBusca.setPrefWidth(300);
        Button btnBuscar = new Button("Buscar");
        btnBuscar.setOnAction(e -> carregarTabela(txtBusca.getText()));

        HBox linhaBusca = new HBox(10);
        linhaBusca.setAlignment(Pos.CENTER_LEFT);
        linhaBusca.getChildren().addAll(new Label("Pesquisa:"), txtBusca, btnBuscar);

        montarColunas();
        tabela.setPrefHeight(250);
        tabela.setOnMouseClicked(e -> preencherFormularioComSelecionado());

        VBox centro = new VBox(10);
        centro.setPadding(new Insets(10));
        centro.getChildren().addAll(linhaBusca, tabela);
        raiz.setCenter(centro);

        // ---- Formulario e botoes: SOMENTE para o ADMIN ----
        // Perfil COMUM ve apenas a consulta (a tabela acima).
        if (Sessao.isAdmin()) {
            raiz.setBottom(montarFormulario());
            carregarAutoresNoCombo();
        }

        carregarTabela("");

        stage.setScene(new Scene(raiz, 950, 620));
        stage.setTitle("Gerenciar Livros");
        stage.show();
    }

    /** Monta a parte de baixo da tela (form + botoes), usada so pelo ADMIN. */
    private VBox montarFormulario() {
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        txtTitulo.setPrefWidth(300);
        txtAno.setPrefWidth(80);
        txtPaginas.setPrefWidth(80);
        cmbAutor.setPromptText("Selecione o autor");

        form.add(new Label("Titulo:"), 0, 0);
        form.add(txtTitulo, 1, 0);
        form.add(new Label("ISBN:"), 2, 0);
        form.add(txtIsbn, 3, 0);
        form.add(new Label("Ano:"), 0, 1);
        form.add(txtAno, 1, 1);
        form.add(new Label("Paginas:"), 2, 1);
        form.add(txtPaginas, 3, 1);
        form.add(new Label("Idioma:"), 0, 2);
        form.add(txtIdioma, 1, 2);
        form.add(new Label("Autor:"), 2, 2);
        form.add(cmbAutor, 3, 2);
        form.add(chkDisponivel, 1, 3);

        btnNovo.setOnAction(e -> limparFormulario());
        btnSalvar.setOnAction(e -> salvar());
        btnExcluir.setOnAction(e -> excluir());

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER_RIGHT);
        botoes.setPadding(new Insets(0, 10, 10, 10));
        botoes.getChildren().addAll(btnNovo, btnSalvar, btnExcluir);

        VBox rodape = new VBox(5);
        Label tituloForm = new Label("Dados do Livro");
        tituloForm.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 10;");
        rodape.getChildren().addAll(tituloForm, form, botoes);
        return rodape;
    }

    private void montarColunas() {
        TableColumn<Livro, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);

        TableColumn<Livro, String> colTitulo = new TableColumn<>("Titulo");
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTitulo.setPrefWidth(230);

        TableColumn<Livro, String> colIsbn = new TableColumn<>("ISBN");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colIsbn.setPrefWidth(120);

        TableColumn<Livro, Integer> colAno = new TableColumn<>("Ano");
        colAno.setCellValueFactory(new PropertyValueFactory<>("anoPublicacao"));
        colAno.setPrefWidth(60);

        TableColumn<Livro, Integer> colPag = new TableColumn<>("Paginas");
        colPag.setCellValueFactory(new PropertyValueFactory<>("numPaginas"));
        colPag.setPrefWidth(70);

        TableColumn<Livro, String> colIdioma = new TableColumn<>("Idioma");
        colIdioma.setCellValueFactory(new PropertyValueFactory<>("idioma"));
        colIdioma.setPrefWidth(90);

        // "situacao" chama getSituacao() -> "Disponivel" ou "Emprestado".
        TableColumn<Livro, String> colSituacao = new TableColumn<>("Situacao");
        colSituacao.setCellValueFactory(new PropertyValueFactory<>("situacao"));
        colSituacao.setPrefWidth(90);

        // "nomeAutor" vem preenchido pelo JOIN feito no DAO.
        TableColumn<Livro, String> colAutor = new TableColumn<>("Autor");
        colAutor.setCellValueFactory(new PropertyValueFactory<>("nomeAutor"));
        colAutor.setPrefWidth(170);

        tabela.getColumns().addAll(colId, colTitulo, colIsbn, colAno, colPag,
                colIdioma, colSituacao, colAutor);
    }

    private void carregarTabela(String filtro) {
        List<Livro> lista = control.listar(filtro);
        tabela.setItems(FXCollections.observableArrayList(lista));
    }

    /** Busca todos os autores e coloca no ComboBox. */
    private void carregarAutoresNoCombo() {
        List<Autor> autores = autorControl.listar("");
        cmbAutor.setItems(FXCollections.observableArrayList(autores));
    }

    private void preencherFormularioComSelecionado() {
        Livro l = tabela.getSelectionModel().getSelectedItem();
        if (l == null) {
            return;
        }
        selecionado = l;
        txtTitulo.setText(l.getTitulo());
        txtIsbn.setText(l.getIsbn());
        txtAno.setText(String.valueOf(l.getAnoPublicacao()));
        txtPaginas.setText(String.valueOf(l.getNumPaginas()));
        txtIdioma.setText(l.getIdioma());
        chkDisponivel.setSelected(Boolean.TRUE.equals(l.getStatus()));
        // Para selecionar o autor certo no combo, procuramos na lista de
        // itens aquele cujo id eh igual ao id_autor do livro clicado.
        for (Autor a : cmbAutor.getItems()) {
            if (a.getId().equals(l.getIdAutor())) {
                cmbAutor.setValue(a);
                break;
            }
        }
    }

    private void limparFormulario() {
        selecionado = null;
        txtTitulo.clear();
        txtIsbn.clear();
        txtAno.clear();
        txtPaginas.clear();
        txtIdioma.clear();
        chkDisponivel.setSelected(true);   // um livro novo costuma entrar disponivel
        cmbAutor.setValue(null);
        tabela.getSelectionModel().clearSelection();
    }

    private void salvar() {
        Long id = (selecionado == null) ? null : selecionado.getId();

        List<String> erros = control.salvar(id, txtTitulo.getText(), txtIsbn.getText(),
                txtAno.getText(), txtPaginas.getText(), txtIdioma.getText(),
                chkDisponivel.isSelected(), cmbAutor.getValue());

        if (erros.isEmpty()) {
            new Alert(AlertType.INFORMATION, "Livro salvo com sucesso!").show();
            limparFormulario();
            carregarTabela(txtBusca.getText());
        } else {
            mostrarErros(erros);
        }
    }

    private void excluir() {
        if (selecionado == null) {
            new Alert(AlertType.WARNING, "Clique em um livro na tabela para excluir.").show();
            return;
        }
        Alert confirma = new Alert(AlertType.CONFIRMATION,
                "Excluir o livro '" + selecionado.getTitulo() + "'?");
        confirma.showAndWait().ifPresent(botao -> {
            if (botao == ButtonType.OK) {
                String erro = control.excluir(selecionado.getId());
                if (erro == null) {
                    new Alert(AlertType.INFORMATION, "Livro excluido.").show();
                    limparFormulario();
                    carregarTabela(txtBusca.getText());
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
