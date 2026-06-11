package edu.curso.view;

import edu.curso.security.Sessao;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/**
 * CAMADA FRONTEIRA (Boundary) - Barra de menu compartilhada pelas telas.
 *
 * Classe utilitaria (metodo estatico): cada tela chama
 * AppMenu.criarMenu(stage) e coloca a barra no topo do BorderPane.
 *
 * A barra tambem respeita os PERFIS: os itens "Autores" e "Usuarios"
 * so sao adicionados quando o usuario logado eh ADMIN.
 */
public class AppMenu {

    public static MenuBar criarMenu(Stage stage) {
        MenuBar barra = new MenuBar();

        // ---- Menu Navegar ----
        Menu menuNavegar = new Menu("Navegar");

        MenuItem mInicio = new MenuItem("Inicio");
        mInicio.setOnAction(e -> {
            new PrincipalBoundary().start(new Stage());
            stage.close();
        });

        MenuItem mLivros = new MenuItem("Livros");
        mLivros.setOnAction(e -> {
            new LivroBoundary().start(new Stage());
            stage.close();
        });

        MenuItem mEmprestimos = new MenuItem("Emprestimos");
        mEmprestimos.setOnAction(e -> {
            new EmprestimoBoundary().start(new Stage());
            stage.close();
        });

        menuNavegar.getItems().addAll(mInicio, mLivros, mEmprestimos);

        // Itens exclusivos do ADMIN (o COMUM nem ve essas opcoes).
        if (Sessao.isAdmin()) {
            MenuItem mAutores = new MenuItem("Autores");
            mAutores.setOnAction(e -> {
                new AutorBoundary().start(new Stage());
                stage.close();
            });

            MenuItem mUsuarios = new MenuItem("Usuarios");
            mUsuarios.setOnAction(e -> {
                new UsuarioBoundary().start(new Stage());
                stage.close();
            });

            menuNavegar.getItems().addAll(mAutores, mUsuarios);
        }

        // ---- Menu Aplicacao ----
        Menu menuAplicacao = new Menu("Aplicacao");
        MenuItem mLogout = new MenuItem("Logout");
        mLogout.setOnAction(e -> {
            Sessao.logout();   // encerra a sessao do usuario
            stage.close();
            new LoginBoundary().start(new Stage());
        });
        menuAplicacao.getItems().add(mLogout);

        barra.getMenus().addAll(menuNavegar, menuAplicacao);
        return barra;
    }
}
