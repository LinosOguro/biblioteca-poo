package edu.curso.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdminBoundary extends Application {

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40;");

        Label title = new Label("Área do Super Usuário");
        title.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        Label info = new Label("Acesso liberado apenas para admin/admin.");
        info.setStyle("-fx-font-size: 14;");

        Button bCadastrarUsuario = new Button("Cadastrar Usuário");
        bCadastrarUsuario.setPrefSize(160, 40);
        bCadastrarUsuario.setOnAction(e -> {
            try {
                new UsuarioBoundary().start(new Stage());
            } catch (Exception ex) {
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Erro ao abrir tela de cadastro: " + ex.getMessage()).show();
            }
        });

        Button bFechar = new Button("Sair");
        bFechar.setPrefSize(120, 40);
        bFechar.setOnAction(e -> stage.close());

        root.getChildren().addAll(title, info, bCadastrarUsuario, bFechar);

        Scene scene = new Scene(root, 520, 260);
        stage.setScene(scene);
        stage.setTitle("Super Usuário");
        stage.show();
    }
}
