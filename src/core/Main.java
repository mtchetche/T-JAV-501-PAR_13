package src.core;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import src.input.KeyboardInput;

/**
 * Point d'entrée du jeu.
 *
 * <p>Cette classe initialise tous les composants nécessaires au
 * fonctionnement du jeu :</p>
 *
 * <ol>
 *   <li>le gestionnaire d'entrées clavier ;</li>
 *   <li>la logique du jeu ({@link Game}) ;</li>
 *   <li>le panneau d'affichage ({@link GamePanel}) ;</li>
 *   <li>la fenêtre principale Swing ;</li>
 *   <li>la boucle de jeu ({@link GameLoop}).</li>
 * </ol>
 *
 * <p>L'exécution est lancée dans le thread Swing via
 * {@link SwingUtilities#invokeLater(Runnable)} afin de garantir
 * que toutes les opérations graphiques sont gérées dans l'EDT
 * (Event Dispatch Thread).</p>
 */
public class Main {

    /**
     * Méthode principale : initialise l'interface utilisateur et démarre
     * la boucle de jeu.
     *
     * @param args arguments de ligne de commande (non utilisés)
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            KeyboardInput keyboardInput = new KeyboardInput();

            Game game = new Game(keyboardInput);

            GamePanel gamePanel = new GamePanel(game);

            JFrame window = new JFrame(Constants.WINDOW_TITLE);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setContentPane(gamePanel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setResizable(false);

            window.setVisible(true);

            gamePanel.addKeyListener(keyboardInput);
            gamePanel.setFocusable(true);
            gamePanel.requestFocusInWindow();

            GameLoop gameLoop = new GameLoop(game, gamePanel);
            gameLoop.start();
        });
    }
}
