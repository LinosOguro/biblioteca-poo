package edu.curso.view;

import edu.curso.control.UsuarioControl;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UsuarioBoundary extends Application {

    private TextField txtNome = new TextField();
    private TextField txtEmail = new TextField();
    private TextField txtSenha = new TextField();
    private TextField txtTel = new TextField();
    private TextField txtCpf = new TextField();

    private Label tNome = new Label("Digite seu Nome: ");
    private Label tEmail = new Label("Insira seu email: ");
    private Label tSenha = new Label("Insira sua senha: ");
    private Label tTel = new Label("Insira seu telefone: ");
    private Label tCpf = new Label("Insira seu CPF: ");

    private Button bConfirmar = new Button("Confirmar");
    private Button bCancelar = new Button("Cancelar");

    @Override
    public void start(Stage stage) {

        UsuarioControl usuarioControl = new UsuarioControl();

        // Elementos Pane e Scene
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(30));

        // VBox principal para centralização
        VBox vbPrincipal = new VBox(20);
        vbPrincipal.setAlignment(Pos.TOP_CENTER);
        vbPrincipal.setStyle("-fx-padding: 20;");

        // Título
        Label titulo = new Label("Cadastro de Novo Usuário");
        titulo.setStyle("-fx-font-size: 22; -fx-font-weight: bold;");

        // Container para os campos
        VBox vbCampos = new VBox(15);
        vbCampos.setStyle("-fx-padding: 20; -fx-max-width: 500;");
        vbCampos.setAlignment(Pos.TOP_CENTER);

        // Campo Nome
        HBox hbNome = criarCampoHBox("Nome:", txtNome);

        // Campo Email
        HBox hbEmail = criarCampoHBox("Email:", txtEmail);

        // Campo Telefone
        HBox hbTelefone = criarCampoHBox("Telefone:", txtTel);

        // Campo CPF
        HBox hbCpf = criarCampoHBox("CPF:", txtCpf);

        // Campo Senha
        HBox hbSenha = criarCampoHBox("Senha:", txtSenha);

        // Adicionar campos ao VBox
        vbCampos.getChildren().addAll(hbNome, hbEmail, hbTelefone, hbCpf, hbSenha);

        // Container para botões
        HBox hbBotoes = new HBox(15);
        hbBotoes.setAlignment(Pos.CENTER);
        hbBotoes.setStyle("-fx-padding: 20;");

        bCancelar.setPrefSize(100, 40);
        bCancelar.setStyle("-fx-font-size: 12;");
        bConfirmar.setPrefSize(100, 40);
        bConfirmar.setStyle("-fx-font-size: 12;");

        hbBotoes.getChildren().addAll(bCancelar, bConfirmar);

        // Adicionar ao VBox principal
        vbPrincipal.getChildren().addAll(titulo, vbCampos, hbBotoes);

        Scene sc = new Scene(bp, 700, 600);
        bp.setCenter(vbPrincipal);
        BorderPane.setAlignment(vbPrincipal, Pos.TOP_CENTER);

        // Ações
        bCancelar.setOnAction(event -> stage.close());

        bConfirmar.setOnAction(event -> {
            try {
                usuarioControl.cadastrarUsuario(txtNome.getText(), txtEmail.getText(), txtTel.getText(), txtCpf.getText(), txtSenha.getText());
                new Alert(AlertType.INFORMATION, "Usuário cadastrado com sucesso.").show();
                stage.close();
            } catch (IllegalArgumentException e) {
                new Alert(AlertType.WARNING, e.getMessage()).show();
            } catch (RuntimeException e) {
                new Alert(AlertType.ERROR, "Erro ao cadastrar usuário: " + e.getMessage()).show();
            }
        });

        // Inicializar
        stage.setTitle("Cadastro de Usuário");
        stage.setScene(sc);
        stage.show();
    }

    private HBox criarCampoHBox(String labelText, TextField textField) {
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(labelText);
        label.setPrefWidth(100);
        label.setStyle("-fx-font-size: 12;");

        textField.setPrefWidth(300);
        textField.setPrefHeight(35);

        hb.getChildren().addAll(label, textField);
        return hb;
    }
}