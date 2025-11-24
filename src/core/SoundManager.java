package src.core;

import javax.sound.sampled.*;
import java.io.File;

/**
 * Gestionnaire centralisé pour la lecture des sons et musiques du jeu.
 * <p>
 * Cette classe propose des méthodes statiques permettant :
 * <ul>
 *     <li>de jouer un son ponctuel (tir, impact, pickup…)</li>
 *     <li>de lancer une musique de fond en boucle</li>
 *     <li>d'arrêter la musique en cours</li>
 *     <li>d'exécuter des sons système (ex : expiration d'un bonus)</li>
 * </ul>
 * Les fichiers audio doivent se trouver dans le dossier :
 * <pre>
 *     assets/sound/
 * </pre>
 * et être au format compatible avec {@link AudioSystem} (souvent WAV).
 */
public class SoundManager {

    /**
     * Joue un son ponctuel (non bouclé).
     * <p>
     * Cette méthode est utilisée pour les effets courts :
     * tirs, collisions, ramassage d'objets, explosions, etc.
     * </p>
     *
     * @param path nom du fichier audio (recherché dans <code>assets/sound/</code>)
     */
    public static void playSound(String path) {
        try {
            File file = new File("assets/sound/" + path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.out.println("Erreur son : " + e.getMessage());
        }
    }

    /** Clip utilisé pour la musique de fond. */
    private static Clip musicClip;

    /**
     * Lance une musique de fond en boucle infinie.
     * <p>
     * Si une musique était déjà en cours, elle est remplacée.
     * </p>
     *
     * @param path nom du fichier audio à jouer (dans <code>assets/sound/</code>)
     */
    public static void playMusic(String path) {
        try {
            File file = new File("assets/sound/" + path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
        } catch (Exception e) {
            System.out.println("Erreur musique : " + e.getMessage());
        }
    }

    /**
     * Arrête la musique actuellement en cours de lecture.
     * <p>
     * N'a aucun effet si aucune musique n'est active.
     * </p>
     */
    public static void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
        }
    }

    /**
     * Joue ou simule un son indiquant qu'un bonus a expiré.
     * <p>
     * Pour un vrai son, décommente et ajoute un fichier
     * dans <code>assets/sound/</code>.
     * </p>
     */
    public static void playPickupExpire() {
        // Exemple :
        // SoundManager.playSound("bonus/expire.wav");
        System.out.println("[SOUND] Bonus expiré");
    }

}
