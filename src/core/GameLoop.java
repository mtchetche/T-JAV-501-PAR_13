package src.core;

/**
 * Boucle principale du jeu (Game Loop).
 *
 * <p>Cette classe gère le cycle d'exécution du jeu, composé de :
 * <ul>
 *     <li>La mise à jour de la logique du jeu ({@link Game#update(double)})</li>
 *     <li>Le rafraîchissement graphique ({@link GamePanel#repaint()})</li>
 * </ul>
 *
 * <p>La boucle fonctionne dans un thread dédié, garantissant que
 * l'interface graphique ne soit pas bloquée par les procédures internes du jeu.
 *
 * <h2>Fonctionnement :</h2>
 * <ul>
 *     <li>Utilise un delta-time (dt) calculé via {@code System.nanoTime()}</li>
 *     <li>Boucle tant que le flag {@code running} est actif</li>
 *     <li>Applique un léger sleep (~16ms) pour viser ~60 FPS</li>
 * </ul>
 *
 * <p>La méthode {@link #start()} crée et lance automatiquement
 * un nouveau thread portant le nom "GameLoopThread".
 */
public class GameLoop implements Runnable {

    /** Instance principale du jeu. */
    private final Game game;

    /** Panneau graphique utilisé pour afficher le jeu. */
    private final GamePanel gamePanel;

    /** Indique si la boucle doit continuer à tourner. */
    private boolean running;

    /**
     * Constructeur du GameLoop.
     *
     * @param game       instance du jeu à mettre à jour
     * @param gamePanel  panneau graphique sur lequel dessiner
     */
    public GameLoop(Game game, GamePanel gamePanel) {
        this.game = game;
        this.gamePanel = gamePanel;
        this.running = false;
    }

    /**
     * Démarre la boucle de jeu si elle n'est pas déjà en cours.
     *
     * <p>Cette méthode crée un thread séparé, lance la méthode {@link #run()},
     * et permet au jeu de fonctionner de manière fluide et asynchrone.</p>
     */
    public void start() {
        System.out.println("START CALLED");

        if (!running) {
            running = true;

            Thread t = new Thread(this, "GameLoopThread");
            System.out.println("THREAD CREATED");

            t.start();
            System.out.println("THREAD STARTED");
        }
    }

    /**
     * Méthode principale de la boucle de jeu.
     *
     * <h2>Actions effectuées à chaque tick :</h2>
     * <ul>
     *     <li>Calcul du delta-time (temps écoulé entre deux frames)</li>
     *     <li>Mise à jour du jeu via {@link Game#update(double)}</li>
     *     <li>Rendu via {@link GamePanel#repaint()}</li>
     *     <li>Pause de ~16ms afin de viser 60 FPS</li>
     * </ul>
     *
     * <p>Si une erreur survient pendant le sleep, la boucle s'arrête proprement.</p>
     */
    @Override
    public void run() {

        System.out.println("RUN ENTERED");

        long lastTime = System.nanoTime();

        while (running) {

            System.out.println("LOOP TICK");

            long now = System.nanoTime();
            double dt = (now - lastTime) / 1_000_000_000.0; // en secondes
            lastTime = now;

            game.update(dt);
            gamePanel.repaint();

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (Exception e) {
                e.printStackTrace();
                running = false;
            }
        }

        System.out.println("LOOP EXITED");
    }
}
