package src.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import src.core.SoundManager;
import src.util.SpriteLoader;

/**
 * Bonus de vitesse (BonusSpeed).
 *
 * <p>Ce bonus confère au joueur un boost temporaire de vitesse lorsqu'il
 * est ramassé. Il utilise le sprite {@code vitesse.png}.</p>
 *
 * <p>Le bonus est affiché uniquement tant qu'il est actif et visible
 * (gestion héritée de {@link Pickup}).</p>
 */
public class BonusSpeed extends Pickup {

    /** Sprite du bonus de vitesse. */
    private static final BufferedImage sprite = SpriteLoader.load("assets/bonus/vitesse.png");

    /** Durée de l'effet de vitesse appliqué au joueur. */
    private final double speedDuration;

    /**
     * Crée un bonus de vitesse aux coordonnées spécifiées.
     *
     * @param x position horizontale
     * @param y position verticale
     * @param speedDuration durée du boost de vitesse en secondes
     */
    public BonusSpeed(double x, double y, double speedDuration) {
        super(x, y);
        this.speedDuration = speedDuration;
    }

    /**
     * Action exécutée lorsque le joueur ramasse ce bonus.
     *
     * <p>Effets :
     * <ul>
     *     <li>Joue un son de ramassage.</li>
     *     <li>Applique un boost de vitesse pour {@code speedDuration} secondes.</li>
     * </ul>
     *
     * @param player le joueur ramassant le bonus
     */
    @Override
    protected void onPickup(Player player) {
        SoundManager.playSound("son bonus/bonus.wav");
        player.addSpeedBoost(speedDuration);
    }

    /**
     * Affiche le bonus à l'écran tant qu'il est actif et visible.
     *
     * @param g contexte graphique utilisé pour le rendu
     */
    @Override
    public void render(Graphics2D g) {
        if (!isActive() || !isVisible())
            return;
        g.drawImage(sprite, (int) x, (int) y, size, size, null);
    }
}
