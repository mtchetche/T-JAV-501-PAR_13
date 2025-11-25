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
 * Format supporté : WAV natif, MP3 via système (afplay/paplay/aplay).
 */
public class SoundManager {

    private static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");
    private static final boolean IS_LINUX = System.getProperty("os.name").toLowerCase().contains("linux");
    private static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().contains("win");

    /**
     * Joue un son ponctuel (non bouclé).
     * <p>
     * Supporte WAV (natif) et MP3 (via système d'exploitation).
     * </p>
     *
     * @param path nom du fichier audio (recherché dans <code>assets/sound/</code>)
     */
    public static void playSound(String path) {
        try {
            File file = resolveAssetFile(path);
            
            if (file.getName().toLowerCase().endsWith(".wav")) {
                playSoundViaJava(file);
            } else if (file.getName().toLowerCase().endsWith(".mp3")) {
                playSoundViaSystem(file);
            } else {
                System.out.println("[SOUND] Format non supporté : " + file.getName());
            }
        } catch (Exception e) {
            System.out.println("Erreur son (" + path + ") : " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * Joue un son WAV via l'API Java Audio (AudioSystem).
     */
    private static void playSoundViaJava(File file) {
        try {
            final AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            final Clip clip = AudioSystem.getClip();

            // Fermer automatiquement le clip et le stream une fois le son terminé
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
                    clip.close();
                    try {
                        audioIn.close();
                    } catch (Exception ignored) { }
                }
            });

            clip.open(audioIn);
            clip.start();
            System.out.println("[SOUND] WAV started: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Erreur WAV : " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    /**
     * Joue un son MP3 via le lecteur système (afplay sur macOS, paplay sur Linux, etc).
     */
    private static void playSoundViaSystem(File file) {
        new Thread(() -> {
            try {
                String[] cmd;
                if (IS_MAC) {
                    cmd = new String[]{"afplay", file.getAbsolutePath()};
                } else if (IS_LINUX) {
                    cmd = new String[]{"paplay", file.getAbsolutePath()};
                } else if (IS_WINDOWS) {
                    cmd = new String[]{"powershell", "-c", "(New-Object System.Media.SoundPlayer).PlaySync('" + file.getAbsolutePath() + "')"};
                } else {
                    System.out.println("[SOUND] Système non reconnu pour MP3");
                    return;
                }
                
                Process process = Runtime.getRuntime().exec(cmd);
                process.waitFor();
                System.out.println("[SOUND] MP3 played: " + file.getAbsolutePath());
            } catch (Exception e) {
                System.out.println("Erreur lecture MP3 : " + e.getMessage());
            }
        }).start();
    }

    /** Clip utilisé pour la musique de fond. */
    private static Clip musicClip;
    private static Process musicProcess;
    private static boolean musicShouldPlay = false;
    private static Thread musicThread;

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
            // Stopper et fermer la musique précédente si nécessaire
            stopMusic();

            File file = resolveAssetFile(path);
            
            if (file.getName().toLowerCase().endsWith(".wav")) {
                playMusicViaJava(file);
            } else if (file.getName().toLowerCase().endsWith(".mp3")) {
                playMusicViaSystem(file);
            } else {
                System.out.println("[SOUND] Format non supporté : " + file.getName());
            }
        } catch (Exception e) {
            System.out.println("Erreur musique (" + path + ") : " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    /**
     * Joue une musique WAV en boucle via AudioSystem.
     */
    private static void playMusicViaJava(File file) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();
            System.out.println("[SOUND] WAV music loop started: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Erreur WAV music : " + e.getMessage());
            e.printStackTrace(System.out);
        }
    }

    /**
     * Joue une musique MP3 en boucle via système (avec redémarrage auto).
     */
    private static void playMusicViaSystem(File file) {
        musicShouldPlay = true;
        musicThread = new Thread(() -> {
            while (musicShouldPlay) {
                try {
                    String[] cmd;
                    if (IS_MAC) {
                        cmd = new String[]{"afplay", file.getAbsolutePath()};
                    } else if (IS_LINUX) {
                        cmd = new String[]{"paplay", file.getAbsolutePath()};
                    } else if (IS_WINDOWS) {
                        cmd = new String[]{"powershell", "-c", "(New-Object System.Media.SoundPlayer).PlaySync('" + file.getAbsolutePath() + "')"};
                    } else {
                        return;
                    }
                    
                    musicProcess = Runtime.getRuntime().exec(cmd);
                    musicProcess.waitFor();
                    System.out.println("[SOUND] MP3 music loop restarted");
                } catch (Exception e) {
                    System.out.println("Erreur boucle musique MP3 : " + e.getMessage());
                    break;
                }
            }
        });
        musicThread.setDaemon(true);
        musicThread.start();
        System.out.println("[SOUND] MP3 music loop started: " + file.getAbsolutePath());
    }

    /**
     * Recherche le fichier audio dans `assets/sound/<path>` en essayant
     * le répertoire courant puis en remontant jusqu'à 4 niveaux parents.
     * Renvoie le File trouvé ou lance FileNotFoundException si introuvable.
     */
    private static File resolveAssetFile(String path) throws java.io.FileNotFoundException {
        String rel = "assets/sound/" + path;
        
        // 1. Essayer d'abord le chemin relatif simple
        File f = new File(rel);
        if (f.exists()) {
            System.out.println("[SOUND] Trouvé (chemin relatif): " + f.getAbsolutePath());
            return f;
        }

        // 2. Essayer en remontant les répertoires parents
        File cwd = new File(System.getProperty("user.dir"));
        System.out.println("[SOUND] Working dir: " + cwd.getAbsolutePath());
        
        File cur = cwd;
        for (int i = 0; i < 6; i++) {
            File candidate = new File(cur, rel);
            System.out.println("[SOUND] En essai (niveau " + i + "): " + candidate.getAbsolutePath());
            if (candidate.exists()) {
                System.out.println("[SOUND] ✓ Trouvé!");
                return candidate;
            }
            cur = cur.getParentFile();
            if (cur == null) break;
        }

        // 3. Essayer depuis la racine en cherchant le dossier T-JAV-501-PAR_13
        try {
            java.nio.file.Path start = cwd.toPath();
            java.util.Optional<java.nio.file.Path> found = java.nio.file.Files.walk(start, 6)
                    .filter(p -> p.toString().endsWith("assets" + java.io.File.separator + "sound"))
                    .findFirst();
            if (found.isPresent()) {
                File candidate = new File(found.get().toFile(), path);
                if (candidate.exists()) {
                    System.out.println("[SOUND] ✓ Trouvé (walk): " + candidate.getAbsolutePath());
                    return candidate;
                }
            }
        } catch (Exception ignored) { }

        System.out.println("[SOUND] ✗ NON TROUVÉ: " + rel);
        throw new java.io.FileNotFoundException("Audio file not found: " + rel + " (searched from " + System.getProperty("user.dir") + ")");
    }

    /**
     * Arrête la musique actuellement en cours de lecture.
     * <p>
     * N'a aucun effet si aucune musique n'est active.
     * </p>
     */
    public static void stopMusic() {
        // Signaler au thread de musique de s'arrêter
        musicShouldPlay = false;
        
        // Arrêter le clip WAV
        if (musicClip != null && musicClip.isOpen()) {
            try { 
                musicClip.stop(); 
                musicClip.close();
            } catch (Exception ignored) {}
        }
        
        // Arrêter le processus MP3
        if (musicProcess != null) {
            try {
                musicProcess.destroyForcibly();
            } catch (Exception ignored) {}
        }
        
        System.out.println("[SOUND] Music stopped");
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
        // SoundManager.playSound("bonus.wav");
        System.out.println("[SOUND] Bonus expiré");
    }

}
