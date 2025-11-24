package src.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Cette classe gère toutes les entrées clavier du jeu.
 * Elle stocke dans un tableau l'état de chaque touche du clavier (enfoncée / relâchée).
 * 
 * Le Player (et d’autres entités dans le futur) pourront venir lire ces états
 * pour savoir quoi faire : se déplacer, sauter, attaquer, etc.
 * 
 * Cette classe sera installée comme "KeyListener" sur la fenêtre du jeu.
 */
public class KeyboardInput implements KeyListener {

    /**
     * Tableau d'états des touches.
     * 
     * keys[code] = true  → la touche est actuellement enfoncée
     * keys[code] = false → la touche est relâchée
     * 
     * On utilise 256 car c'est suffisant pour toutes les touches utiles.
     */
    private final boolean[] keys = new boolean[256];

    /**
     * Permet à d’autres classes (comme Player) de demander si une touche est enfoncée.
     * 
     * @param keyCode Code de la touche (ex : KeyEvent.VK_LEFT)
     * @return true si la touche est actuellement maintenue
     */
    public boolean isKeyDown(int keyCode) {
        if (keyCode < 0 || keyCode >= keys.length) return false;
        return keys[keyCode];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < keys.length) {
            keys[code] = true;   // La touche vient d’être enfoncée
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code >= 0 && code < keys.length) {
            keys[code] = false;  // La touche vient d’être relâchée
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Non utilisé pour le gameplay → laissé vide volontairement
    }
}
