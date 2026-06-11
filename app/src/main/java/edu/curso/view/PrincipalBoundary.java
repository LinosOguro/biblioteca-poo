package edu.curso.view;

import edu.curso.model.Usuario;
import edu.curso.security.Sessao;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * CAMADA FRONTEIRA (Boundary) - Menu principal apos o login.
 *
 * Aqui os 2 PERFIS DE ACESSO ficam visiveis:
 *   - Livros e Emprestimos: aparecem para TODOS os perfis;
 *   - Autores e Usuarios: aparecem APENAS para o ADMIN.
 *
 * Repare que esta tela nao pergunta nome/perfil para ninguem: ela consulta
 * a Sessao (camada de seguranca), preenchida no momento do login.
 */
public class PrincipalBoundary {

    public void start(Stage stage) {
        BorderPane raiz = new BorderPane();
        raiz.setTop(AppMenu.criarMenu(stage));

        VBox vb = new VBox(20);
        vb.setAlignment(Pos.CENTER);

        // Saudacao personalizada com nome e perfil do usuario logado.
        Usuario logado = Sessao.getUsuarioLogado();
        Label titulo = new Label("Bem-vindo, " + logado.getNome()
                + " (" + logado.getPerfil() + ")");
        titulo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
        vb.getChildren().add(titulo);

        // ---- Botoes visiveis para TODOS os perfis ----
        Button bLivros = criarBotao(Sessao.isAdmin() ? "Gerenciar Livros" : "Consultar Livros");
        bLivros.setOnAction(e -> {
            new LivroBoundary().start(new Stage());
            stage.close();
        });

        Button bEmprestimos = criarBotao(Sessao.isAdmin() ? "Gerenciar Emprestimos" : "Meus Emprestimos");
        bEmprestimos.setOnAction(e -> {
            new EmprestimoBoundary().start(new Stage());
            stage.close();
        });

        vb.getChildren().addAll(bLivros, bEmprestimos);

        // ---- Botoes EXCLUSIVOS do ADMIN ----
        // Para o usuario COMUM, estes botoes nem sao criados.
        if (Sessao.isAdmin()) {
            Button bAutores = criarBotao("Gerenciar Autores");
            bAutores.setOnAction(e -> {
                new AutorBoundary().start(new Stage());
                stage.close();
            });

            Button bUsuarios = criarBotao("Gerenciar Usuarios");
            bUsuarios.setOnAction(e -> {
                new UsuarioBoundary().start(new Stage());
                stage.close();
            });

            vb.getChildren().addAll(bAutores, bUsuarios);
        }

        // ---- Sair: encerra a sessao e volta ao login ----
        Button bSair = criarBotao("Sair");
        bSair.setOnAction(e -> {
            Sessao.logout();   // limpa o usuario logado (seguranca)
            stage.close();
            new LoginBoundary().start(new Stage());
        });
        vb.getChildren().add(bSair);

        raiz.setCenter(vb);

        stage.setScene(new Scene(raiz, 600, 520));
        stage.setTitle("Sistema de Biblioteca - Menu Principal");
        stage.show();
    }

    /** Auxiliar para criar os botoes do menu com o mesmo tamanho/estilo. */
    private Button criarBotao(String texto) {
        Button b = new Button(texto);
        b.setPrefSize(250, 50);
        b.setStyle("-fx-font-size: 14;");
        return b;
    }
}
