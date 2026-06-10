package edu.curso.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmprestimoBoundary extends Application {
    
    @Override
    public void start(Stage stage) {
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        
        Label label = new Label("Gerenciador de Empréstimos");
        label.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        vb.getChildren().add(label);
        
        Scene scene = new Scene(vb, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Gerenciar Empréstimos");
        stage.show();
    }
}
