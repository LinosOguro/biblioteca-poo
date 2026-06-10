package edu.curso.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import edu.curso.model.Usuario;

public class PrincipalBoundary extends Application {
    
    private Usuario usuarioLogado;
    
    public void start(Stage stage) throws Exception {
        start(stage, null);
    }
    
    public void start(Stage stage, Usuario usuario) throws Exception {
        usuarioLogado = usuario;
        
        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-padding: 20;");
        
        Label titulo = new Label();
        if (usuario != null) {
            titulo.setText("Bem-vindo, " + usuario.getNome() + "!");
        } else {
            titulo.setText("Bem-vindo ao Sistema de Biblioteca!");
        }
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        
        Button bLivros = new Button("Gerenciar Livros");
        bLivros.setPrefSize(250, 50);
        bLivros.setStyle("-fx-font-size: 14;");
        bLivros.setOnAction(e -> {
            try {
                new LivroBoundary().start(new Stage());
            } catch (Exception ex) {
                new Alert(AlertType.ERROR, "Erro ao abrir gerenciador de livros: " + ex.getMessage()).show();
            }
        });
        
        Button bAutores = new Button("Gerenciar Autores");
        bAutores.setPrefSize(250, 50);
        bAutores.setStyle("-fx-font-size: 14;");
        bAutores.setOnAction(e -> {
            try {
                new AutorBoundary().start(new Stage());
            } catch (Exception ex) {
                new Alert(AlertType.ERROR, "Erro ao abrir gerenciador de autores: " + ex.getMessage()).show();
            }
        });
        
        Button bEmprestimos = new Button("Gerenciar Empréstimos");
        bEmprestimos.setPrefSize(250, 50);
        bEmprestimos.setStyle("-fx-font-size: 14;");
        bEmprestimos.setOnAction(e -> {
            try {
                new EmprestimoBoundary().start(new Stage());
            } catch (Exception ex) {
                new Alert(AlertType.ERROR, "Erro ao abrir gerenciador de empréstimos: " + ex.getMessage()).show();
            }
        });
        
        Button bSair = new Button("Sair");
        bSair.setPrefSize(250, 50);
        bSair.setStyle("-fx-font-size: 14;");
        bSair.setOnAction(e -> {
            stage.close();
            try {
                new LoginBoundary().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        vb.getChildren().addAll(titulo, bLivros, bAutores, bEmprestimos, bSair);
        
        Scene scene = new Scene(vb, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Sistema de Biblioteca - Menu Principal");
        stage.show();
    }
    
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }
}
