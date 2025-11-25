package src.world;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Représente une plateforme rectangulaire sur laquelle les entités
 * peuvent marcher, sauter ou entrer en collision.
 * <p>
 * Les plateformes définissent les surfaces solides du niveau.
 * Elles sont fixes et utilisées dans les calculs de collisions
 * pour le joueur, les ennemis ou d'autres objets.
 * </p>
 */
public class Platform {

    /** Position X de la plateforme. */
    public double x;

    /** Position Y de la plateforme. */
    public double y;

    /** Largeur de la plateforme. */
    public double width;

    /** Hauteur de la plateforme. */
    public double height;

    /**
     * Crée une nouvelle plateforme rectangulaire.
     *
     * @param x Position horizontale.
     * @param y Position verticale.
     * @param width Largeur de la plateforme.
     * @param height Hauteur de la plateforme.
     */
    public Platform(double x, double y, double width, double height) {
    this.x = x;
    this.y = y - 35;
    this.width = width;
    this.height = height;
    }

    /**
     * Vérifie si un rectangle donné entre en collision avec la plateforme.
     * <p>
     * La collision est détectée en testant le chevauchement
     * entre deux rectangles axis-aligned.
     * </p>
     *
     * @param px Position X du rectangle test.
     * @param py Position Y du rectangle test.
     * @param pwidth Largeur du rectangle test.
     * @param pheight Hauteur du rectangle test.
     * @return {@code true} si les deux rectangles se chevauchent.
     */
    public boolean intersects(double px, double py, double pwidth, double pheight) {
        return (px < x + width &&
                px + pwidth > x &&
                py < y + height &&
                py + pheight > y);
    }

    /**
     * Dessine la plateforme.
     * <p>
     * Comporte un corps gris clair et une bande foncée en haut
     * pour respecter le style visuel de la maquette.
     * </p>
     *
     * @param g Contexte graphique 2D.
     */
    public void render(Graphics2D g) {
        g.setColor(new Color(230,230,230));  // gris clair
        g.fillRect((int)x, (int)y, (int)width, (int)height);

        g.setColor(new Color(50,50,50));  // contour foncé (comme ta maquette)
        g.fillRect((int)x, (int)y - 8, (int)width, 8);
    }
}
