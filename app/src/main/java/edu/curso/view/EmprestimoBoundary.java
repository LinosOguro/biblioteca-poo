package edu.curso.view;

import edu.curso.model.Usuario;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmprestimoBoundary extends Application {

    private Usuario usuario;

    @Override
    public void start(Stage stage) {
        start(stage, null);
    }

    public void start(Stage stage, Usuario usuario) {
        this.usuario = usuario;

        BorderPane root = new BorderPane();
        root.setTop(AppMenu.createMenuBar(stage, usuario));

        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);

        Label label = new Label("Gerenciador de Empréstimos");
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        vb.getChildren().add(label);
        root.setCenter(vb);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Gerenciar Empréstimos");
        stage.show();
    }
}
