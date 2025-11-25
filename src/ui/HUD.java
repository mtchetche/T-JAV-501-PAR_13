package src.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import src.core.Inventory;
import src.core.ItemType;

/**
 * Affiche le HUD du jeu (interface utilisateur minimaliste) contenant :
 * <ul>
 *   <li>Le nombre total de kills effectués.</li>
 *   <li>Le contenu de l'inventaire (notamment le nombre de TIMER).</li>
 *   <li>L'état de possession de l'AK47.</li>
 * </ul>
 *
 * <p>Le HUD apparaît dans un panneau semi-transparent affiché
 * en haut à gauche de l’écran.</p>
 */
public class HUD {

    private final Font fontTitle = new Font("Consolas", Font.BOLD, 28);
    private final Font fontText = new Font("Consolas", Font.PLAIN, 20);
    private final Font fontCounter = new Font("Consolas", Font.BOLD, 22);

    /**
     * Affiche l'ensemble des informations du HUD :
     * <ul>
     *   <li>Bloc semi-transparent contenant les informations</li>
     *   <li>Titre "HUD"</li>
     *   <li>Nombre de kills</li>
     *   <li>Nombre de TIMER dans l’inventaire</li>
     *   <li>Possession ou non de l'AK47</li>
     * </ul>
     *
     * @param g         Contexte graphique utilisé pour dessiner le HUD
     * @param killCount Nombre total d’ennemis éliminés
     * @param inventory Inventaire du joueur (peut être null)
     * @param hasAk47   Indique si le joueur possède une AK47
     */
    public void render(Graphics2D g, int killCount, Inventory inventory, boolean hasAk47, int waveNum, int elapsedSec, int remainSec) {
        int margin = 32;
        int y = 40;
        int xLeft = margin;
        int xRight = src.core.Constants.WINDOW_WIDTH - margin;

        // --- Infos Vague & Temps (aligné à gauche) ---
        g.setColor(Color.WHITE);
        g.setFont(fontTitle);
        g.drawString("VAGUE " + waveNum, xLeft, y);

        g.setFont(fontText);
        int xInfo = xLeft + 170;
        int minutes = elapsedSec / 60;
        int seconds = elapsedSec % 60;
        g.drawString("Temps Total : " + String.format("%02d:%02d", minutes, seconds), xInfo, y);

        int xRemain = xInfo + 220;
        int rMin = remainSec / 60;
        int rS = remainSec % 60;
        g.drawString("Temps Restant : " + String.format("%02d:%02d", rMin, rS), xRemain, y);

        // --- Items récupérés (aligné à droite, row) ---
        int iconSize = 32;
        int gap = 18;
        int xItem = xRight;

        // AK47 (affiché uniquement si récupéré)
        if (hasAk47) {
            java.awt.Image akImg = src.util.SpriteLoader.load("assets/items/ak47.png");
            if (akImg != null)
                g.drawImage(akImg, xItem - iconSize, y - iconSize/2, iconSize, iconSize, null);
            xItem -= iconSize + gap;
        }

        // Timer
        int timerCount = (inventory != null) ? inventory.count(ItemType.TIMER) : 0;
        g.setFont(fontCounter);
        g.drawString(String.valueOf(timerCount), xItem - iconSize - 28, y + 8);
        java.awt.Image timerImg = src.util.SpriteLoader.load("assets/items/timer.png");
        if (timerImg != null)
            g.drawImage(timerImg, xItem - iconSize, y - iconSize/2, iconSize, iconSize, null);
        xItem -= iconSize + gap + 32;

        // Kills
        g.setFont(fontCounter);
        g.drawString(String.valueOf(killCount), xItem - iconSize - 28, y + 8);
        java.awt.Image killImg = src.util.SpriteLoader.load("assets/items/kill.png");
        if (killImg != null)
            g.drawImage(killImg, xItem - iconSize, y - iconSize/2, iconSize, iconSize, null);
        xItem -= iconSize + gap + 32;
    }
}
