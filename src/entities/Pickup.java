package src.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import src.core.SoundManager;

/**
 * Représente un pickup générique dans le jeu (bonus).
 *
 * <p>Un pickup possède :</p>
 * <ul>
 *     <li>une position (x, y)</li>
 *     <li>une taille fixe de 32x32</li>
 *     <li>une durée de vie maximale de 20 secondes</li>
 *     <li>un système de clignotement dans les 5 dernières secondes</li>
 *     <li>une collision avec le joueur</li>
 *     <li>une expiration naturelle qui déclenche un son</li>
 * </ul>
 *
 * <p>Les classes concrètes doivent implémenter :</p>
 * <ul>
 *     <li>{@link #onPickup(Player)} pour appliquer l’effet du bonus</li>
 *     <li>{@link #render(Graphics2D)} pour dessiner le bonus</li>
 * </ul>
 */
public abstract class Pickup {

    /** Position X du pickup dans le monde */
    protected double x;

    /** Position Y du pickup dans le monde */
    protected double y;

    /** Taille fixe d’un pickup (32x32 pixels) */
    protected final int size = 32;

    /** True si le pickup est encore actif (non ramassé et non expiré) */
    protected boolean active = true;

    /** Durée maximale de vie en secondes avant expiration */
    protected final double maxLifetime = 20.0;

    /** Temps écoulé depuis la création du pickup */
    protected double lifetime = 0.0;

    /**
     * Constructeur d’un pickup générique.
     *
     * @param x position horizontale du pickup
     * @param y position verticale du pickup
     */
    public Pickup(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Met à jour le pickup :
     * <ul>
     *     <li>incrémente le timer interne</li>
     *     <li>le fait expirer au bout de 20 secondes</li>
     *     <li>teste la collision avec le joueur</li>
     * </ul>
     *
     * @param dt     temps écoulé depuis la dernière frame (en secondes)
     * @param player joueur à tester pour une éventuelle collision
     */
    public void update(double dt, Player player) {
        if (!active) return;

        lifetime += dt;

        // Expiration naturelle
        if (lifetime >= maxLifetime) {
            active = false;
            SoundManager.playPickupExpire();
            return;
        }

        // Collision avec le joueur
        if (player != null && player.getRect().intersects(getBounds())) {
            onPickup(player);
            active = false;
        }
    }

    /**
     * Méthode appelée lorsque le joueur ramasse ce pickup.
     *
     * <p>Chaque sous-classe doit définir l’effet :</p>
     * <ul>
     *     <li>BonusHealth : +1 point de vie</li>
     *     <li>BonusShield : active un bouclier temporaire</li>
     *     <li>BonusSpeed : augmente la vitesse temporairement</li>
     * </ul>
     *
     * @param player joueur ayant ramassé le bonus
     */
    protected abstract void onPickup(Player player);

    /**
     * Indique si le pickup doit clignoter (pendant les 5 dernières secondes).
     *
     * @return true si le pickup est dans la phase de clignotement
     */
    protected boolean shouldFlash() {
        double remaining = maxLifetime - lifetime;
        return remaining <= 5.0;
    }

    /**
     * Détermine si le pickup doit être visible lors de cette frame.
     * Permet un clignotement toutes les 150 ms.
     *
     * @return true si le sprite doit être dessiné
     */
    protected boolean isVisible() {
        if (!shouldFlash()) return true;

        double tMs = lifetime * 1000.0;   // en ms
        int phase = (int)(tMs / 150.0);   // changement toutes les 150ms
        return (phase % 2) == 0;          // 1 frame sur 2 visible
    }

    /**
     * Retourne le rectangle de collision du pickup.
     *
     * @return un Rectangle représentant la zone du pickup
     */
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }

    /**
     * Indique si le pickup est encore actif.
     *
     * @return true si le bonus est disponible
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Méthode de rendu du pickup.
     * <p>IMPORTANT : commencer l’implémentation par :</p>
     *
     * <pre>
     *     if (!isVisible()) return;
     * </pre>
     *
     * @param g contexte graphique utilisé pour dessiner
     */
    public abstract void render(Graphics2D g);
}
