package src.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

/**
 * Utilitaire permettant de charger des sprites depuis différents emplacements.
 * <p>
 * Le chargement se fait selon deux méthodes :
 * </p>
 * <ul>
 *     <li><strong>Depuis le classpath</strong> : utile lorsque les ressources sont intégrées
 *         dans un fichier JAR ou placées dans le dossier <code>resources/</code>.</li>
 *     <li><strong>Depuis le système de fichiers</strong> : utile lors du développement,
 *         lorsque les assets sont directement dans un dossier du projet.</li>
 * </ul>
 *
 * <p>En cas d'erreur, la méthode affiche un message d'avertissement dans la console et retourne
 * <code>null</code>.</p>
 */
public class SpriteLoader {

    /**
     * Charge une image depuis un chemin donné.
     * <p>
     * Le chemin est tenté de deux manières :
     * </p>
     * <ol>
     *     <li>Lecture via le classpath (utilisation de <code>getResourceAsStream()</code>).</li>
     *     <li>Lecture via un fichier local sur le disque.</li>
     * </ol>
     *
     * @param path Chemin du sprite (ex : <code>"assets/player.png"</code>)
     * @return Une image <code>BufferedImage</code> si le chargement réussit, sinon <code>null</code>.
     */
    public static BufferedImage load(String path) {
        try {
            // 1) Tentative via resources
            InputStream is = SpriteLoader.class.getClassLoader().getResourceAsStream(path);
            if (is != null)
                return ImageIO.read(is);

            // 2) Tentative via disque
            return ImageIO.read(new File(path));

        } catch (IOException e) {
            System.err.println("ERREUR : impossible de charger " + path);
            return null;
        }
    }
}
