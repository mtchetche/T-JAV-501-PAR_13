package src.core;

/**
 * Types d'items stockables dans l'inventaire du joueur.
 *
 * <p>Chaque type d'item correspond à un effet géré ailleurs dans le jeu
 * (dans {@code Game}, {@code Player}, etc.). L'inventaire se contente
 * de stocker ces types.</p>
 *
 * <ul>
 *   <li><b>TIMER</b> : stoppe le temps pendant une durée définie.</li>
 *   <li><b>AK47</b> : donne au joueur une arme à distance.</li>
 * </ul>
 */
public enum ItemType {
    TIMER,
    AK47
}
