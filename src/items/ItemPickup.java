package src.items;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import src.core.ItemType;
import src.core.SoundManager;
import src.util.SpriteLoader;

/**
 * Représente un objet ramassable (item) posé au sol.
 *
 * <p>Deux types d’items sont gérés :</p>
 * <ul>
 *     <li><b>TIMER</b> :
 *         <ul>
 *             <li>Disparaît automatiquement après 20 secondes.</li>
 *             <li>Clignote durant les 5 dernières secondes.</li>
 *             <li>Joue un son lorsque le timer arrive à expiration.</li>
 *         </ul>
 *     </li>
 *     <li><b>AK47</b> :
 *         <ul>
 *             <li>Ne disparaît jamais naturellement.</li>
 *             <li>Ne clignote jamais.</li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * <p>L’item peut être rendu, mis à jour et détecté via un rectangle de collision.
 * Une fois ramassé, il est désactivé (alive = false).</p>
 *
 * @see ItemType
 * @see SpriteLoader
 * @see SoundManager
 */
public class ItemPickup {

    private final ItemType type;
    private double x, y;
    private final int size = 32;

    private boolean alive = true;

    // Durée de vie (pour TIMER uniquement)
    private final boolean expires;
    private final double maxLifetime; // 20s pour TIMER, 0 pour AK47
    private double lifetime = 0.0;
    private boolean expirationSoundPlayed = false;

    // Sprites
    private static final BufferedImage timerSprite =
            SpriteLoader.load("assets/items/timer.png");

    private static final BufferedImage akSprite =
            SpriteLoader.load("assets/items/ak47.png");

    /**
     * Crée un item ramassable au sol.
     *
     * @param x     Position X dans le monde
     * @param y     Position Y dans le monde
     * @param type  Type de l’item (TIMER ou AK47)
     */
    public ItemPickup(double x, double y, ItemType type) {
        this.x = x;
        this.y = y;
        this.type = type;

        if (type == ItemType.TIMER) {
            this.expires = true;
            this.maxLifetime = 20.0;
        } else {
            this.expires = false;
            this.maxLifetime = 0.0;
        }
    }

    /**
     * Met à jour l’état de l’item.
     *
     * <p>Pour les items temporaires (TIMER) :</p>
     * <ul>
     *     <li>Incrémente le timer interne.</li>
     *     <li>Désactive l’item lorsqu’il expire.</li>
     *     <li>Joue un son d’expiration la première fois qu’il disparaît.</li>
     * </ul>
     *
     * @param dt Temps écoulé depuis la dernière frame (en secondes)
     */
    public void update(double dt) {
        if (!alive) return;

        if (expires) {
            lifetime += dt;

            if (lifetime >= maxLifetime) {
                alive = false;
                if (!expirationSoundPlayed) {
                    expirationSoundPlayed = true;
                    SoundManager.playPickupExpire();
                }
            }
        }
    }

    /**
     * Indique si l’item doit clignoter.
     *
     * @return true si le type est TIMER et qu’il reste moins de 5 secondes
     */
    private boolean shouldFlash() {
        if (!expires) return false; // AK47 ne clignote jamais
        double remaining = maxLifetime - lifetime;
        return remaining <= 5.0;
    }

    /**
     * Indique si l’item doit être visible sur cette frame.
     *
     * <p>Lorsque l’item est en phase de clignotement :<br>
     * visible 150 ms / invisible 150 ms.</p>
     *
     * @return true si l’item doit être affiché à l’écran
     */
    private boolean isVisible() {
        if (!shouldFlash()) return true;

        double tMs = lifetime * 1000.0;
        int phase = (int)(tMs / 150.0);
        return (phase % 2) == 0;
    }

    /**
     * Dessine l’item à l’écran s’il est actif et visible.
     *
     * @param g Contexte graphique (Graphics2D)
     */
    public void render(Graphics2D g) {
        if (!alive) return;
        if (!isVisible()) return;

        BufferedImage sprite = (type == ItemType.TIMER) ? timerSprite : akSprite;

        g.drawImage(sprite, (int)x, (int)y, size, size, null);
    }

    /**
     * Retourne le rectangle de collision de l’item.
     *
     * @return Rectangle utilisé pour détecter la prise d’item
     */
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }

    /**
     * Indique si l’item est toujours actif (non ramassé et non expiré).
     *
     * @return true si l’item est encore présent dans le monde
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Désactive l’item (utilisé lorsque le joueur le ramasse).
     */
    public void kill() {
        this.alive = false;
    }

    /**
     * Retourne le type de l’item.
     *
     * @return type de l’item (TIMER ou AK47)
     */
    public ItemType getType() {
        return type;
    }
}

