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
    /** Liste de toutes les entités vivantes (joueur, ennemis, etc.) présentes dans le niveau. */
    private final ArrayList<src.entities.LivingEntity> entities = new ArrayList<>();

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
        platforms.add(new Platform(0, 720, 1280, 40));

        // -------- LEFT --------
        platforms.add(new Platform(0, 260, 280, 25));
        platforms.add(new Platform(0, 295, 220, 25));
        platforms.add(new Platform(0, 330, 160, 25));

        // -------- CENTER LEFT --------
        platforms.add(new Platform(285, 350, 200, 25));
        platforms.add(new Platform(285, 589, 200, 25));

        // -------- CENTER --------
        platforms.add(new Platform(500, 480, 200, 25));

        // -------- CENTER RIGHT --------
        platforms.add(new Platform(730, 350, 200, 25));
        platforms.add(new Platform(730, 590, 200, 25));

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

    /** Retourne la liste des entités vivantes du niveau. */
    public ArrayList<src.entities.LivingEntity> getEntities() {
        return entities;
    }

    /** Ajoute une entité vivante au niveau. */
    public void addEntity(src.entities.LivingEntity e) {
        entities.add(e);
    }

    /** Retire une entité vivante du niveau. */
    public void removeEntity(src.entities.LivingEntity e) {
        entities.remove(e);
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
