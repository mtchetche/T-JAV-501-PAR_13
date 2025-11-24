package src.entities;

/**
 * Représente les différents états possibles d'un ennemi dans le jeu.
 * <p>
 * Cette énumération est utilisée par les IA des différents types d'ennemis
 * (Enemy01, Enemy02, Enemy03...) afin de définir leur comportement courant.
 * Chaque implémentation interprète ces états selon sa logique propre.
 * </p>
 */
public enum EnemyState {

    /**
     * L’ennemi ne fait rien de particulier :
     * patrouille, attend, ou reste immobile.
     */
    IDLE,

    /**
     * L’ennemi poursuit activement le joueur.
     * Utilisé par la majorité des types d’ennemis.
     */
    CHASING,

    /**
     * L’ennemi prépare une charge.
     * Typiquement utilisé par Enemy01 et Enemy03.
     */
    CHARGING,

    /**
     * L’ennemi exécute une attaque de mêlée
     * lorsqu'il est au contact du joueur.
     */
    ATTACKING,

    /**
     * L’ennemi tire un projectile vers le joueur.
     * Utilisé par les ennemis à distance : Enemy02 et Enemy03.
     */
    SHOOTING,

    /**
     * L’ennemi fuit le joueur lorsqu’il est trop proche.
     * Utilisé principalement par Enemy02.
     */
    FLEEING,

    /**
     * L’ennemi attend la fin d’un délai entre deux actions
     * (après une attaque, un tir ou une charge).
     */
    COOLDOWN
}
