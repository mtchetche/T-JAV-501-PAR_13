package src.entities;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Classe de base représentant une entité générique dans le jeu.
 *
 * <p>Cette classe ne gère ni la vie, ni les dégâts, ni les interactions
 * spécifiques (joueur/ennemi). Elle sert uniquement de fondation commune
 * pour tous les objets visibles dans le monde du jeu.</p>
 *
 * <p>Une entité possède :</p>
 * <ul>
 *     <li>Une position (x, y)</li>
 *     <li>Une taille (width, height)</li>
 *     <li>Une vitesse (vx, vy)</li>
 * </ul>
 *
 * <p>Les entités plus complexes comme le joueur, les ennemis ou les projectiles
 * hériteront de cette classe et surchargeront les méthodes si nécessaire.</p>
 */
public class Entity {

    /** Position horizontale (en pixels) de l'entité dans le monde du jeu. */
    protected double x;

    /** Position verticale (en pixels) de l'entité dans le monde du jeu. */
    protected double y;

    /** Largeur de l'entité (en pixels). */
    protected double width;

    /** Hauteur de l'entité (en pixels). */
    protected double height;

    /** Vitesse horizontale (en pixels par seconde). */
    protected double vx;

    /** Vitesse verticale (en pixels par seconde). */
    protected double vy;

    /**
     * Construit une entité générique avec une position et une taille.
     *
     * @param x      position initiale en X (en pixels)
     * @param y      position initiale en Y (en pixels)
     * @param width  largeur de l'entité (en pixels)
     * @param height hauteur de l'entité (en pixels)
     */
    public Entity(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.vx = 0.0;
        this.vy = 0.0;
    }

    /**
     * Met à jour la position de l'entité selon sa vitesse.
     *
     * <p>Cette méthode utilise le temps écoulé depuis la dernière frame
     * pour effectuer un déplacement indépendant du framerate.</p>
     *
     * @param dt temps écoulé en secondes depuis la dernière frame
     */
    public void update(double dt) {
        x += vx * dt;
        y += vy * dt;
    }

    /**
     * Dessine l’entité à l’écran.
     *
     * <p>Par défaut, l’entité est représentée par un simple rectangle blanc.
     * Les sous-classes peuvent surcharger cette méthode pour un rendu plus
     * complexe (sprites, animations, particules, etc.).</p>
     *
     * @param g contexte graphique utilisé pour dessiner
     */
    public void render(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.drawRect((int) x, (int) y, (int) width, (int) height);
    }

    // -----------------------
    // Getters / Setters de base
    // -----------------------

    /** @return position X de l'entité */
    public double getX() {
        return x;
    }

    /** @return position Y de l'entité */
    public double getY() {
        return y;
    }

    /** @return largeur de l'entité (en pixels) */
    public double getWidth() {
        return width;
    }

    /** @return hauteur de l'entité (en pixels) */
    public double getHeight() {
        return height;
    }

    /**
     * @return position Y du bas de l'entité (utile pour les collisions avec le sol)
     */
    public double getBottom() {
        return y + height;
    }

    /**
     * @return position X du centre de l'entité (utile pour la visée et le placement précis)
     */
    public double getCenterX() {
        return x + width / 2.0;
    }

    /**
     * @return position Y du centre de l'entité
     */
    public double getCenterY() {
        return y + height / 2.0;
    }
}
