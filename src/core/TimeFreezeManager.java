package src.core;

/**
 * Gère l'effet de gel du temps (Timer).
 * <p>
 * Lorsque le temps est gelé :
 * <ul>
 *     <li>les ennemis ne s'update plus (dt = 0 pour eux)</li>
 *     <li>les projectiles ennemis sont figés</li>
 *     <li>les spawns d'ennemis continuent normalement</li>
 *     <li>le joueur continue de se déplacer et d'agir</li>
 *     <li>le temps global du jeu continue de s'écouler</li>
 * </ul>
 * Ce système permet de geler uniquement les ENNEMIS et les PROJECTILES ENNEMIS,
 * sans bloquer le reste de la logique du jeu.
 */
public class TimeFreezeManager {

    /** Indique si le temps est actuellement gelé. */
    private boolean frozen = false;

    /** Temps restant avant la fin de l'effet (en secondes). */
    private double freezeTime = 0.0;

    /**
     * Active le gel du temps pour une durée donnée.
     *
     * @param duration durée du gel en secondes (ignoré si &le; 0)
     */
    public void activate(double duration) {
        if (duration <= 0) return;
        frozen = true;
        freezeTime = duration;
    }

    /**
     * Met à jour le compte à rebours du gel du temps.
     * <p>
     * Cette méthode doit être appelée à chaque frame par la GameLoop.
     * </p>
     *
     * @param dt temps réel écoulé depuis la dernière frame
     */
    public void update(double dt) {
        if (!frozen) return;

        freezeTime -= dt;
        if (freezeTime <= 0) {
            frozen = false;
            freezeTime = 0;
        }
    }

    /**
     * @return {@code true} si le temps des ennemis est gelé
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * @return le temps restant avant la fin de l'effet (en secondes)
     */
    public double getRemainingFreezeTime() {
        return freezeTime;
    }
}
