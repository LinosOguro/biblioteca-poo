package edu.curso.view;

import java.util.List;

import edu.curso.control.UsuarioControl;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
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
 * CAMADA FRONTEIRA (Boundary) - Tela de CRUD de Usuarios.
 * Segue a mesma receita da AutorBoundary (leia os comentarios de la).
 *
 * Particularidades desta tela:
 *  - Acesso EXCLUSIVO do ADMIN (eh aqui que se criam as contas do sistema).
 *  - Campo senha usa PasswordField (mostra bolinhas em vez do texto).
 *  - ComboBox de PERFIL define o nivel de acesso: ADMIN ou COMUM.
 *  - A senha NAO aparece como coluna da tabela (boa pratica de seguranca).
 */
public class UsuarioBoundary {

    private UsuarioControl control = new UsuarioControl();

    private Usuario selecionado = null;

    private TableView<Usuario> tabela = new TableView<>();
    private TextField txtBusca = new TextField();

    private TextField txtNome = new TextField();
    private TextField txtEmail = new TextField();
    private TextField txtTelefone = new TextField();
    private TextField txtCpf = new TextField();
    private PasswordField txtSenha = new PasswordField();
    private ComboBox<String> cmbPerfil = new ComboBox<>();

    private Button btnNovo = new Button("Novo");
    private Button btnSalvar = new Button("Salvar");
    private Button btnExcluir = new Button("Excluir");

    public void start(Stage stage) {
        // ---- SEGURANCA: somente ADMIN gerencia usuarios. ----
        if (!Sessao.isAdmin()) {
            new Alert(AlertType.WARNING, "Acesso restrito ao administrador.").show();
            return;
        }

        BorderPane raiz = new BorderPane();
        raiz.setTop(AppMenu.criarMenu(stage));

        // ---- Centro: busca + tabela ----
        txtBusca.setPromptText("Buscar por nome...");
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

        // ---- Formulario + botoes ----
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        txtNome.setPrefWidth(300);
        // O ComboBox de perfil tem apenas os dois valores aceitos pelo sistema.
        cmbPerfil.setItems(FXCollections.observableArrayList("ADMIN", "COMUM"));
        cmbPerfil.setPromptText("Selecione o perfil");

        form.add(new Label("Nome (login):"), 0, 0);
        form.add(txtNome, 1, 0);
        form.add(new Label("Email:"), 2, 0);
        form.add(txtEmail, 3, 0);
        form.add(new Label("Telefone:"), 0, 1);
        form.add(txtTelefone, 1, 1);
        form.add(new Label("CPF:"), 2, 1);
        form.add(txtCpf, 3, 1);
        form.add(new Label("Senha:"), 0, 2);
        form.add(txtSenha, 1, 2);
        form.add(new Label("Perfil:"), 2, 2);
        form.add(cmbPerfil, 3, 2);

        btnNovo.setOnAction(e -> limparFormulario());
        btnSalvar.setOnAction(e -> salvar());
        btnExcluir.setOnAction(e -> excluir());

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER_RIGHT);
        botoes.setPadding(new Insets(0, 10, 10, 10));
        botoes.getChildren().addAll(btnNovo, btnSalvar, btnExcluir);

        VBox rodape = new VBox(5);
        Label tituloForm = new Label("Dados do Usuario");
        tituloForm.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 10;");
        rodape.getChildren().addAll(tituloForm, form, botoes);
        raiz.setBottom(rodape);

        carregarTabela("");

        stage.setScene(new Scene(raiz, 950, 600));
        stage.setTitle("Gerenciar Usuarios");
        stage.show();
    }

    private void montarColunas() {
        TableColumn<Usuario, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Usuario, String> colNome = new TableColumn<>("Nome (login)");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setPrefWidth(180);

        TableColumn<Usuario, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(220);

        TableColumn<Usuario, String> colTel = new TableColumn<>("Telefone");
        colTel.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colTel.setPrefWidth(110);

        TableColumn<Usuario, String> colCpf = new TableColumn<>("CPF");
        colCpf.setCellValueFactory(new PropertyValueFactory<>("cpf"));
        colCpf.setPrefWidth(110);

        TableColumn<Usuario, String> colPerfil = new TableColumn<>("Perfil");
        colPerfil.setCellValueFactory(new PropertyValueFactory<>("perfil"));
        colPerfil.setPrefWidth(90);

        // Repare: NAO existe coluna de senha - senha nao se exibe em tela.
        tabela.getColumns().addAll(colId, colNome, colEmail, colTel, colCpf, colPerfil);
    }

    private void carregarTabela(String filtro) {
        List<Usuario> lista = control.listar(filtro);
        tabela.setItems(FXCollections.observableArrayList(lista));
    }

    private void preencherFormularioComSelecionado() {
        Usuario u = tabela.getSelectionModel().getSelectedItem();
        if (u == null) {
            return;
        }
        selecionado = u;
        txtNome.setText(u.getNome());
        txtEmail.setText(u.getEmail());
        txtTelefone.setText(u.getTelefone() == null ? "" : u.getTelefone());
        txtCpf.setText(u.getCpf());
        txtSenha.setText(u.getSenha());
        cmbPerfil.setValue(u.getPerfil());
    }

    private void limparFormulario() {
        selecionado = null;
        txtNome.clear();
        txtEmail.clear();
        txtTelefone.clear();
        txtCpf.clear();
        txtSenha.clear();
        cmbPerfil.setValue(null);
        tabela.getSelectionModel().clearSelection();
    }

    private void salvar() {
        Long id = (selecionado == null) ? null : selecionado.getId();

        List<String> erros = control.salvar(id, txtNome.getText(), txtEmail.getText(),
                txtTelefone.getText(), txtCpf.getText(), txtSenha.getText(),
                cmbPerfil.getValue());

        if (erros.isEmpty()) {
            new Alert(AlertType.INFORMATION, "Usuario salvo com sucesso!").show();
            limparFormulario();
            carregarTabela(txtBusca.getText());
        } else {
            mostrarErros(erros);
        }
    }

    private void excluir() {
        if (selecionado == null) {
            new Alert(AlertType.WARNING, "Clique em um usuario na tabela para excluir.").show();
            return;
        }
        Alert confirma = new Alert(AlertType.CONFIRMATION,
                "Excluir o usuario '" + selecionado.getNome() + "'?");
        confirma.showAndWait().ifPresent(botao -> {
            if (botao == ButtonType.OK) {
                String erro = control.excluir(selecionado.getId());
                if (erro == null) {
                    new Alert(AlertType.INFORMATION, "Usuario excluido.").show();
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
