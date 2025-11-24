package src.entities;

import src.world.Level;
import src.world.Platform;
import src.core.Constants;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Projectile tiré par Enemy02.
 *
 * <p>Le projectile se déplace en ligne droite selon une vitesse donnée,
 * inflige des dégâts au joueur, et disparaît dans les cas suivants :</p>
 *
 * <ul>
 *     <li>sortie de l'écran ;</li>
 *     <li>collision avec une plateforme ;</li>
 *     <li>collision avec le joueur (géré dans ProjectileManager) ;</li>
 *     <li>destruction explicite via {@link #kill()}.</li>
 * </ul>
 *
 * <p>La classe gère uniquement le déplacement, la détection de collision
 * avec le décor, et le rendu graphique du projectile.</p>
 */
public class Bullet {

    /**
     * Position X du projectile.
     */
    private double x;

    /**
     * Position Y du projectile.
     */
    private double y;

    /**
     * Vitesse horizontale.
     */
    private double vx;

    /**
     * Vitesse verticale.
     */
    private double vy;

    /**
     * Largeur du projectile.
     */
    private double width = 10;

    /**
     * Hauteur du projectile.
     */
    private double height = 10;

    /**
     * Dégâts infligés au joueur en cas d'impact.
     */
    private int damage;

    /**
     * Indique si le projectile est toujours actif.
     */
    private boolean alive = true;

    /**
     * Référence vers le niveau afin de tester les collisions
     * contre les plateformes.
     */
    private final Level level;

    /**
     * Crée un projectile doté d'une position, d'une vitesse et d'un montant
     * de dégâts.
     *
     * @param x position horizontale initiale
     * @param y position verticale initiale
     * @param vx vitesse horizontale
     * @param vy vitesse verticale
     * @param damage dégâts infligés au joueur
     * @param level niveau contenant les plateformes
     */
    public Bullet(double x, double y, double vx, double vy, int damage, Level level) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.level = level;
    }

    /**
     * Met à jour la position du projectile et vérifie :
     * <ul>
     *     <li>la sortie de l'écran ;</li>
     *     <li>la collision avec le décor.</li>
     * </ul>
     *
     * @param dt delta time (en secondes)
     */
    public void update(double dt) {
        if (!alive) return;

        x += vx * dt;
        y += vy * dt;

        // Sortie écran
        if (x + width < 0 || x > Constants.WINDOW_WIDTH
                || y + height < 0 || y > Constants.WINDOW_HEIGHT) {
            alive = false;
            return;
        }

        // Collision plateformes
        for (Platform p : level.getPlatforms()) {
            if (p.intersects(x, y, width, height)) {
                alive = false;
                return;
            }
        }
    }

    /**
     * Affiche le projectile sous forme d'ovale cyan.
     *
     * @param g contexte graphique utilisé pour le rendu
     */
    public void render(Graphics2D g) {
        if (!alive) return;

        g.setColor(Color.CYAN);
        g.fillOval((int)x, (int)y, (int)width, (int)height);
    }

    /**
     * @return {@code true} si le projectile est encore actif
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Détruit immédiatement le projectile.
     */
    public void kill() {
        this.alive = false;
    }

    /**
     * @return les dégâts infligés par le projectile
     */
    public int getDamage() {
        return damage;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
