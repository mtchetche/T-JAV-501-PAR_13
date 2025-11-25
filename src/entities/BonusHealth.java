package src.entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import src.util.SpriteLoader;
import src.core.SoundManager;

/**
 * Représente un bonus de soin (BonusHealth).
 *
 * <p>Ce bonus utilise l'image {@code heart.png} et,
 * lorsqu’il est ramassé par le joueur, il joue un son et
 * restaure 1 point de vie en appelant {@code player.takeDamage(-1)}.</p>
 *
 * <p>Le bonus est affiché uniquement lorsqu’il est actif et visible.</p>
 */
public class BonusHealth extends Pickup {

    private static final BufferedImage sprite = SpriteLoader.load("assets/bonus/heart.png");

    /**
     * Crée un bonus de vie aux coordonnées spécifiées.
     *
     * @param x position horizontale du bonus
     * @param y position verticale du bonus
     */
    public BonusHealth(double x, double y) {
        super(x, y);
    }

    /**
     * Action exécutée lorsque le joueur ramasse ce bonus.
     *
     * <p>Effets :
     * <ul>
     *   <li>Joue un son de bonus.</li>
     *   <li>Rend 1 point de vie au joueur.</li>
     * </ul>
     *
     * @param player le joueur ramassant l'objet
     */
    @Override
    protected void onPickup(Player player) {
        SoundManager.playSound("son bonus/bonus.wav");
        player.takeDamage(-1);
    }

    /**
     * Affiche le sprite du bonus si celui-ci est actif et visible.
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
