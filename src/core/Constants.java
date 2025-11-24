package src.core;

/**
 * Classe centrale contenant toutes les constantes globales du jeu.
 * 
 * Le but de cette classe est de regrouper les paramètres essentiels de la fenêtre,
 * des performances et autres valeurs fixes pour éviter la duplication dans le code.
 * 
 * Toute modification globale (ex : changer les FPS, la taille de l’écran)
 * se fait ici.
 */
public class Constants {

    /** Largeur de la fenêtre du jeu en pixels */
    public static final int WINDOW_WIDTH = 1280;

    /** Hauteur de la fenêtre du jeu en pixels */
    public static final int WINDOW_HEIGHT = 720;

    /** FPS cible (images par seconde) */
    public static final int TARGET_FPS = 60;

    /** Durée d’une frame en nanosecondes (utilisée pour le timing précis) */
    public static final long FRAME_DURATION_NS = 1_000_000_000L / TARGET_FPS;

    /** Nom affiché dans la barre de titre de la fenêtre Java */
    public static final String WINDOW_TITLE = "SYNTAX ERROR 2D - Vague 1 Prototype";
}
