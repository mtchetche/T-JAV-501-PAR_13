package src.core;

/**
 * Représente une vague d'ennemis avec une durée déterminée.
 * <p>
 * Une vague possède :
 * <ul>
 *     <li>une durée totale</li>
 *     <li>un temps écoulé</li>
 *     <li>un état "terminée" ou non</li>
 * </ul>
 * La classe ne gère pas le contenu de la vague (ennemis, spawns, etc.),
 * uniquement son chronomètre.
 */
public class Wave {

    /** Durée totale de la vague en secondes. */
    private double duration;

    /** Temps écoulé depuis le début de la vague. */
    private double timer = 0;

    /** Indique si la vague est terminée. */
    private boolean finished = false;

    /**
     * Crée une nouvelle vague d'une durée donnée.
     *
     * @param durationSeconds durée totale de la vague en secondes
     */
    public Wave(double durationSeconds) {
        this.duration = durationSeconds;
    }

    /**
     * Met à jour le timer de la vague.
     * <p>
     * Une fois la durée atteinte, la vague est marquée comme terminée.
     *
     * @param dt delta time réel depuis la frame précédente
     */
    public void update(double dt) {
        if (finished)
            return;

        timer += dt;
        if (timer >= duration)
            finished = true;
    }

    /**
     * @return {@code true} si la vague est terminée
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Retourne le temps restant avant la fin de la vague.
     *
     * @return temps restant en secondes (minimum 0)
     */
    public double getRemainingTime() {
        return Math.max(0, duration - timer);
    }
}
