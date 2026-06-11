package edu.curso.view;

import java.util.List;

import edu.curso.control.AutorControl;
import edu.curso.model.Autor;
import edu.curso.security.Sessao;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
 * CAMADA FRONTEIRA (Boundary) - Tela de CRUD de Autores.
 *
 * Esta tela eh o "gabarito" dos CRUDs do sistema: LivroBoundary,
 * UsuarioBoundary e EmprestimoBoundary seguem exatamente a mesma receita.
 *
 * COMO A TELA FUNCIONA (fluxo completo):
 *  1. A TableView lista os autores vindos do banco (via AutorControl.listar).
 *  2. Clicar em uma linha COPIA os dados para o formulario e guarda o
 *     objeto em "selecionado" (estamos em modo EDICAO).
 *  3. O botao "Novo" limpa tudo e volta para o modo CADASTRO (selecionado = null).
 *  4. "Salvar" envia os textos crus para o Controle, que valida e persiste.
 *     - lista de erros vazia  -> sucesso: recarrega a tabela e limpa o form.
 *     - lista com mensagens   -> mostra um alerta com UM erro por campo.
 *  5. "Excluir" pede confirmacao e manda o Controle apagar o selecionado.
 *
 * A tela NAO contem regras de negocio nem SQL: ela apenas coleta dados,
 * chama o Controle e exibe resultados. (Principio das camadas separadas.)
 */
public class AutorBoundary {

    // ----- Comunicacao com a camada de Controle -----
    private AutorControl control = new AutorControl();

    // ----- Estado da tela -----
    // Autor clicado na tabela. null = modo cadastro / preenchido = modo edicao.
    private Autor selecionado = null;

    // ----- Componentes visuais -----
    private TableView<Autor> tabela = new TableView<>();
    private TextField txtBusca = new TextField();

    private TextField txtNome = new TextField();
    private TextField txtNacionalidade = new TextField();
    private DatePicker dpDataNasc = new DatePicker();
    private TextField txtEmail = new TextField();
    private TextField txtBiografia = new TextField();

    private Button btnNovo = new Button("Novo");
    private Button btnSalvar = new Button("Salvar");
    private Button btnExcluir = new Button("Excluir");

    public void start(Stage stage) {
        // ---- SEGURANCA: somente ADMIN gerencia autores. ----
        if (!Sessao.isAdmin()) {
            new Alert(AlertType.WARNING, "Acesso restrito ao administrador.").show();
            return;
        }

        BorderPane raiz = new BorderPane();
        raiz.setTop(AppMenu.criarMenu(stage));

        // ---- Centro: barra de busca + tabela ----
        txtBusca.setPromptText("Buscar por nome...");
        txtBusca.setPrefWidth(300);
        Button btnBuscar = new Button("Buscar");
        // Reaproveita o listar(filtro) do controle: com texto vazio, lista todos.
        btnBuscar.setOnAction(e -> carregarTabela(txtBusca.getText()));

        HBox linhaBusca = new HBox(10);
        linhaBusca.setAlignment(Pos.CENTER_LEFT);
        linhaBusca.getChildren().addAll(new Label("Pesquisa:"), txtBusca, btnBuscar);

        montarColunas();
        tabela.setPrefHeight(250);
        // Clique em uma linha -> entra em modo edicao com os dados da linha.
        tabela.setOnMouseClicked(e -> preencherFormularioComSelecionado());

        VBox centro = new VBox(10);
        centro.setPadding(new Insets(10));
        centro.getChildren().addAll(linhaBusca, tabela);
        raiz.setCenter(centro);

        // ---- Parte de baixo: formulario + botoes ----
        GridPane form = new GridPane();
        form.setHgap(10);   // espaco horizontal entre colunas do grid
        form.setVgap(10);   // espaco vertical entre linhas do grid
        form.setPadding(new Insets(10));

        txtNome.setPrefWidth(300);
        txtBiografia.setPrefWidth(640);

        // add(componente, coluna, linha)
        form.add(new Label("Nome:"), 0, 0);
        form.add(txtNome, 1, 0);
        form.add(new Label("Nacionalidade:"), 2, 0);
        form.add(txtNacionalidade, 3, 0);
        form.add(new Label("Nascimento:"), 0, 1);
        form.add(dpDataNasc, 1, 1);
        form.add(new Label("Email:"), 2, 1);
        form.add(txtEmail, 3, 1);
        form.add(new Label("Biografia:"), 0, 2);
        form.add(txtBiografia, 1, 2);

        btnNovo.setOnAction(e -> limparFormulario());
        btnSalvar.setOnAction(e -> salvar());
        btnExcluir.setOnAction(e -> excluir());

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER_RIGHT);
        botoes.setPadding(new Insets(0, 10, 10, 10));
        botoes.getChildren().addAll(btnNovo, btnSalvar, btnExcluir);

