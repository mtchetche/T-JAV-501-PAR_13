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

    private final Font fontTitle = new Font("Consolas", Font.BOLD, 18);
    private final Font fontText = new Font("Consolas", Font.PLAIN, 16);

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
    public void render(Graphics2D g, int killCount, Inventory inventory, boolean hasAk47) {

        // Position du bloc HUD
        int x = 20;
        int y = 20;
        int width = 180;
        int height = 100;

        // Fond translucide
        g.setColor(new Color(0, 0, 0, 140)); // semi-transparent
        g.fillRoundRect(x, y, width, height, 12, 12);

        int textX = x + 12;
        int textY = y + 28;

        // --- TITRE ---
        g.setColor(Color.WHITE);
        g.setFont(fontTitle);
        g.drawString("HUD", textX, textY);

        textY += 25;

        g.setFont(fontText);

        // --- KILLS ---
        g.drawString("Kills : " + killCount, textX, textY);
        textY += 22;

        // --- TIMER(S) ---
        int timerCount = (inventory != null) ? inventory.count(ItemType.TIMER) : 0;
        g.drawString("Timer : " + timerCount, textX, textY);
        textY += 22;

        // --- AK47 ---
        g.drawString("AK47 : " + (hasAk47 ? "Oui" : "Non"), textX, textY);
    }
}
