package src.core;

import java.awt.*;

/**
 * Représente un bonus (pickup) présent sur le terrain.
 *
 * <p>Les pickups ne sont pas stockables dans l'inventaire :
 * ils sont consommés immédiatement lorsqu'un joueur entre en collision
 * avec eux.</p>
 *
 * <p>Types de pickups disponibles :</p>
 * <ul>
 *     <li><b>HEART</b> : rend 1 point de vie.</li>
 *     <li><b>SHIELD</b> : ajoute un bouclier temporaire.</li>
 *     <li><b>SPEED</b> : confère une augmentation temporaire de vitesse.</li>
 * </ul>
 *
 * <p>Tous les pickups disparaissent automatiquement au bout de 20 secondes.
 * Dans les 5 dernières secondes, ils commencent à clignoter
 * (géré via {@link #shouldFlash()}).</p>
 */
public class Pickup {

    /**
     * Types de pickups présents sur la carte.
     */
    public enum PickupType {
        HEART,
        SHIELD,
        SPEED
        // FLAME supprimé du gameplay
    }

    /** Position X du pickup dans le monde. */
    private double x;

    /** Position Y du pickup dans le monde. */
    private double y;

    /** Taille du carré représentant le pickup (32x32). */
    private final int size = 32;

    /** Type du pickup (HEART, SHIELD, SPEED). */
    private final PickupType type;

    /** Indique si le pickup est encore actif dans le monde. */
    private boolean alive = true;

    /** Durée maximale de vie du pickup, en secondes. */
    private final double maxLifetime = 20.0;

    /** Temps écoulé depuis l'apparition du pickup. */
    private double lifetime = 0.0;

    /**
     * Durée d'effet du pickup après consommation.
     * (utile pour SHIELD ou SPEED, sans effet pour HEART)
     */
    private final double duration;

    /**
     * Crée un pickup avec une durée d'effet nulle (cas HEART).
     *
     * @param x position X
     * @param y position Y
     * @param type type de pickup
     */
    public Pickup(double x, double y, PickupType type) {
        this(x, y, type, 0.0);
    }

    /**
     * Crée un pickup avec une durée d'effet définie.
     *
     * @param x position X
     * @param y position Y
     * @param type type du pickup
     * @param duration durée de l'effet après consommation (en secondes)
     */
    public Pickup(double x, double y, PickupType type, double duration) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.duration = duration;
    }

    /**
     * Met à jour la durée de vie du pickup.
     *
     * @param dt temps écoulé depuis la dernière mise à jour (en secondes)
     */
    public void update(double dt) {
        if (!alive) return;

        lifetime += dt;
        if (lifetime >= maxLifetime) {
            alive = false;
        }
    }

    /**
     * Indique si le pickup doit être rendu en mode clignotant,
     * ce qui se produit durant les 5 dernières secondes de son existence.
     *
     * @return true si le pickup doit clignoter
     */
    public boolean shouldFlash() {
        double remaining = maxLifetime - lifetime;
        return remaining <= 5.0;
    }

    /**
     * Détermine si le pickup intersecte un rectangle donné (par ex. la hitbox du joueur).
     *
     * @param px position X du rectangle
     * @param py position Y du rectangle
     * @param pw largeur du rectangle
     * @param ph hauteur du rectangle
     * @return true si une collision a lieu
     */
    public boolean intersects(double px, double py, double pw, double ph) {
        return px < x + size &&
                px + pw > x &&
                py < y + size &&
                py + ph > y;
    }

    /** @return true si le pickup est encore actif */
    public boolean isAlive() { return alive; }

    /** @return type du pickup */
    public PickupType getType() { return type; }

    /** @return durée d'effet après consommation */
    public double getDuration() { return duration; }

    /** @return position X du pickup */
    public double getX() { return x; }

    /** @return position Y du pickup */
    public double getY() { return y; }

    /** @return taille du carré du pickup */
    public int getSize() { return size; }
}