        VBox rodape = new VBox(5);
        Label tituloForm = new Label("Dados do Autor");
        tituloForm.setStyle("-fx-font-weight: bold; -fx-padding: 0 0 0 10;");
        rodape.getChildren().addAll(tituloForm, form, botoes);
        raiz.setBottom(rodape);

        carregarTabela("");   // tabela ja abre listando todos

        stage.setScene(new Scene(raiz, 950, 600));
        stage.setTitle("Gerenciar Autores");
        stage.show();
    }

    /** Define as colunas da TableView e liga cada uma a um getter do model. */
    private void montarColunas() {
        // PropertyValueFactory("nome") chama getNome() de cada Autor da lista.
        TableColumn<Autor, Long> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Autor, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setPrefWidth(200);

        TableColumn<Autor, String> colNac = new TableColumn<>("Nacionalidade");
        colNac.setCellValueFactory(new PropertyValueFactory<>("nacionalidade"));
        colNac.setPrefWidth(120);

        TableColumn<Autor, java.time.LocalDate> colNasc = new TableColumn<>("Nascimento");
        colNasc.setCellValueFactory(new PropertyValueFactory<>("dataNasc"));
        colNasc.setPrefWidth(100);

        TableColumn<Autor, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(180);

        TableColumn<Autor, String> colBio = new TableColumn<>("Biografia");
        colBio.setCellValueFactory(new PropertyValueFactory<>("biografia"));
        colBio.setPrefWidth(260);

        tabela.getColumns().addAll(colId, colNome, colNac, colNasc, colEmail, colBio);
    }

    /** Pede a lista ao Controle e joga na tabela. */
    private void carregarTabela(String filtro) {
        List<Autor> lista = control.listar(filtro);
        tabela.setItems(FXCollections.observableArrayList(lista));
    }

    /** Copia os dados da linha clicada para o formulario (modo edicao). */
    private void preencherFormularioComSelecionado() {
        Autor a = tabela.getSelectionModel().getSelectedItem();
        if (a == null) {
            return;   // clique em area vazia da tabela
        }
        selecionado = a;
        txtNome.setText(a.getNome());
        txtNacionalidade.setText(a.getNacionalidade());
        dpDataNasc.setValue(a.getDataNasc());
        txtEmail.setText(a.getEmail());
        txtBiografia.setText(a.getBiografia());
    }

    /** Volta ao modo cadastro: limpa os campos e a selecao. */
    private void limparFormulario() {
        selecionado = null;
        txtNome.clear();
        txtNacionalidade.clear();
        dpDataNasc.setValue(null);
        txtEmail.clear();
        txtBiografia.clear();
        tabela.getSelectionModel().clearSelection();
    }

    /** Envia os dados ao Controle. id null = INSERT / id preenchido = UPDATE. */
    private void salvar() {
        Long id = (selecionado == null) ? null : selecionado.getId();

        List<String> erros = control.salvar(id, txtNome.getText(),
                txtNacionalidade.getText(), dpDataNasc.getValue(),
                txtEmail.getText(), txtBiografia.getText());

        if (erros.isEmpty()) {
            new Alert(AlertType.INFORMATION, "Autor salvo com sucesso!").show();
            limparFormulario();
            carregarTabela(txtBusca.getText());
        } else {
            mostrarErros(erros);
        }
    }

    /** Exclui o autor selecionado, com confirmacao. */
    private void excluir() {
        if (selecionado == null) {
            new Alert(AlertType.WARNING, "Clique em um autor na tabela para excluir.").show();
            return;
        }
        Alert confirma = new Alert(AlertType.CONFIRMATION,
                "Excluir o autor '" + selecionado.getNome() + "'?");
        // showAndWait() espera o usuario responder; so exclui se clicar OK.
        confirma.showAndWait().ifPresent(botao -> {
            if (botao == ButtonType.OK) {
                String erro = control.excluir(selecionado.getId());
                if (erro == null) {
                    new Alert(AlertType.INFORMATION, "Autor excluido.").show();
                    limparFormulario();
                    carregarTabela(txtBusca.getText());
                } else {
                    new Alert(AlertType.ERROR, erro).show();
                }
            }
        });
    }

    /** Mostra todas as mensagens de validacao, uma por linha. */
    private void mostrarErros(List<String> erros) {
        Alert alerta = new Alert(AlertType.ERROR);
        alerta.setTitle("Validacao");
        alerta.setHeaderText("Corrija os seguintes campos:");
        alerta.setContentText(String.join("\n", erros));
        alerta.show();
    }
}
