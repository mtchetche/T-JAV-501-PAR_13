package src.core;

import src.entities.BonusHealth;
import src.entities.BonusShield;
import src.entities.BonusSpeed;
import src.entities.Pickup;
import src.entities.Player;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Gère l'ensemble des bonus (pickups) présents sur la carte.
 *
 * <p>Le système se compose de différents types de bonus :</p>
 * <ul>
 *     <li><b>Bonus de vie</b> ({@link BonusHealth}) : apparaissent régulièrement.</li>
 *     <li><b>Bonus de bouclier</b> ({@link BonusShield}) : apparaissent
 *         lorsqu'un ennemi de type Enemy01 est éliminé (géré dans {@code Game}).</li>
 *     <li><b>Bonus de vitesse</b> ({@link BonusSpeed}) : apparaissent
 *         lorsqu'un ennemi de type Enemy02 est éliminé.</li>
 * </ul>
 *
 * <p>La durée de vie d'un bonus (20 secondes), son clignotement dans
 * les 5 dernières secondes, ainsi que sa désactivation/expiration sont
 * gérés par la classe {@link Pickup} et ses sous-classes.</p>
 *
 * <p>Cette classe se charge uniquement :</p>
 * <ul>
 *     <li>du spawn aléatoire ou contextuel des bonus ;</li>
 *     <li>de la mise à jour individuelle de chaque bonus ;</li>
 *     <li>de la suppression des bonus ramassés ou expirés ;</li>
 *     <li>du rendu graphique.</li>
 * </ul>
 */
public class PickupManager {

    /** Liste de tous les pickups actuellement présents sur la carte. */
    private final ArrayList<Pickup> pickups = new ArrayList<>();

    /** Référence au joueur (pour collisions et application d'effets). */
    private final Player player;

    /** Générateur aléatoire utilisé pour le spawn. */
    private final Random random = new Random();

    /** Temps restant avant le prochain spawn d'un bonus de vie. */
    private double healthSpawnTimer = 0;

    /**
     * Constructeur.
     *
     * @param player le joueur, utilisé pour vérifier les collisions
     */
    public PickupManager(Player player) {
        this.player = player;
        resetHealthSpawnTimer();
    }

    /**
     * Réinitialise le timer de spawn du bonus de vie.
     * Le prochain bonus apparaîtra dans un intervalle aléatoire
     * compris entre 10 et 25 secondes.
     */
    private void resetHealthSpawnTimer() {
        healthSpawnTimer = 10 + random.nextDouble() * 15; // [10 ; 25]s
    }

    /**
     * Met à jour tous les pickups existants.
     *
     * @param dt               temps écoulé depuis la dernière mise à jour (en secondes)
     * @param currentWaveIndex numéro de la vague actuelle (peut influer sur les règles de spawn)
     */
    public void update(double dt, int currentWaveIndex) {

        // Gestion du spawn régulier des bonus de vie
        healthSpawnTimer -= dt;
        if (healthSpawnTimer <= 0) {
            spawnRandomHealthBonus();
            resetHealthSpawnTimer();
        }

        // Mise à jour individuelle de chaque pickup
        for (Pickup p : pickups) {
            p.update(dt, player);
        }

        // Suppression des pickups ramassés ou expirés
        pickups.removeIf(p -> !p.isActive());
    }

    /**
     * Dessine tous les pickups actifs sur la carte.
     *
     * @param g contexte graphique 2D
     */
    public void render(Graphics2D g) {
        for (Pickup p : pickups) {
            p.render(g);
        }
    }

    /**
     * Fait apparaître un bonus de vie à une position aléatoire dans le décor.
     * Le spawn évite les bords de la fenêtre pour ne pas gêner le joueur.
     */
    private void spawnRandomHealthBonus() {
        double x = 100 + random.nextDouble() * (Constants.WINDOW_WIDTH - 200);
        double y = 150 + random.nextDouble() * (Constants.WINDOW_HEIGHT - 300);
        pickups.add(new BonusHealth(x, y));
    }

    /**
     * Fait apparaître un bonus de bouclier.
     *
     * @param x position X du bonus
     * @param y position Y du bonus
     * @param duration durée d'effet après ramassage (en secondes)
     */
    public void spawnShield(double x, double y, double duration) {
        pickups.add(new BonusShield(x, y, duration));
    }

    /**
     * Fait apparaître un bonus de vitesse.
     *
     * @param x position X
     * @param y position Y
     * @param duration durée d'effet après ramassage (en secondes)
     */
    public void spawnSpeed(double x, double y, double duration) {
        pickups.add(new BonusSpeed(x, y, duration));
    }
}
