package edu.curso.view;

import edu.curso.model.Usuario;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class AppMenu {

    public static MenuBar createMenuBar(Stage stage, Usuario usuario) {
        MenuBar menuBar = new MenuBar();

        Menu menuNavegar = new Menu("Navegar");
        MenuItem mInicio = new MenuItem("Início");
        MenuItem mLivros = new MenuItem("Livros");
        MenuItem mAutores = new MenuItem("Autores");
        MenuItem mEmprestimos = new MenuItem("Empréstimos");

        mInicio.setOnAction(e -> openPrincipal(stage, usuario));
        mLivros.setOnAction(e -> openLivro(stage, usuario));
        mAutores.setOnAction(e -> openAutor(stage, usuario));
        mEmprestimos.setOnAction(e -> openEmprestimo(stage, usuario));

        menuNavegar.getItems().addAll(mInicio, mLivros, mAutores, mEmprestimos);

        Menu menuAplicacao = new Menu("Aplicação");
        MenuItem mSair = new MenuItem("Logout");
        mSair.setOnAction(e -> {
            stage.close();
            try {
                new LoginBoundary().start(new Stage());
            } catch (Exception ex) {
                showError("Erro ao retornar para o login: " + ex.getMessage());
            }
        });
        menuAplicacao.getItems().add(mSair);

        menuBar.getMenus().addAll(menuNavegar, menuAplicacao);
        return menuBar;
    }

    private static void openPrincipal(Stage stage, Usuario usuario) {
        try {
            new PrincipalBoundary().start(new Stage(), usuario);
            stage.close();
        } catch (Exception ex) {
            showError("Erro ao abrir tela inicial: " + ex.getMessage());
        }
    }

    private static void openLivro(Stage stage, Usuario usuario) {
        try {
            new LivroBoundary().start(new Stage(), usuario);
            stage.close();
        } catch (Exception ex) {
            showError("Erro ao abrir gerenciador de livros: " + ex.getMessage());
        }
    }

    private static void openAutor(Stage stage, Usuario usuario) {
        try {
            new AutorBoundary().start(new Stage(), usuario);
            stage.close();
        } catch (Exception ex) {
            showError("Erro ao abrir gerenciador de autores: " + ex.getMessage());
        }
    }

    private static void openEmprestimo(Stage stage, Usuario usuario) {
        try {
            new EmprestimoBoundary().start(new Stage(), usuario);
            stage.close();
        } catch (Exception ex) {
            showError("Erro ao abrir gerenciador de empréstimos: " + ex.getMessage());
        }
    }

    private static void showError(String message) {
        new Alert(AlertType.ERROR, message).show();
    }
}
