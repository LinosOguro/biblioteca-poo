package edu.curso;

import edu.curso.view.LoginBoundary;
import javafx.application.Application;

/**
 * Ponto de entrada do programa.
 *
 * Application.launch() inicializa o JavaFX e chama o metodo start()
 * da LoginBoundary - a partir dai, a navegacao acontece de tela em tela.
 */
public class App {
    public static void main(String[] args) {
        Application.launch(LoginBoundary.class, args);
    }
}
