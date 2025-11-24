package src.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import src.core.SoundManager;
import src.util.SpriteLoader;

/**
 * Bonus de bouclier (BonusShield).
 *
 * <p>Ce bonus applique un effet de protection temporaire au joueur
 * lorsqu'il est ramassé. Il utilise le sprite {@code shield.png}.</p>
 *
 * <p>Le bouclier reste actif pendant une durée définie lors de la création
 * du bonus. L'affichage est géré uniquement lorsque le bonus est actif
 * et visible (gestion assurée par {@link Pickup}).</p>
 */
public class BonusShield extends Pickup {

    /** Sprite du bonus bouclier. */
    private static final BufferedImage sprite = SpriteLoader.load("assets/bonus/shield.png");

    /** Durée d’activation du bouclier une fois ramassé. */
    private final double shieldDuration;

    /**
     * Crée un bonus de bouclier aux coordonnées indiquées.
     *
     * @param x position horizontale
     * @param y position verticale
     * @param shieldDuration durée du bouclier une fois activé, en secondes
     */
    public BonusShield(double x, double y, double shieldDuration) {
        super(x, y);
        this.shieldDuration = shieldDuration;
    }

    /**
     * Action exécutée lorsque le joueur ramasse le bonus.
     *
     * <p>Effets :
     * <ul>
     *   <li>Joue un son de ramassage.</li>
     *   <li>Ajoute un bouclier au joueur pour {@code shieldDuration} secondes.</li>
     * </ul>
     *
     * @param player le joueur ramassant le bonus
     */
    @Override
    protected void onPickup(Player player) {
        SoundManager.playSound("son bonus/bonus.wav");
        player.addShield(shieldDuration);
    }

    /**
     * Affiche le sprite du bonus s’il est actif et visible.
     *
     * @param g contexte graphique de rendu
     */
    @Override
    public void render(Graphics2D g) {
        if (!isActive() || !isVisible())
            return;
        g.drawImage(sprite, (int) x, (int) y, size, size, null);
    }
}
