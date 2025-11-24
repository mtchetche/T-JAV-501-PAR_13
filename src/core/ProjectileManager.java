package src.core;

import src.entities.Bullet;
import java.awt.Graphics2D;
import java.util.ArrayList;
import src.world.Level;
import src.entities.Player;

/**
 * Gère tous les projectiles ennemis présents dans la scène.
 * <p>
 * Les projectiles tirés par les ennemis (Enemy02 / Enemy03) sont stockés
 * et mis à jour ici, tandis que les projectiles du joueur (AK47)
 * sont gérés séparément dans {@link Game}.
 * </p>
 *
 * <h2>Fonctionnalités :</h2>
 * <ul>
 *     <li>Ajout de projectiles ennemis.</li>
 *     <li>Déplacement des projectiles en fonction du temps écoulé.</li>
 *     <li>Prise en compte du gel du temps via {@link TimeFreezeManager}
 *         (dt = 0 pendant un Time Freeze).</li>
 *     <li>Détection des collisions avec le joueur.</li>
 *     <li>Suppression automatique des projectiles détruits ou sortis de l'écran.</li>
 * </ul>
 */
public class ProjectileManager {

    /** Liste de tous les projectiles ennemis actifs dans la scène. */
    private final ArrayList<Bullet> bullets = new ArrayList<>();

    /** Référence au niveau courant (utilisé éventuellement pour collisions décor). */
    private final Level level;

    /** Joueur cible — utilisé pour détecter les impacts. */
    private final Player player;

    /** Gestionnaire du gel du temps (timer freeze). */
    private final TimeFreezeManager timeFreezeManager;

    /**
     * Construit un gestionnaire de projectiles ennemis.
     *
     * @param level le niveau courant
     * @param player le joueur à atteindre
     * @param timeFreezeManager gestionnaire du gel du temps (peut être {@code null})
     */
    public ProjectileManager(Level level, Player player, TimeFreezeManager timeFreezeManager) {
        this.level = level;
        this.player = player;
        this.timeFreezeManager = timeFreezeManager;
    }

    /**
     * Ajoute un projectile ennemi à la scène.
     *
     * @param b le projectile à ajouter
     */
    public void addBullet(Bullet b) {
        bullets.add(b);
    }

    /**
     * Met à jour tous les projectiles ennemis.
     * <p>
     * Le déplacement des projectiles utilise {@code dt}, qui est mis à {@code 0}
     * si un gel du temps est actif, empêchant les projectiles de bouger.
     * </p>
     * <p>
     * Cette méthode gère également :
     * <ul>
     *     <li>le déplacement,</li>
     *     <li>la détection de collision avec le joueur,</li>
     *     <li>la suppression des projectiles détruits.</li>
     * </ul>
     *
     * @param dt temps écoulé depuis la dernière frame (en secondes)
     */
    public void update(double dt) {

        // dt utilisé pour le mouvement des projectiles ennemis
        double moveDt = (timeFreezeManager != null && timeFreezeManager.isFrozen()) ? 0.0 : dt;

        for (Bullet b : bullets) {
            b.update(moveDt);

            if (!b.isAlive()) continue;

            // Collision avec le joueur
            if (player.getRect().intersects(
                    (int)b.getX(), (int)b.getY(),
                    (int)b.getWidth(), (int)b.getHeight())) {

                player.takeDamage(b.getDamage());
                b.kill();
            }
        }

        // Nettoyage des projectiles morts
        bullets.removeIf(b -> !b.isAlive());
    }

    /**
     * Dessine tous les projectiles ennemis.
     *
     * @param g contexte graphique utilisé pour le rendu
     */
    public void render(Graphics2D g) {
        for (Bullet b : bullets) {
            b.render(g);
        }
    }
}
