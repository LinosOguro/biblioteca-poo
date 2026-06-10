package edu.curso.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import edu.curso.control.LoginControl;
import edu.curso.model.Usuario;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginBoundary extends Application {
    
    private TextField fNome = new TextField();
    private TextField fSenha = new TextField();

    private Label tNome = new Label("Usuário: ");
    private Label tSenha = new Label("Senha: ");
    private Label tCadastrar = new Label("Primeira Vez?");

    private Button bEntrar = new Button("Acessar");

    private LoginControl loginUC;
    
    private LoginControl getLoginUC() {
        if (loginUC == null) {
            loginUC = new LoginControl();
        }
        return loginUC;
    }

    public void start(Stage stage){

        System.out.println("========== LOGINUI INICIADO ==========");

        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(20));

        // VBox principal para centralização
        VBox vbPrincipal = new VBox(30);
        vbPrincipal.setAlignment(Pos.CENTER);
        vbPrincipal.setStyle("-fx-padding: 40;");

        // Título
        Label titulo = new Label("Sistema de Biblioteca");
        titulo.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");

        VBox vbImagem = new VBox(20);
        vbImagem.setAlignment(Pos.CENTER);
        vbImagem.getChildren().add(titulo);

        // Container para os campos de entrada
        VBox vbCampos = new VBox(15);
        vbCampos.setAlignment(Pos.CENTER);
        vbCampos.setStyle("-fx-max-width: 400;");

        // Campo de usuário
        HBox hbNome = new HBox(10);
        hbNome.setAlignment(Pos.CENTER_LEFT);
        tNome.setPrefWidth(80);
        tNome.setStyle("-fx-font-size: 12;");
        fNome.setPrefWidth(250);
        fNome.setPrefHeight(40);
        hbNome.getChildren().addAll(tNome, fNome);

        // Campo de senha
        HBox hbSenha = new HBox(10);
        hbSenha.setAlignment(Pos.CENTER_LEFT);
        tSenha.setPrefWidth(80);
        tSenha.setStyle("-fx-font-size: 12;");
        fSenha.setPrefWidth(250);
        fSenha.setPrefHeight(40);
        hbSenha.getChildren().addAll(tSenha, fSenha);

        vbCampos.getChildren().addAll(hbNome, hbSenha);

        // Container para botões
        HBox hbBotoes = new HBox(15);
        hbBotoes.setAlignment(Pos.CENTER);
        bEntrar.setPrefSize(120, 45);
        bEntrar.setStyle("-fx-font-size: 14; -fx-padding: 10;");
        hbBotoes.getChildren().add(bEntrar);

        // Label "Primeira Vez?"
        tCadastrar.setStyle("-fx-text-fill: #0078d4; -fx-font-size: 12; -fx-cursor: hand;");
        tCadastrar.setOnMouseEntered(e -> tCadastrar.setStyle("-fx-text-fill: #005a9e; -fx-font-size: 12; -fx-cursor: hand; -fx-underline: true;"));
        tCadastrar.setOnMouseExited(e -> tCadastrar.setStyle("-fx-text-fill: #0078d4; -fx-font-size: 12; -fx-cursor: hand;"));

        // Adicionar tudo ao VBox principal
        vbPrincipal.getChildren().addAll(vbImagem, vbCampos, hbBotoes, tCadastrar);

        Scene sc = new Scene(bp, 800, 500);
        bp.setCenter(vbPrincipal);
        BorderPane.setAlignment(vbPrincipal, Pos.CENTER);

        // Ações

        bEntrar.setOnAction(event -> { 
            String nome = fNome.getText().trim();
            String senha = fSenha.getText();
            
            if (nome.equals("admin") && senha.equals("admin")) {
                try {
                    new AdminBoundary().start(new Stage());
                    stage.close();
                } catch (Exception e) {
                    new Alert(AlertType.ERROR, "Erro ao abrir área de super usuário: " + e.getMessage()).show();
                }
                return;
            }

            if (nome.isEmpty() || senha.isEmpty()) {
                new Alert(AlertType.WARNING, "Por favor, preencha usuário e senha.").show();
                return;
            }
            
            String resultado = getLoginUC().logar(nome, senha);
            System.out.println("Login attempt -> nome=" + nome + ", resultado=" + resultado);

            switch (resultado) {
                case "LOGIN_OK":
                    Usuario usuario = getLoginUC().autenticar(nome, senha);
                    System.out.println("autenticar() returned: " + usuario);
                    if (usuario != null) {
                        try {
                            PrincipalBoundary principal = new PrincipalBoundary();
                            principal.start(new Stage(), usuario);
                            stage.close();
                        } catch (Exception e) {
                            new Alert(AlertType.ERROR, "Erro ao abrir tela principal: " + e.getMessage()).show();
                        }
                    } else {
                        new Alert(AlertType.ERROR, "Erro interno ao autenticar usuário.").show();
                    }
                    break;
                case "SENHA_INCORRETA":
                    new Alert(AlertType.WARNING, "Senha incorreta. Tente novamente.").show();
                    fSenha.clear();
                    break;
                case "USUARIO_NAO_EXISTE":
                    new Alert(AlertType.INFORMATION, "Usuário não existe. Cadastros são realizados somente pelo administrador.").show();
                    break;
                case "ERRO_BD":
                    new Alert(AlertType.ERROR, "Erro ao acessar o banco de dados. Verifique a conexão.").show();
                    break;
                default:
                    new Alert(AlertType.ERROR, "Erro desconhecido de login.").show();
            }
        });

        tCadastrar.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                new Alert(AlertType.INFORMATION, "Cadastros são realizados somente pelo administrador.").show();
            }
        });

        //Inicia
        stage.setScene(sc);
        stage.setTitle("Login - Sistema de Biblioteca");
        stage.show();
        System.out.println("========== JANELA DE LOGIN EXIBIDA ==========");

    }

    private int mostrarConfirmacao(String titulo, String mensagem) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        return alert.showAndWait().isPresent() ? (alert.getResult().getButtonData().name().equals("OK_DONE") ? 1 : 0) : 0;
    }

}