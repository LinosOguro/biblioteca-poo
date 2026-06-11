package edu.curso.view;

import edu.curso.control.LoginControl;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * CAMADA FRONTEIRA (Boundary) - Tela de Login (porta de entrada do sistema).
 *
 * Esta eh a UNICA classe que estende Application: o JavaFX exige uma (e
 * apenas uma) classe "principal" por programa, lancada pelo App.main().
 * As demais telas sao classes comuns que recebem um Stage e o exibem.
 *
 * Fluxo do login:
 *  1. O usuario digita nome e senha (senha em PasswordField = oculta).
 *  2. O LoginControl confere no banco; se ok, registra o usuario na Sessao.
 *  3. Com a Sessao preenchida, abrimos a tela principal - que ja sabera
 *     se mostra o menu de ADMIN ou o de usuario COMUM.
 */
public class LoginBoundary extends Application {

    private TextField fNome = new TextField();
    // PasswordField: igual ao TextField, mas exibe bolinhas no lugar do texto.
    private PasswordField fSenha = new PasswordField();

    private Label tNome = new Label("Usuario: ");
    private Label tSenha = new Label("Senha: ");
    private Label tCadastrar = new Label("Primeira vez?");

    private Button bEntrar = new Button("Acessar");

    private LoginControl control = new LoginControl();

    @Override
    public void start(Stage stage) {
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(20));

        VBox vbPrincipal = new VBox(30);
        vbPrincipal.setAlignment(Pos.CENTER);

        Label titulo = new Label("Sistema de Biblioteca");
        titulo.setStyle("-fx-font-size: 28; -fx-font-weight: bold;");

        // ---- Campos de usuario e senha ----
        VBox vbCampos = new VBox(15);
        vbCampos.setAlignment(Pos.CENTER);

        HBox hbNome = new HBox(10);
        hbNome.setAlignment(Pos.CENTER);
        tNome.setPrefWidth(80);
        fNome.setPrefWidth(250);
        hbNome.getChildren().addAll(tNome, fNome);

        HBox hbSenha = new HBox(10);
        hbSenha.setAlignment(Pos.CENTER);
        tSenha.setPrefWidth(80);
        fSenha.setPrefWidth(250);
        hbSenha.getChildren().addAll(tSenha, fSenha);

        vbCampos.getChildren().addAll(hbNome, hbSenha);

        // ---- Botao de acesso ----
        bEntrar.setPrefSize(120, 45);
        HBox hbBotoes = new HBox(15);
        hbBotoes.setAlignment(Pos.CENTER);
        hbBotoes.getChildren().add(bEntrar);

        tCadastrar.setStyle("-fx-text-fill: #0078d4; -fx-cursor: hand;");

        vbPrincipal.getChildren().addAll(titulo, vbCampos, hbBotoes, tCadastrar);
        bp.setCenter(vbPrincipal);

        // ---- Acao do login ----
        bEntrar.setOnAction(event -> {
            // O Controle valida, confere no banco e registra na Sessao.
            // Devolve null em caso de sucesso, ou a mensagem de erro.
            String erro = control.autenticar(fNome.getText(), fSenha.getText());

            if (erro == null) {
                new PrincipalBoundary().start(new Stage());  // abre o menu
                stage.close();                               // fecha o login
            } else {
                new Alert(AlertType.WARNING, erro).show();
                fSenha.clear();
            }
        });

        // Cadastros sao feitos pelo administrador na tela de Usuarios.
        tCadastrar.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                new Alert(AlertType.INFORMATION,
                        "Procure o administrador da biblioteca para criar sua conta.").show();
            }
        });

        stage.setScene(new Scene(bp, 800, 500));
        stage.setTitle("Login - Sistema de Biblioteca");
        stage.show();
    }
}
