package src.world;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * Représente un niveau du jeu contenant l'ensemble des plateformes.
 * <p>
 * La classe initialise toutes les plateformes fixes du décor
 * (sol, côtés gauche et droit, plateformes centrales, etc.).
 * Ces plateformes sont utilisées pour les collisions des entités
 * (joueur, ennemis, projectiles, items...).
 * </p>
 */
public class Level {

    /** Liste de toutes les plateformes présentes dans le niveau. */
    private final ArrayList<Platform> platforms = new ArrayList<>();

    /**
     * Construit le niveau en ajoutant toutes les plateformes prédéfinies
     * à leurs positions respectives.
     * <p>
     * Ces plateformes sont statiques et servent de base au gameplay :
     * zones de déplacement, obstacles, supports pour les ennemis et le joueur.
     * </p>
     */
    public Level() {

        // -------- SOL --------
        platforms.add(new Platform(0, 685, 1280, 40));

        // -------- LEFT --------
        platforms.add(new Platform(0, 260, 280, 25));
        platforms.add(new Platform(0, 295, 220, 25));
        platforms.add(new Platform(0, 330, 160, 25));

        // -------- CENTER LEFT --------
        platforms.add(new Platform(285, 350, 200, 25));
        platforms.add(new Platform(285, 600, 200, 25));

        // -------- CENTER --------
        platforms.add(new Platform(500, 490, 200, 25));

        // -------- CENTER RIGHT --------
        platforms.add(new Platform(730, 350, 200, 25));
        platforms.add(new Platform(730, 600, 200, 25));

        // -------- RIGHT --------
        platforms.add(new Platform(980, 260, 280, 25));
        platforms.add(new Platform(1040, 295, 220, 25));
        platforms.add(new Platform(1100, 330, 160, 25));
    }

    /**
     * Retourne la liste des plateformes du niveau.
     *
     * @return Liste des plateformes utilisées pour les collisions.
     */
    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    /**
     * Affiche toutes les plateformes du niveau.
     *
     * @param g Contexte graphique 2D.
     */
    public void render(Graphics2D g) {
        for (Platform p : platforms) {
            p.render(g);
        }
    }
}
