package src.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import src.core.Constants;

/**
 * Représente un projectile tiré par l’AK47 du joueur.
 *
 * <p>Le projectile se déplace en ligne droite selon une vitesse donnée,
 * inflige un certain nombre de dégâts, et disparaît lorsqu’il quitte l’écran
 * ou touche une cible.</p>
 *
 * - La position est définie par (x, y).
 * - La vitesse est définie par (vx, vy).
 * - Le projectile possède une taille fixe (16×4).
 * - Le projectile devient inactif lorsqu'il est "tué" ou sort de la fenêtre.
 */
public class Ak47Bullet {

    private double x, y;
    private double vx, vy;
    private final int width = 16;
    private final int height = 4;

    private final int damage;
    private boolean alive = true;

    /**
     * Crée un projectile de l'AK47.
     *
     * @param x position initiale en X
     * @param y position initiale en Y
     * @param vx vitesse horizontale
     * @param vy vitesse verticale
     * @param damage dégâts infligés à l’impact
     */
    public Ak47Bullet(double x, double y, double vx, double vy, int damage) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
    }

    /**
     * Met à jour la position du projectile.
     *
     * @param dt delta time utilisé pour le déplacement
     */
    public void update(double dt) {
        if (!alive) return;

        x += vx * dt;
        y += vy * dt;

        // Si la balle sort de l'écran, on la détruit
        if (x < -50 || x > Constants.WINDOW_WIDTH + 50
                || y < -50 || y > Constants.WINDOW_HEIGHT + 50) {
            alive = false;
        }
    }

    /**
     * Affiche le projectile à l’écran.
     *
     * @param g contexte graphique
     */
    public void render(Graphics2D g) {
        if (!alive) return;

        g.setColor(Color.YELLOW);
        g.fillRect((int)x, (int)y, width, height);
    }

    /**
     * Teste l’intersection entre ce projectile et un rectangle (souvent un ennemi).
     *
     * @param rx position X du rectangle
     * @param ry position Y du rectangle
     * @param rwidth largeur du rectangle
     * @param rheight hauteur du rectangle
     * @return true si une collision est détectée
     */
    public boolean intersects(int rx, int ry, int rwidth, int rheight) {
        return rx < x + width &&
                rx + rwidth > x &&
                ry < y + height &&
                ry + rheight > y;
    }

    /**
     * @return les dégâts infligés par ce projectile
     */
    public int getDamage() {
        return damage;
    }

    /**
     * @return true si le projectile est encore actif
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Détruit le projectile (le rend inactif).
     */
    public void kill() {
        this.alive = false;
    }
}
