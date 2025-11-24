package src.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Classe de base abstraite représentant une entité vivante dans le jeu.
 *
 * <p>Une entité vivante peut être :</p>
 * <ul>
 *     <li>Le joueur</li>
 *     <li>Un ennemi</li>
 * </ul>
 *
 * <p>Cette classe gère automatiquement :</p>
 * <ul>
 *     <li>La position et la taille</li>
 *     <li>La vie (HP) et les dégâts</li>
 *     <li>La vitesse de déplacement</li>
 *     <li>La force de saut</li>
 *     <li>La représentation basique de la barre de vie</li>
 * </ul>
 *
 * <p>Les classes dérivées doivent implémenter :</p>
 * <ul>
 *     <li>{@link #update(double)} — logique par frame</li>
 *     <li>{@link #render(Graphics2D)} — affichage</li>
 * </ul>
 */
public abstract class LivingEntity {

    /** Position X de l'entité dans le monde du jeu (en pixels). */
    protected double x;

    /** Position Y de l'entité dans le monde du jeu (en pixels). */
    protected double y;

    /** Largeur de l'entité (en pixels). */
    protected double width;

    /** Hauteur de l'entité (en pixels). */
    protected double height;

    /** Points de vie maximum de l'entité. */
    protected int maxHealth;

    /** Points de vie actuels. */
    protected int health;

    /** Dégâts infligés par l'entité lorsqu'elle attaque. */
    protected int damage;

    /** Vitesse de déplacement horizontal (en unités de jeu). */
    protected double moveSpeed;

    /** Force appliquée lors d’un saut. */
    protected double jumpForce;

    /** Vitesse horizontale (pixels/s). */
    protected double vx = 0;

    /** Vitesse verticale (pixels/s). */
    protected double vy = 0;

    /**
     * Construit une entité vivante avec des valeurs de base.
     *
     * @param x          position initiale en X
     * @param y          position initiale en Y
     * @param width      largeur de l'entité
     * @param height     hauteur de l'entité
     * @param maxHealth  vie maximale
     * @param damage     dégâts infligés
     * @param moveSpeed  vitesse horizontale
     * @param jumpForce  force du saut
     */
    public LivingEntity(double x, double y,
                        double width, double height,
                        int maxHealth, int damage,
                        double moveSpeed, double jumpForce) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.maxHealth = maxHealth;
        this.health = maxHealth;

        this.damage = damage;

        this.moveSpeed = moveSpeed;
        this.jumpForce = jumpForce;
    }

    /**
     * Met à jour l'entité.
     *
     * <p>Les classes dérivées doivent définir le comportement
     * de déplacement, IA, collisions, actions, etc.</p>
     *
     * @param dt temps écoulé en secondes depuis la dernière frame
     */
    public abstract void update(double dt);

    /**
     * Dessine l'entité à l'écran en utilisant le contexte graphique fourni.
     *
     * @param g contexte graphique utilisé pour le rendu
     */
    public abstract void render(Graphics2D g);

    /**
     * Inflige des dégâts à l'entité.
     *
     * @param dmg quantité de dégâts reçus
     */
    public void takeDamage(int dmg) {
        health -= dmg;
        if (health < 0) {
            health = 0;
        }
    }

    /**
     * Soigne l'entité.
     *
     * @param amount quantité de HP restaurés
     */
    public void heal(int amount) {
        health += amount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    /**
     * Vérifie si l'entité est morte.
     *
     * @return {@code true} si les HP sont à 0
     */
    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Retourne un rectangle représentant l'entité,
     * utile pour la détection de collisions.
     *
     * @return rectangle de collision
     */
    public Rectangle getRect() {
        return new Rectangle((int)x, (int)y, (int)width, (int)height);
    }

    /**
     * Affiche une barre de vie rouge (fond) et verte (vie restante)
     * au-dessus de l'entité.
     *
     * @param g contexte graphique
     */
    public void renderHealthBar(Graphics2D g) {

        if (maxHealth <= 0) return;

        int barWidth = (int) width;
        int barHeight = 5;

        double ratio = (double) health / maxHealth;
        int greenWidth = (int) (barWidth * ratio);

        int bx = (int) x;
        int by = (int) (y - 10);

        g.setColor(Color.RED);
        g.fillRect(bx, by, barWidth, barHeight);

        g.setColor(Color.GREEN);
        g.fillRect(bx, by, greenWidth, barHeight);
    }

    // -----------------------
    // GETTERS
    // -----------------------

    /** @return position X actuelle */
    public double getX() { return x; }

    /** @return position Y actuelle */
    public double getY() { return y; }

    /** @return largeur de l'entité */
    public double getWidth() { return width; }

    /** @return hauteur de l'entité */
    public double getHeight() { return height; }

    /** @return dégâts infligés par l'entité */
    public int getDamage() { return damage; }

    /** @return centre X de l'entité */
    public double getCenterX() { return x + width / 2.0; }

    /** @return centre Y de l'entité */
    public double getCenterY() { return y + height / 2.0; }
}
