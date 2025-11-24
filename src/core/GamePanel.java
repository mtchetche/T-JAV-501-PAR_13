package src.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Panneau graphique principal du jeu.
 *
 * <p>Cette classe étend {@link JPanel} et sert de "canvas" pour l'affichage.
 * Elle ne contient aucune logique de mise à jour (pas de boucle de jeu),
 * mais se contente de dessiner le contenu du jeu lorsque Swing demande un
 * rafraîchissement graphique via {@link #paintComponent(Graphics)}.</p>
 *
 * <p>Le rendu réel est entièrement délégué à l'objet {@link Game}, via
 * l'appel à {@link Game#render(Graphics2D)}.</p>
 *
 * <h2>Caractéristiques :</h2>
 * <ul>
 *     <li>Double-buffering activé pour éviter le scintillement</li>
 *     <li>Respect de la taille définie dans {@link Constants}</li>
 *     <li>Effacement de fond avant rendu personnalisé</li>
 * </ul>
 */
public class GamePanel extends JPanel {

    /** Référence vers l'instance principale du jeu. */
    private final Game game;

    /**
     * Constructeur du GamePanel.
     *
     * @param game instance du jeu à rendre graphiquement
     */
    public GamePanel(Game game) {
        this.game = game;

        setPreferredSize(new java.awt.Dimension(
                Constants.WINDOW_WIDTH,
                Constants.WINDOW_HEIGHT));
                
        setDoubleBuffered(true);
    }

    /**
     * Méthode appelée automatiquement par Swing lorsqu'il faut redessiner le panneau.
     *
     * <p>Cette méthode :
     * <ul>
     *     <li>efface le fond en gris</li>
     *     <li>convertit le {@link Graphics} en {@link Graphics2D}</li>
     *     <li>délègue entièrement le rendu au jeu via {@link Game#render(Graphics2D)}</li>
     * </ul>
     *
     * @param g contexte graphique fourni par Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(new Color(90, 90, 90));
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d = (Graphics2D) g;

        game.render(g2d);
    }
}
